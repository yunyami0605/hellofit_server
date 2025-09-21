package com.hellofit.hellofit_server.comment.dto;

import com.hellofit.hellofit_server.comment.CommentEntity;
import com.hellofit.hellofit_server.post.dto.PostResponseDto;
import com.hellofit.hellofit_server.user.dto.UserMappingResponseDto;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 댓글 응답 dto
 */
public class CommentResponseDto {
    @Getter
    @Builder
    public static class Summary {
        private UUID id;
        private UUID postId;
        private UUID parentId;
        private String content;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private LocalDateTime deletedAt;
        private UserMappingResponseDto.Summary author;
        private Integer likeCount;
        private String targetNickname;

        public static CommentResponseDto.Summary from(CommentEntity comment, int likeCount) {

            String targetNickname = null;

            if (comment.getTarget() != null) {
                targetNickname = comment.getTarget()
                    .getUser()
                    .getNickname();
            }


            return Summary.builder()
                .id(comment.getId())
                .postId(comment.getPost()
                    .getId())
                .parentId(comment.getParent() != null ? comment.getParent()
                    .getId() : null)
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .deletedAt(comment.getDeletedAt())
                .author(UserMappingResponseDto.Summary.fromEntity(comment.getUser()))
                .likeCount(likeCount)
                .targetNickname(targetNickname)
                .build();
        }

    }
}
