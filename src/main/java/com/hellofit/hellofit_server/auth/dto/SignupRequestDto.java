package com.hellofit.hellofit_server.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "회원가입 요청 DTO")
public class SignupRequestDto {

    @NotBlank(message = "비밀번호는 필수입니다.")
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
