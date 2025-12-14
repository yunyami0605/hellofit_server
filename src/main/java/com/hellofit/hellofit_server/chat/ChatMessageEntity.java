package com.hellofit.hellofit_server.chat;

import com.hellofit.hellofit_server.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "chat_messages", indexes = {
        @Index(name = "idx_chat_messages_user_created", columnList = "user_id, created_at")
})
public class ChatMessageEntity extends BaseEntity {

    @Column(name = "user_id", nullable = false, columnDefinition = "CHAR(36)")
    private UUID userId;

    @Column(name = "session_id", columnDefinition = "CHAR(36)")
    private UUID sessionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private ChatRole role;

    // 어시스턴트 답변이 1000자를 초과할 수 있어 TEXT로 확장
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    public static ChatMessageEntity of(UUID userId, UUID sessionId, ChatRole role, String content) {
        ChatMessageEntity e = new ChatMessageEntity();
        e.userId = userId;
        e.sessionId = sessionId;
        e.role = role;
        e.content = content;
        return e;
    }
}


