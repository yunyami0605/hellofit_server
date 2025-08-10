package com.hellofit.hellofit_server.auth;

import com.hellofit.hellofit_server.auth.dto.*;
import com.hellofit.hellofit_server.global.dto.MutationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "회원가입 API")
    @SecurityRequirements(value = {})
    @PostMapping("/signup")
    public ResponseEntity<MutationResponse> signup(@RequestBody @Valid SignupRequestDto request) {
        return ResponseEntity.ok(
                new MutationResponse(authService.signup(request))
        );
    }

    @Operation(summary = "로그인 API")
    @SecurityRequirements(value = {})
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody @Valid LoginRequestDto request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @Operation(summary = "토큰 갱신 API")
    @PostMapping("/refresh")
    public ResponseEntity<TokenRefreshResponseDto> refreshToken(@RequestBody @Valid TokenRefreshRequestDto request) {
        return ResponseEntity.ok(authService.refreshAccessToken(request));
    }

    @Operation(summary = "로그아웃 API")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal UUID userId){
        authService.logout(userId);
        return ResponseEntity.noContent().build();
    }
}
