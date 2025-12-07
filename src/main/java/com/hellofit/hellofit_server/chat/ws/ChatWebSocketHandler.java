package com.hellofit.hellofit_server.chat.ws;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hellofit.hellofit_server.chat.ChatService;
import com.hellofit.hellofit_server.llm.LlmClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
@Component
@RequiredArgsConstructor
/**
 * ChatWebSocketHandler
 *
 * 룸 전략
 * - roomId 제공 시: "room:{roomId}" 룸으로 조인하여 모든 참여자에게 브로드캐스트
 * - roomId 미제공 시: "user:{userId}" 개인 룸으로만 에코
 *
 * 주의
 * - 현재 스트리밍 chunk는 보낸 세션에만 전송하고, 최종 완성 메시지만 룸 전체 브로드캐스트
 * - 멀티 인스턴스 환경에서는 외부 pub/sub(예: Redis)가 필요
 */
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;
    private final ChatService chatService;
    private final LlmClient llmClient;

    // roomKey -> sessions
    // 동시성: 다중 세션 접근을 고려해 ConcurrentHashMap + CopyOnWriteArraySet 사용
    private final ConcurrentHashMap<String, CopyOnWriteArraySet<WebSocketSession>> roomSessions = new ConcurrentHashMap<>();
    // sessionId -> roomKey
    private final ConcurrentHashMap<String, String> sessionRoom = new ConcurrentHashMap<>();

    /**
     * 핸드셰이크 완료 후: 사용자 인증 결과(userId) 확인, 룸 조인, connected 이벤트 전송
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        UUID userId = (UUID) session.getAttributes()
                                    .get("userId");
        if (userId == null) {
            sendJson(session, Map.of("type", "error", "code", "WS_UNAUTHORIZED", "message", "Invalid or missing token"));
            session.close(new CloseStatus(1008, "Unauthorized"));
            log.info("[WS] NOT EXIST USER HERE");
            return;
        }
        log.info("[WS] connected: userId={}", userId);
        String roomKey = resolveRoomKey(session, userId);
        roomSessions.computeIfAbsent(roomKey, k -> new CopyOnWriteArraySet<>())
                    .add(session);
        sessionRoom.put(session.getId(), roomKey);
        sendJson(session, Map.of("type", "connected"));
    }

    /**
     * 클라이언트 프레임 라우팅
     * - user_message: 실제 채팅 처리
     * - typing: 부가 신호(서버에서는 로그만)
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        UUID userId = (UUID) session.getAttributes()
                                    .get("userId");
        if (userId == null) {
            session.close(new CloseStatus(1008, "Unauthorized"));
            return;
        }
        JsonNode node = objectMapper.readTree(message.getPayload());
        String type = node.path("type")
                          .asText();
        switch (type) {
            case "user_message" -> handleUserMessage(session, userId, node);
            case "typing" -> {
                // no-op
                if (node.has("value")) {
                    log.debug("[WS] typing: userId={} value={}", userId, node.get("value")
                                                                             .asBoolean());
                }
            }
            default ->
                sendJson(session, Map.of("type", "error", "code", "WS_PROTOCOL_ERROR", "message", "Unknown type"));
        }
    }

    /**
     * 사용자 메시지 처리
     * 1) 유효성 검사
     * 2) DB 저장(사용자 메시지)
     * 3) 사용자 메시지를 룸 전체로 브로드캐스트(에코 포함)
     * 4) LLM 호출 → 청크 스트리밍(요청 세션) → 완료 프레임 전송
     * 5) 어시스턴트 메시지 저장 및 룸 전체 브로드캐스트
     */
    private void handleUserMessage(WebSocketSession session, UUID userId, JsonNode node) throws IOException {
        String content = node.path("content")
                             .asText();
        if (content == null || content.isBlank()) {
            sendJson(session, Map.of("type", "error", "code", "INVALID_INPUT", "message", "content required"));
            return;
        }
        if (content.length() > 1000) {
            sendJson(session, Map.of("type", "error", "code", "PAYLOAD_TOO_LARGE", "message", "content too long"));
            return;
        }

        log.info("[WS] user_message received: userId={} len={} preview=\"{}\"",
                userId, content.length(), preview(content, 120));
        // 세션 파라미터(optional)
        UUID sessionId = null;
        if (node.has("sessionId")) {
            String s = node.get("sessionId").asText();
            if (s != null && !s.isBlank()) {
                if (s.startsWith("cs_")) {
                    try { sessionId = UUID.fromString(s.substring(3)); } catch (Exception ignored) {}
                } else {
                    try { sessionId = UUID.fromString(s); } catch (Exception ignored) {}
                }
            }
        }
        var userMsg = chatService.createUserMessageEntity(userId, content, Optional.ofNullable(sessionId));
        log.debug("[WS] user_message saved: userId={} msgId=cm_{}", userId, userMsg.getId());

        // echo back and broadcast to room
        // createdAt은 UTC ISO-8601로 정규화하여 프론트 파싱 일관성 유지
        Map<String, Object> userMsgPayload = Map.of(
            "id", "cm_" + userMsg.getId(),
            "role", "user",
            "content", content,
            "createdAt", userMsg.getCreatedAt()
                                .atZone(ZoneOffset.UTC)
                                .toInstant()
                                .toString()
        );
        broadcastToRoom(session, userMsgPayload);

        // 세션 히스토리 기반 LLM 호출 (가능한 경우)
        var history = chatService.getSessionMessagesForLlm(userId, sessionId, 20);
        var historyWithCurrent = new java.util.ArrayList<com.hellofit.hellofit_server.llm.LlmClient.ChatMessage>(history);
        historyWithCurrent.add(new com.hellofit.hellofit_server.llm.LlmClient.ChatMessage("user", content));
        String reply = history.isEmpty() ? llmClient.generate(content) : llmClient.chat(historyWithCurrent);

        // streaming simulation by chunking
        // 실제 LLM이 스트리밍을 지원하지 않는 경우 대비: 고정 길이로 분할 전송
        int chunkSize = 100;
        for (int i = 0; i < reply.length(); i += chunkSize) {
            String delta = reply.substring(i, Math.min(reply.length(), i + chunkSize));
            sendJson(session, Map.of("type", "assistant_chunk", "delta", delta));
        }

        var assistantMsg = chatService.saveAssistantMessageEntity(userId, reply, Optional.ofNullable(sessionId));
        log.info("[WS] assistant_done: userId={} answerLen={} msgId=cm_{}", userId, reply.length(), assistantMsg.getId());
        ObjectNode done = objectMapper.createObjectNode();
        done.put("type", "assistant_done");
        done.put("id", "cm_" + assistantMsg.getId());
        sendJson(session, done);

        // broadcast assistant message as final message to room
        Map<String, Object> assistantPayload = Map.of(
            "id", "cm_" + assistantMsg.getId(),
            "role", "assistant",
            "content", reply,
            "createdAt", assistantMsg.getCreatedAt()
                                     .atZone(ZoneOffset.UTC)
                                     .toInstant()
                                     .toString()
        );
        broadcastToRoom(session, assistantPayload);
    }

    /**
     * 트랜스포트 에러 시 간단한 에러 프레임 전송
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.warn("WS error", exception);
        if (session.isOpen()) {
            sendJson(session, Map.of("type", "error", "code", "INTERNAL_ERROR", "message", "Unexpected error"));
        }
    }

    /**
     * 세션 종료 시 룸에서 제거하고 비어 있으면 룸 정리
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String roomKey = sessionRoom.remove(session.getId());
        if (roomKey != null) {
            var set = roomSessions.getOrDefault(roomKey, new CopyOnWriteArraySet<>());
            set.remove(session);
            if (set.isEmpty()) {
                roomSessions.remove(roomKey);
            }
        }
        super.afterConnectionClosed(session, status);
    }

    /**
     * 룸 키 결정
     * - roomId 제공 시 "room:{roomId}"
     * - 미제공 시 "user:{userId}"
     */
    private String resolveRoomKey(WebSocketSession session, UUID userId) {
        Object roomIdAttr = session.getAttributes()
                                   .get("roomId");
        if (roomIdAttr instanceof String s && !s.isBlank()) {
            return "room:" + s;
        }
        return "user:" + userId;
    }

    /**
     * 동일 룸의 모든 세션에 JSON 페이로드를 전송
     * - 소스 세션의 룸을 기준으로 조회
     */
    private void broadcastToRoom(WebSocketSession sourceSession, Object payload) throws IOException {
        String roomKey = sessionRoom.get(sourceSession.getId());
        if (roomKey == null) {
            sendJson(sourceSession, payload);
            return;
        }
        var targets = roomSessions.getOrDefault(roomKey, new CopyOnWriteArraySet<>());
        String json = objectMapper.writeValueAsString(payload);
        for (WebSocketSession s : targets) {
            if (s.isOpen()) {
                s.sendMessage(new TextMessage(json.getBytes(StandardCharsets.UTF_8)));
            }
        }
    }

    /**
     * 로그용 미리보기 문자열 생성
     */
    private String preview(String text, int max) {
        String p = text.substring(0, Math.min(text.length(), max));
        return text.length() > max ? p + "..." : p;
    }

    /**
     * JSON 직렬화 후 텍스트 프레임 전송
     */
    private void sendJson(WebSocketSession session, Object obj) throws IOException {
        String json = objectMapper.writeValueAsString(obj);
        session.sendMessage(new TextMessage(json.getBytes(StandardCharsets.UTF_8)));
    }
}


