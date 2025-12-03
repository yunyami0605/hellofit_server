package com.hellofit.hellofit_server.chat;

import com.hellofit.hellofit_server.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "chat_sessions")
public class ChatSessionEntity extends BaseEntity {

    @Column(name = "user_id", nullable = false, columnDefinition = "CHAR(36)")
    private UUID userId;

    public static ChatSessionEntity of(UUID userId) {
        ChatSessionEntity e = new ChatSessionEntity();
        e.userId = userId;
        return e;
    }
}


