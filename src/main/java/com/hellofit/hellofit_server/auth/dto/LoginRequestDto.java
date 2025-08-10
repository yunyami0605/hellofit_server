package com.hellofit.hellofit_server.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Schema(description = "로그인 요청 DTO")
public class LoginRequestDto {
    @Email
    @NotBlank
    @Schema(description = "이메일", example="test1@test.com")
    private String email;

    @NotBlank
    @Schema(description = "비밀번호", example="test1234")
    private String password;
}
