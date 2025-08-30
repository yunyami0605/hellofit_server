package com.hellofit.hellofit_server.post.dto;

import com.hellofit.hellofit_server.image.ImageEntity;
import com.hellofit.hellofit_server.image.dto.ImageResponseDto;
import com.hellofit.hellofit_server.post.PostEntity;
import com.hellofit.hellofit_server.user.UserEntity;
import com.hellofit.hellofit_server.user.dto.UserMappingResponseDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class PostResponseDto {
    @Getter
    @Setter
    @Builder
    public static class Summary{
        private String title;
        private String content;
        private UUID id;
        private List<String> images;
        private UserMappingResponseDto.Summary user;
        private LocalDateTime updatedAt;

        public static Summary from(PostEntity post, List<String> images){
            return Summary.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .user(UserMappingResponseDto.Summary.fromEntity(post.getUser()))
                    .images(images)
                    .updatedAt(post.getUpdatedAt())
                    .build();
        }
    }

    /**
     * list 조회 축약 응답형
     */
    @Getter
    @Setter
    @Builder
    public static class SummaryList{
        private String title;
        private String content;
        private UUID id;
        private List<String> images;
        private UserMappingResponseDto.Summary user;
        private LocalDateTime updatedAt;

        public static SummaryList from(PostEntity post, List<String> images){
            return SummaryList.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .user(UserMappingResponseDto.Summary.fromEntity(post.getUser()))
                    .images(images)
                    .updatedAt(post.getUpdatedAt())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class PatchData{
        private String title;
        private String content;
        private UUID id;
        private LocalDateTime updatedAt;
        private List<ImageResponseDto.DataBeforeMutation> images;

        public static PatchData fromEntity(PostEntity post, List<ImageResponseDto.DataBeforeMutation> images){
            return PatchData.builder()
                    .title(post.getTitle())
                    .content(post.getContent())
                    .id(post.getId())
                    .images(images)
                    .updatedAt(post.getUpdatedAt())
                    .build();
        }
    }
}
