package com.hellofit.hellofit_server.chat;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, UUID> {
    Page<ChatMessageEntity> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
    Page<ChatMessageEntity> findByUserIdAndCreatedAtBeforeOrderByCreatedAtDesc(UUID userId, LocalDateTime before, Pageable pageable);
    Page<ChatMessageEntity> findByUserIdAndSessionIdOrderByCreatedAtDesc(UUID userId, UUID sessionId, Pageable pageable);
    Optional<ChatMessageEntity> findByIdAndUserId(UUID id, UUID userId);
}


