package com.hellofit.hellofit_server.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.UUID;

public class CommentRequestDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "CommentCreateRequest", description = "댓글/답글 생성 요청 DTO")
    public static class Create {
        @Schema(description = "댓글/답글 내용", example = "댓글 내용입니다.")
        @NotBlank(message = "내용은 필수 입력값입니다.")
        private String content;

        // 답글 상위 댓글
        @Schema(description = "부모 댓글 ID (없으면 null)",
            example = "550e8400-e29b-41d4-a716-446655440000",
            nullable = true)
        private UUID parentId;

        // 답글 대상자 id
        @Schema(description = "답글 대상 댓글 ID (없으면 null)",
            example = "660e8400-e29b-41d4-a716-446655440111",
            nullable = true)
        private UUID targetId;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "CommentUpdateRequest", description = "댓글/답글 수정 요청 DTO")
    public static class Update {
        @Schema(description = "댓글/답글 내용", example = "댓글 내용입니다.")
        @NotBlank(message = "내용은 필수 입력값입니다.")
        private String content;
    }
}
