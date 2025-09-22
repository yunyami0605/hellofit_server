package com.hellofit.hellofit_server.post.dto;

import com.hellofit.hellofit_server.image.dto.ImageResponseDto;
import com.hellofit.hellofit_server.post.PostEntity;
import com.hellofit.hellofit_server.user.dto.UserMappingResponseDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class PostResponseDto {
    @Getter
    @Setter
    @Builder
    public static class Summary {
        private String title;
        private String content;
        private UUID id;
        private List<String> images;
        private UserMappingResponseDto.AuthorInfo author;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private Integer commentCount;
        private Integer likeCount;
        private Integer viewCount;


        public static Summary from(PostEntity post, List<String> images, Integer commentCount, Integer likeCount, String authorImageUrl) {
            return Summary.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .author(UserMappingResponseDto.AuthorInfo.fromEntity(post.getUser(), authorImageUrl))
                .images(images)
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .viewCount(post.getViewCount())
                .commentCount(commentCount)
                .likeCount(likeCount)
                .build();
        }
    }

    /**
     * list 조회 축약 응답형
     */
    @Getter
    @Setter
    @Builder
    public static class SummaryList {
        private String title;
        private String content;
        private UUID id;
        private Integer commentCount;
        private Integer likeCount;
        private Integer viewCount;
        private List<String> images;
        private UserMappingResponseDto.AuthorInfo author;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static SummaryList from(PostEntity post, List<String> images, Integer likeCount, Integer commentCount, String authorImageUrl) {
            return SummaryList.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .author(UserMappingResponseDto.AuthorInfo.fromEntity(post.getUser(), authorImageUrl))
                .images(images)
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .likeCount(likeCount)
                .commentCount(commentCount)
                .viewCount(post.getViewCount())
                .build();
        }
    }

    @Getter
    @Builder
    public static class PatchData {
        private String title;
        private String content;
        private UUID id;
        private List<ImageResponseDto.DataBeforeMutation> images;

        public static PatchData fromEntity(PostEntity post, List<ImageResponseDto.DataBeforeMutation> images) {
            return PatchData.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .id(post.getId())
                .images(images)
                .build();
        }
    }
}
