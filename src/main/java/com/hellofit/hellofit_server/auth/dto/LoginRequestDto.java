package com.hellofit.hellofit_server.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class LoginRequestDto {
    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;
}
