package com.hellofit.hellofit_server.chat.sse;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
@Component
@RequiredArgsConstructor
/**
 * ChatSseHub
 *
 * 목적
 * - 사용자별(Server-Sent Events) 스트림 연결을 관리하고 메시지를 전송하는 허브.
 *
 * 설계
 * - 연결/종료/에러 시 정리(cleanup) 로직으로 메모리 누수 방지
 */
public class ChatSseHub {

    private final ObjectMapper objectMapper;

    // userId -> (해당 유저의 모든 SSE 연결)
    private final ConcurrentHashMap<UUID, CopyOnWriteArraySet<SseEmitter>> userEmitters = new ConcurrentHashMap<>();

    /**
     * 구독 시작 (SSE 연결 생성)
     * - 최초 연결 시 간단한 "connected" 신호를 전송(클라이언트 상태 갱신용)
     */
    public SseEmitter subscribe(UUID userId) {
        // default timeout: no timeout (0L)
        SseEmitter emitter = new SseEmitter(0L);
        userEmitters.computeIfAbsent(userId, k -> new CopyOnWriteArraySet<>()).add(emitter);

        emitter.onTimeout(() -> cleanup(userId, emitter));
        emitter.onCompletion(() -> cleanup(userId, emitter));
        emitter.onError((e) -> cleanup(userId, emitter));

        // initial connected event
        try {
            send(userId, java.util.Map.of("type", "connected"));
        } catch (Exception ignored) {}

        return emitter;
    }

    /**
     * 페이로드 전송
     * - 동일 유저의 모든 SseEmitter에 동일 JSON 문자열을 브로드캐스트
     * - 전송 중 예외가 발생한 emitter는 정리(cleanup)
     */
    public void send(UUID userId, Object payload) {
        var set = userEmitters.getOrDefault(userId, new CopyOnWriteArraySet<>());
        if (set.isEmpty()) return;
        try {
            String json = objectMapper.writeValueAsString(payload);
            for (SseEmitter emitter : set) {
                try {
                    emitter.send(json, MediaType.TEXT_PLAIN);
                } catch (IOException ex) {
                    cleanup(userId, emitter);
                }
            }
        } catch (Exception e) {
            log.warn("[SSE] serialize/send failed", e);
        }
    }

    /**
     * 연결 정리
     * - emitter 제거, 비었으면 userId 키 자체 삭제
     */
    private void cleanup(UUID userId, SseEmitter emitter) {
        var set = userEmitters.getOrDefault(userId, new CopyOnWriteArraySet<>());
        set.remove(emitter);
        if (set.isEmpty()) {
            userEmitters.remove(userId);
        }
    }
}

