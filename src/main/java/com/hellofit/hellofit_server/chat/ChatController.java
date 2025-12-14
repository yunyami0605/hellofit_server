package com.hellofit.hellofit_server.chat;

import com.hellofit.hellofit_server.chat.dto.ChatDtos;
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
 * - 이 컨트롤러는 HTTP 기반의 채팅 관련 API만 제공합니다.
 * - WebSocket 메시지 입출력은 /api/ws/chat 엔드포인트의 핸들러에서 처리되며,
 *   이 컨트롤러로 들어오지 않습니다.
 *
 * Endpoints
 * - GET /chat/history: 채팅 히스토리 조회(커서 기반, 최신순 정렬)
 * - POST /chat/message: 사용자 메시지를 등록(큐잉)하고 메시지 ID를 반환
 * - POST /chat/session: 채팅 세션을 생성하고 세션 ID(cs_*)를 반환
 */
public class ChatController {

    private final ChatService chatService;

    /**
     * GET /chat/history
     *
     * Desc
     * - 현재 사용자 기준 채팅 히스토리를 커서 기반으로 조회합니다.
     * - 정렬은 최신 메시지(createdAt DESC)부터 반환합니다.
     * - cursorId가 제공되면 해당 커서 이전(createdAt before cursor) 데이터만 조회합니다.
     *
     * Security
     * - bearerAuth 필요
     *
     * Params
     * - cursorId: 커서 문자열(null 가능). 서버 내부적으로 cm_UUID 형태를 처리합니다.
     * - size: 페이지 크기(1~100)
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
     *
     * Desc
     * - 사용자 메시지를 저장/큐잉합니다.
     * - 본 API는 메시지를 즉시 브로드캐스트하지 않을 수 있으며, 비동기 처리(예: LLM 응답)를 위한 큐에 적재할 수 있습니다.
     * - 반환 값은 메시지 ID(cm_*)와 상태(queued) 입니다.
     *
     * Security
     * - bearerAuth 필요
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
        UUID msgId = chatService.createUserMessage(userId, body.content(), Optional.empty());
        log.info("[REST] message queued: userId={} msgId=cm_{}", userId, msgId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ChatDtos.SendMessageResponse.queued(msgId));
    }

    /**
     * POST /chat/session
     *
     * Desc
     * - 새로운 채팅 세션을 생성합니다.
     * - 반환 값은 세션 ID(cs_*) 입니다.
     *
     * Security
     * - bearerAuth 필요
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


