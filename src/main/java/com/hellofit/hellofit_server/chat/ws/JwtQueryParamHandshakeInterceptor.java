package com.hellofit.hellofit_server.chat.ws;

import com.hellofit.hellofit_server.global.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtQueryParamHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        if (request instanceof ServletServerHttpRequest servlet) {
            HttpServletRequest http = servlet.getServletRequest();
            String token = http.getParameter("token");
            String roomId = http.getParameter("roomId");
            if (token == null || token.isBlank()) {
                log.warn("[WS] handshake without token");
                return true; // allow, will be closed immediately after connect by handler
            }
            var status = jwtTokenProvider.validateToken(token);
            if (status != com.hellofit.hellofit_server.auth.constants.TokenStatus.VALID) {
                log.warn("[WS] handshake invalid token");
                return true;
            }
            UUID userId = jwtTokenProvider.getUserIdFromToken(token);
            attributes.put("userId", userId);
            if (roomId != null && !roomId.isBlank()) {
                attributes.put("roomId", roomId);
            }
            log.debug("[WS] handshake validated: userId={}", userId);
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
    }
}


