package com.hellofit.hellofit_server.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Schema(description = "유저 생성 요청 DTO")
public class CreateUserRequestDto {
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

    @Size(min = 2, max = 12, message = "닉네임은 2자 이상 12자 이하여야 합니다.")
    @NotBlank(message = "닉네임은 필수 입력값입니다.")
    @Schema(
        description = "닉네임 (2~12자)",
        example = "test",
        minLength = 2,
        maxLength = 12
    )
    private String nickname;

    @Column(name = "is_privacy_agree")
    @AssertTrue
    @Schema(
        description = "개인정보 수집 동의 여부",
        example = "true"
    )
    private Boolean isPrivacyAgree;
}
