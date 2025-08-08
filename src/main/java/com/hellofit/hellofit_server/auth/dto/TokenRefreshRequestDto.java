package com.hellofit.hellofit_server.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

/**
 * 재발급 요청 api dto
 * */
@Getter
public class TokenRefreshRequestDto {
    @NotBlank
    private String refreshToken;
}
