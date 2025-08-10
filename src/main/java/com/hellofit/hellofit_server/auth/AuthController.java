package com.hellofit.hellofit_server.auth;

import com.hellofit.hellofit_server.auth.dto.*;
import com.hellofit.hellofit_server.global.dto.ErrorResponse;
import com.hellofit.hellofit_server.global.dto.MutationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Auth API", description = "인증/인가 관련 API")
@SecurityRequirement(name = "bearerAuth") // 기본은 인증 필요
public class AuthController {
    private final AuthService authService;

    @Operation(
        summary = "회원가입 API"
    )
    @ApiResponse(
            responseCode = "200",
            description = "가입 성공",
            content = @Content(schema = @Schema(implementation = MutationResponse.class))
    )
    @ApiResponse(
            responseCode = "409",
            description = "이미 존재하는 이메일"
    )
    @SecurityRequirements(value = {})
    @PostMapping("/signup")
    public ResponseEntity<MutationResponse> signup(@RequestBody @Valid SignupRequestDto request) {
        return ResponseEntity.ok(
                new MutationResponse(authService.signup(request))
        );
    }

    @Operation(
            summary = "이메일 로그인 API"
    )
    @ApiResponse(
            responseCode = "200",
            description = "로그인 성공",
            content = @Content(schema = @Schema(implementation = LoginResponseDto.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "가입되지 않은 이메일입니다."
    )
    @ApiResponse(
            responseCode = "401",
            description = "비밀번호가 일치하지 않습니다."
    )
    @SecurityRequirements(value = {})
    @PostMapping("/login")
    public LoginResponseDto login(@RequestBody @Valid LoginRequestDto request) {
        return authService.login(request);
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
