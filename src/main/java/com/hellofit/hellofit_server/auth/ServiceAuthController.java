package com.hellofit.hellofit_server.auth;

import com.hellofit.hellofit_server.global.jwt.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth/service")
@RequiredArgsConstructor
@Tag(name = "Service Token API", description = "SSR/서버용 서비스 토큰 발급 API")
public class ServiceAuthController {

    private final JwtTokenProvider jwtTokenProvider;

    @Value("${service.api-key}")
    private String serviceApiKey;

    @Operation(summary = "서비스용 JWT 발급 API (SSR 전용)")
    @PostMapping("/token")
    public ResponseEntity<?> issueServiceToken(@RequestHeader("X-API-KEY") String apiKey) {
        // 1. API 키 검증
        if (!serviceApiKey.equals(apiKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Invalid API key"));
        }

        // 2. service role 토큰 발급
        String token = jwtTokenProvider.generateServiceToken();

        // 3. 응답
        return ResponseEntity.ok(Map.of("access_token", token));
    }
}
