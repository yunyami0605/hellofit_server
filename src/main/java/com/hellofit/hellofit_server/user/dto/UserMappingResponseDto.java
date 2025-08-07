package com.hellofit.hellofit_server.user.dto;

import com.hellofit.hellofit_server.user.User;
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

        public static Summary fromEntity(User user) {
            return Summary.builder()
                    .id(user.getId())
                    .nickname(user.getNickname())
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

        public static Detail fromEntity(User user) {
            return Detail.builder()
                    .id(user.getId())
                    .nickname(user.getNickname())
                    .build();
        }

    }
}