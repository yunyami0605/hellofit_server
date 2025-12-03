package com.hellofit.hellofit_server.chat;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ChatSessionRepository extends JpaRepository<ChatSessionEntity, UUID> {
    Optional<ChatSessionEntity> findTopByUserIdOrderByCreatedAtDesc(UUID userId);
}


