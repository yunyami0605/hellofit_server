package com.hellofit.hellofit_server.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

public class AuthResponseDto {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    // 중복 체크 api response dto
    public static class NicknameDuplicate {
        private Boolean isDuplicate;
    }

    @Getter
    @Setter
    public static class KakaoToken {
        @JsonProperty("access_token")
        private String accessToken;
        @JsonProperty("refresh_token")
        private String refreshToken;
        @JsonProperty("expires_in")
        private Long expiresIn;
    }

    @Getter
    @Setter
    public static class KakaoUser {
        private String id;
        private KakaoAccount kakao_account;
        private KakaoProperties properties;

        @Getter
        @Setter
        public static class KakaoAccount {
            private String email;
        }

        @Getter
        @Setter
        public static class KakaoProperties {
            private String nickname;
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
//    @Schema(title = "인증 성공 토큰 반환 (ex. 로그인, 회원가입, 갱신)", description = "로그인 응답 DTO")
    public static class Access {
        @Schema(
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
        )
        private String access;
    }
}
