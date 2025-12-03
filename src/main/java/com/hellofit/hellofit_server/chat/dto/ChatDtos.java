package com.hellofit.hellofit_server.chat.dto;

import com.hellofit.hellofit_server.chat.ChatMessageEntity;
import com.hellofit.hellofit_server.chat.ChatRole;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.ZoneOffset;
import java.util.Map;
import java.util.UUID;

public class ChatDtos {

    public record HistoryQuery(
            String cursorId,
            @Min(1) @Max(100) Integer size
    ) {}

    public record HistoryItem(
            String id,
            String role,
            String content,
            String createdAt
    ) {
        public static HistoryItem from(ChatMessageEntity e) {
            return new HistoryItem(
                    "cm_" + e.getId(),
                    e.getRole().name().toLowerCase(),
                    e.getContent(),
                    e.getCreatedAt().atZone(ZoneOffset.UTC).toInstant().toString()
            );
        }
    }

    @Builder
    public record HistoryResponse(
            java.util.List<HistoryItem> items,
            String nextCursor
    ) {}

    public record SendMessageRequest(
            @NotBlank @Size(max = 1000) String content,
            Map<String, Object> meta
    ) {}

    public record SendMessageResponse(
            String id,
            String status
    ) {
        public static SendMessageResponse queued(UUID id) {
            return new SendMessageResponse("cm_" + id, "queued");
        }
    }

    public record CreateSessionResponse(String sessionId) {
        public static CreateSessionResponse of(UUID id) {
            return new CreateSessionResponse("cs_" + id);
        }
    }
}


