package com.hellofit.hellofit_server.chat.sse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
/**
 * Chat SSE Controller
 *
 * 목적
 * - 클라이언트가 서버에서 푸시하는 채팅 이벤트(사용자/어시스턴트 메시지, 스트리밍 청크 등)를
 *   Server-Sent Events 방식으로 구독할 수 있도록 엔드포인트 제공.
 *
 * 보안
 * - JWT(httpOnly 쿠키 attk) 기반 인증이 필터에서 수행되고, 인증된 userId를 @AuthenticationPrincipal로 전달받는다.
 */
public class ChatSseController {

    private final ChatSseHub hub;

    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    /**
     * SSE 스트림 구독
     * - 클라이언트에서 EventSource로 연결 (withCredentials 필요)
     * - 연결이 수립되면 "connected" 신호가 즉시 전송됨
     */
    public SseEmitter stream(@AuthenticationPrincipal UUID userId) {
        return hub.subscribe(userId);
    }
}

