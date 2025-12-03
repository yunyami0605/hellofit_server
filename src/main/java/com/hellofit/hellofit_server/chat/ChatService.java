package com.hellofit.hellofit_server.chat;

import com.hellofit.hellofit_server.chat.dto.ChatDtos;
import com.hellofit.hellofit_server.global.exception.CommonException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatSessionRepository chatSessionRepository;

    @Transactional
    public UUID createSession(UUID userId) {
        ChatSessionEntity session = ChatSessionEntity.of(userId);
        chatSessionRepository.save(session);
        return session.getId();
    }

    @Transactional(readOnly = true)
    public ChatDtos.HistoryResponse getHistory(UUID userId, String cursorId, int size) {
        PageRequest page = PageRequest.of(0, size);
        Page<ChatMessageEntity> result;

        if (cursorId != null && cursorId.startsWith("cm_")) {
            UUID realId = UUID.fromString(cursorId.substring(3));
            ChatMessageEntity cursor = chatMessageRepository.findByIdAndUserId(realId, userId)
                                                            .orElseThrow(() -> new CommonException.BadRequest("ChatService.getHistory", "Invalid cursorId"));
            LocalDateTime before = cursor.getCreatedAt();
            result = chatMessageRepository.findByUserIdAndCreatedAtBeforeOrderByCreatedAtDesc(userId, before, page);
        } else {
            result = chatMessageRepository.findByUserIdOrderByCreatedAtDesc(userId, page);
        }

        var items = result.getContent()
                          .stream()
                          .map(ChatDtos.HistoryItem::from)
                          .toList()
            ;

        String nextCursor = items.isEmpty() ? null : items.get(items.size() - 1)
                                                          .id();

        return ChatDtos.HistoryResponse.builder()
                                       .items(items)
                                       .nextCursor(nextCursor)
                                       .build();
    }

    @Transactional
    public UUID createUserMessage(UUID userId, String content, Optional<UUID> sessionIdOpt) {
        if (content == null || content.isBlank() || content.length() > 1000) {
            throw new CommonException.BadRequest("ChatService.createUserMessage", "content invalid");
        }
        UUID sessionId = sessionIdOpt.orElse(null);
        ChatMessageEntity entity = ChatMessageEntity.of(userId, sessionId, ChatRole.USER, content);
        chatMessageRepository.save(entity);
        return entity.getId();
    }

    @Transactional
    public ChatMessageEntity createUserMessageEntity(UUID userId, String content, Optional<UUID> sessionIdOpt) {
        if (content == null || content.isBlank() || content.length() > 1000) {
            throw new CommonException.BadRequest("ChatService.createUserMessageEntity", "content invalid");
        }
        ChatMessageEntity entity = ChatMessageEntity.of(userId, sessionIdOpt.orElse(null), ChatRole.USER, content);
        chatMessageRepository.save(entity);
        return entity;
    }

    @Transactional
    public UUID saveAssistantMessage(UUID userId, String content, Optional<UUID> sessionIdOpt) {
        ChatMessageEntity entity = ChatMessageEntity.of(userId, sessionIdOpt.orElse(null), ChatRole.ASSISTANT, content);
        chatMessageRepository.save(entity);
        return entity.getId();
    }

    @Transactional
    public ChatMessageEntity saveAssistantMessageEntity(UUID userId, String content, Optional<UUID> sessionIdOpt) {
        ChatMessageEntity entity = ChatMessageEntity.of(userId, sessionIdOpt.orElse(null), ChatRole.ASSISTANT, content);
        chatMessageRepository.save(entity);
        return entity;
    }
}


