package com.hellofit.hellofit_server.chat;

import com.hellofit.hellofit_server.chat.dto.ChatDtos;
import com.hellofit.hellofit_server.chat.sse.ChatSseHub;
import com.hellofit.hellofit_server.llm.LlmClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@Validated
@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
@Slf4j
/**
 * Chat REST Controller
 *
 * Notes
 * - 이 컨트롤러는 HTTP 기반의 채팅 관련 API와 SSE 스트림을 제공합니다.
 *
 * Endpoints
 * - GET /chat/history: 채팅 히스토리 조회(커서 기반, 최신순 정렬)
 * - POST /chat/message: 사용자 메시지를 등록(큐잉)하고 메시지 ID를 반환
 * - POST /chat/session: 채팅 세션을 생성하고 세션 ID(cs_*)를 반환
 */
public class ChatController {

    private final ChatService chatService;
    private final ChatSseHub chatSseHub;
    private final LlmClient llmClient;

    /**
     * GET /chat/history
     */
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/history")
    public ResponseEntity<ChatDtos.HistoryResponse> history(
        Authentication auth,
        @RequestParam(required = false) String cursorId,
        @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size
    ) {
        UUID userId = (UUID) auth.getPrincipal();
        log.debug("[REST] GET /chat/history userId={} cursorId={} size={}", userId, cursorId, size);
        ChatDtos.HistoryResponse body = chatService.getHistory(userId, cursorId, size);
        return ResponseEntity.ok(body);
    }

    /**
     * POST /chat/message
     */
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/message")
    public ResponseEntity<ChatDtos.SendMessageResponse> sendMessage(
        Authentication auth,
        @Valid @RequestBody ChatDtos.SendMessageRequest body
    ) {
        UUID userId = (UUID) auth.getPrincipal();
        String preview = body.content()
                             .substring(0, Math.min(120, body.content()
                                                             .length()));
        log.info("[REST] POST /chat/message userId={} len={} preview=\"{}\"", userId, body.content()
                                                                                          .length(), body.content()
                                                                                                         .length() > 120 ? preview + "..." : preview);
        // 1) 사용자 메시지 저장 및 즉시 브로드캐스트
        UUID msgId = chatService.createUserMessage(userId, body.content(), Optional.empty());
        log.info("[REST] message queued: userId={} msgId=cm_{}", userId, msgId);
        var userPayload = java.util.Map.of(
            "id", "cm_" + msgId,
            "role", "user",
            "content", body.content(),
            "createdAt", java.time.Instant.now().toString()
        );
        chatSseHub.send(userId, userPayload);

        // 2) LLM 스트리밍 → 청크 전송
        final StringBuilder replyBuf = new StringBuilder();
        try {
            llmClient.streamGenerate(body.content(), delta -> {
                replyBuf.append(delta);
                chatSseHub.send(userId, java.util.Map.of("type", "assistant_chunk", "delta", delta));
            });
        } catch (Exception e) {
            String fallback = llmClient.generate(body.content());
            replyBuf.append(fallback);
            int sz = 100;
            for (int i = 0; i < fallback.length(); i += sz) {
                String delta = fallback.substring(i, Math.min(fallback.length(), i + sz));
                chatSseHub.send(userId, java.util.Map.of("type", "assistant_chunk", "delta", delta));
            }
        }
        String reply = replyBuf.toString();

        // 3) 어시스턴트 메시지 저장 및 완료/최종 브로드캐스트
        var assistantMsg = chatService.saveAssistantMessageEntity(userId, reply, Optional.empty());
        chatSseHub.send(userId, java.util.Map.of("type", "assistant_done", "id", "cm_" + assistantMsg.getId()));
        var assistantPayload = java.util.Map.of(
            "id", "cm_" + assistantMsg.getId(),
            "role", "assistant",
            "content", reply,
            "createdAt", java.time.Instant.now().toString()
        );
        chatSseHub.send(userId, assistantPayload);

        return ResponseEntity.status(HttpStatus.CREATED).body(ChatDtos.SendMessageResponse.queued(msgId));
    }

    /**
     * POST /chat/session
     */
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/session")
    public ResponseEntity<ChatDtos.CreateSessionResponse> createSession(Authentication auth) {
        UUID userId = (UUID) auth.getPrincipal();
        UUID id = chatService.createSession(userId);
        log.info("[REST] POST /chat/session userId={} sessionId=cs_{}", userId, id);
        return ResponseEntity.ok(ChatDtos.CreateSessionResponse.of(id));
    }
}


