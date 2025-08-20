package com.hellofit.hellofit_server.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class TokenRefreshResponseDto {
    private String accessCookie;
}
