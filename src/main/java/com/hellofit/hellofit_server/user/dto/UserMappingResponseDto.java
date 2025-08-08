package com.hellofit.hellofit_server.user.dto;

import com.hellofit.hellofit_server.user.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.UUID;

public class UserMappingResponseDto {
    @Getter
    @NoArgsConstructor
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
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Detail{
        private UUID id;
        private String nickname;
        private String email;

        public static Detail fromEntity(UserEntity userEntity) {
            return Detail.builder()
                    .id(userEntity.getId())
                    .nickname(userEntity.getNickname())
                    .build();
        }

    }
}