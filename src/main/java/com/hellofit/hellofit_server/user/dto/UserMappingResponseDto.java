package com.hellofit.hellofit_server.user.dto;

import com.hellofit.hellofit_server.user.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

public class UserMappingResponseDto {
    @Getter
    @AllArgsConstructor
    @Builder
    public static class Summary {
        private UUID id;
        private String nickname;

        public static Summary fromEntity(UserEntity userEntity) {
            return Summary.builder()
                .id(userEntity.getId())
                .nickname(userEntity.getNickname())
                .build();
        }
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Detail {
        private UUID id;
        private String nickname;
        private String email;
        private Long postCount;
        private Long commentCount;

        public static Detail fromEntity(UserEntity userEntity, Long postCount, Long commentCount) {
            return Detail.builder()
                .id(userEntity.getId())
                .nickname(userEntity.getNickname())
                .email(userEntity.getEmail())
                .postCount(postCount)
                .commentCount(commentCount)
                .build();
        }

    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class AuthorInfo {
        private UUID id;
        private String nickname;
        private String imageUrl;

        public static AuthorInfo fromEntity(UserEntity userEntity, String imageUrl) {
            return AuthorInfo.builder()
                .id(userEntity.getId())
                .nickname(userEntity.getNickname())
                .imageUrl(imageUrl)
                .build();
        }
    }
}
