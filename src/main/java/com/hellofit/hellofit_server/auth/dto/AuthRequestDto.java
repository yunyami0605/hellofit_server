package com.hellofit.hellofit_server.auth.dto;

import com.hellofit.hellofit_server.user.enums.LoginProvider;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

public class AuthRequestDto {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "AuthRequest SocialLogin", description = "소셜 로그인 요청 DTO")
    public static class SocialLogin {
        @NotBlank(message = "코드는 필수 입력값입니다.")
        @Schema(
            description = "인가 코드",
            example = "a33wefhuhl..."
        )
        private String code;

        @NotNull(message = "소셜 공급자는 필수 입력값입니다.")
        @Schema(
            description = "소셜 공급자",
            example = "KAKAO",
            implementation = LoginProvider.class
        )
        private LoginProvider provider;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "AuthRequestDto SocialSignup", description = "소셜 회원가입 요청 DTO")
    public static class SocialSignup {
        @NotBlank(message = "비밀번호는 필수입니다.")
        @Email
        @Schema(
            description = "사용자 이메일 주소",
            example = "test@test.com"
        )
        private String email;

        @NotBlank(message = "닉네임은 필수 입력값입니다.")
        @Size(min = 2, max = 12, message = "닉네임은 2자 이상 12자 이하여야 합니다.")
        @Schema(
            description = "닉네임 (2~12자)",
            example = "test",
            minLength = 2,
            maxLength = 12
        )
        private String nickname;

        @AssertTrue(message = "개인정보 수집에 동의해야 가입할 수 있습니다.")
        @Schema(
            description = "개인정보 수집 동의 여부",
            example = "true"
        )
        private Boolean isPrivacyAgree;

        @NotBlank(message = "소셜 id는 필수 입력값입니다.")
        private String socialId;

        @NotNull(message = "소셜 공급자는 필수 입력값입니다.")
        @Schema(
            description = "소셜 공급자",
            example = "KAKAO"
        )
        private LoginProvider provider;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "AuthRequestDto EmailSignup", description = "회원가입 요청 DTO")
    public static class EmailSignup {

        @NotBlank(message = "이메일은 필수입니다.")
        @Email
        @Schema(
            description = "사용자 이메일 주소",
            example = "test@test.com"
        )
        private String email;

        @NotBlank(message = "비밀번호는 필수입니다.")
        @Schema(
            description = "비밀번호 (8~20자, 영문/숫자 조합 권장)",
            example = "test1234",
            format = "password",
            minLength = 8,
            maxLength = 20
        )
        private String password;

        @NotBlank(message = "닉네임은 필수 입력값입니다.")
        @Size(min = 2, max = 12, message = "닉네임은 2자 이상 12자 이하여야 합니다.")
        @Schema(
            description = "닉네임 (2~12자)",
            example = "test",
            minLength = 2,
            maxLength = 12
        )
        private String nickname;

        @AssertTrue(message = "개인정보 수집에 동의해야 가입할 수 있습니다.")
        @Schema(
            description = "개인정보 수집 동의 여부",
            example = "true"
        )
        private Boolean isPrivacyAgree;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Schema(name = "AuthRequestDto EmailLogin", description = "로그인 요청 DTO")
    public static class EmailLogin {
        @Email
        @NotBlank
        @Schema(description = "이메일", example = "test1@test.com")
        private String email;

        @NotBlank
        @Schema(description = "비밀번호", example = "test1234")
        private String password;
    }


}
