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
public class ChatController {

    private final ChatService chatService;

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

    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/session")
    public ResponseEntity<ChatDtos.CreateSessionResponse> createSession(Authentication auth) {
        UUID userId = (UUID) auth.getPrincipal();
        UUID id = chatService.createSession(userId);
        log.info("[REST] POST /chat/session userId={} sessionId=cs_{}", userId, id);
        return ResponseEntity.ok(ChatDtos.CreateSessionResponse.of(id));
    }
}


