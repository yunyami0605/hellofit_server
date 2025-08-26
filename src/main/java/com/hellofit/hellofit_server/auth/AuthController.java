package com.hellofit.hellofit_server.auth;

import com.hellofit.hellofit_server.auth.dto.*;
import com.hellofit.hellofit_server.global.constants.ErrorMessage;
import com.hellofit.hellofit_server.global.dto.ApiErrorResponse;
import com.hellofit.hellofit_server.global.dto.MutationResponse;
import com.hellofit.hellofit_server.user.UserEntity;
import com.hellofit.hellofit_server.user.dto.UserMappingResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Auth API", description = "인증/인가 관련 API")
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
    @ApiResponse(responseCode = "409", description = ErrorMessage.DUPLICATE_EMAIL, content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @SecurityRequirements(value = {})
    @PostMapping("/signup")
    public ResponseEntity<LoginResponseDto> signup(@RequestBody @Valid SignupRequestDto request, HttpServletResponse response) {
        return ResponseEntity.ok(
                authService.signup(request, response)
        );
    }

    @Operation(
            summary = "이메일 로그인 API"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "로그인 성공",
                    content = @Content(schema = @Schema(implementation = LoginResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "로그인 실패",
                    content = @Content(
                            schema = @Schema(implementation = ApiErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = ErrorMessage.WRONG_LOGIN_FORM
                                    )
                            }
                    )
            )
    })
    @SecurityRequirements(value = {})
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody @Valid LoginRequestDto request, HttpServletResponse response) {
        return ResponseEntity.ok(authService.login(request, response));
    }

    @Operation(summary = "토큰 갱신 API")
    @PostMapping("/refresh")
    public ResponseEntity<TokenRefreshResponseDto> refreshToken(HttpServletRequest request) {
        return ResponseEntity.ok(authService.refreshAccessToken(request));
    }

    @Operation(summary = "로그아웃 API")
    @ApiResponse(
            responseCode = "200",
            description = "로그아웃 성공"
    )
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal UserEntity user){
        authService.logout(user.getId());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "본인 유저 정보 조회 API")
    @ApiResponse(
            responseCode = "200",
            description = "유저 정보 성공",
            content = @Content(schema = @Schema(implementation = UserMappingResponseDto.Summary.class))
    )
    @GetMapping("/info")
    public UserMappingResponseDto.Summary authInfo(@AuthenticationPrincipal UserEntity user){
        return authService.getAuthInfo(user.getId());
    }

    @Operation(summary = "XSRF Token 발급 API")
    @ApiResponse(
            responseCode = "200",
            description = "토큰 발급 성공"
    )
    @SecurityRequirements(value = {})
    @PostMapping("/xc")
    public ResponseEntity<Boolean> postXSRFToken(HttpServletResponse response){
        Boolean result = authService.postXSRFToken(response);
        return ResponseEntity.ok(result);
    }

    //check-nickname?nickname=hong
    @Operation(summary = "닉네임 중복 체크 API")
    @ApiResponse(
            responseCode = "200",
            description = "true -> 중복 닉네임",
            content = @Content(schema = @Schema(implementation = AuthResponseDto.NicknameDuplicate.class))
    )
    @GetMapping("/check-nickname")
    @SecurityRequirements(value = {})
    public ResponseEntity<AuthResponseDto.NicknameDuplicate> checkNicknameDuplicate(@RequestParam String nickname){
        return ResponseEntity.ok(authService.checkNicknameDuplicate(nickname));
    }
}
