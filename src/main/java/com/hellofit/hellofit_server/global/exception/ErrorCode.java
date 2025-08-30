package com.hellofit.hellofit_server.global.exception;

import com.hellofit.hellofit_server.global.constants.ErrorMessage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 서비스 전역에서 사용하는 표준 에러 코드 Enum
 * 각 에러 코드는 HTTP 상태 코드와 기본 메시지를 함께 가진다.
 */
@Getter
@AllArgsConstructor
@Schema(description = "에러 코드 Enum") // Swagger 문서화용
public enum ErrorCode {
    // ===== 공통 관련 =====
    // 요청/검증 관련
    @Schema(description = ErrorMessage.VALIDATION_FAILED)
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, ErrorMessage.VALIDATION_FAILED),

    // 서버 내부 오류
    @Schema(description = ErrorMessage.INTERNAL_SERVER_ERROR)
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessage.INTERNAL_SERVER_ERROR),

    // 접근 권한이 없는 요청 (role, 비작성자)
    @Schema(description = ErrorMessage.FORBIDDEN)
    FORBIDDEN(HttpStatus.FORBIDDEN, ErrorMessage.FORBIDDEN),

    // ===== USER 관련 =====
    @Schema(description = ErrorMessage.USER_NOT_FOUND)
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, ErrorMessage.USER_NOT_FOUND),

    @Schema(description = ErrorMessage.DUPLICATE_EMAIL)
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, ErrorMessage.DUPLICATE_EMAIL),

    @Schema(description = ErrorMessage.DUPLICATE_NICKNAME)
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, ErrorMessage.DUPLICATE_NICKNAME),

    // ===== USER PROFILE 관련 =====
    @Schema(description = ErrorMessage.USER_PROFILE_DUPLICATE)
    USER_PROFILE_DUPLICATE(HttpStatus.CONFLICT, ErrorMessage.USER_PROFILE_DUPLICATE),

    @Schema(description = ErrorMessage.USER_PROFILE_NOT_FOUND)
    USER_PROFILE_NOT_FOUND(HttpStatus.NOT_FOUND, ErrorMessage.USER_PROFILE_NOT_FOUND),

    // ===== Auth 관련 =====
    @Schema(description = ErrorMessage.UNAUTHORIZED)
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, ErrorMessage.UNAUTHORIZED),
    
    // 가입되지 않은 이메일로 접근 시
    @Schema(description = ErrorMessage.UNAUTHORIZED_EMAIL)
    UNAUTHORIZED_EMAIL(HttpStatus.UNAUTHORIZED, ErrorMessage.UNAUTHORIZED_EMAIL),

    // 패스워드 불일치
    @Schema(description = ErrorMessage.NOT_MATCH_PASSWORD)
    NOT_MATCH_PASSWORD(HttpStatus.UNAUTHORIZED, ErrorMessage.NOT_MATCH_PASSWORD),

    @Schema(description = ErrorMessage.TOKEN_EXPIRED)
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, ErrorMessage.TOKEN_EXPIRED),

    @Schema(description = ErrorMessage.TOKEN_INVALID)
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, ErrorMessage.TOKEN_INVALID),

    // 잘못된 로그인 폼으로 접근 시
    @Schema(description = ErrorMessage.WRONG_LOGIN_FORM)
    WRONG_LOGIN_FORM(HttpStatus.UNAUTHORIZED, ErrorMessage.WRONG_LOGIN_FORM),

    // 게시글을 찾을 수 없는 경우
    @Schema(description = ErrorMessage.POST_NOT_FOUND)
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, ErrorMessage.POST_NOT_FOUND);

    private final HttpStatus status;
    private final String message;
}
