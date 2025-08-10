package com.hellofit.hellofit_server.auth.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequestDto {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    @Size(min = 2, max = 12)
    private String nickname;

    @AssertTrue(message = "개인정보 수집에 동의해야 가입할 수 있습니다.")
    private Boolean isPrivacyAgree;
}
