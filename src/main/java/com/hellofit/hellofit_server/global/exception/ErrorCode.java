package com.hellofit.hellofit_server.global.exception;

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

    // ===== 사용자 관련 =====
    @Schema(description = "사용자를 찾을 수 없음")
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 사용자를 찾을 수 없습니다."),

    @Schema(description = "중복된 이메일")
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 가입된 이메일입니다."),

    // ===== 요청/검증 관련 =====
    @Schema(description = "유효성 검증 실패")
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "요청 값이 유효하지 않습니다."),

    // ===== 인증/인가 관련 =====
    @Schema(description = "인증 실패")
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),

    @Schema(description = "권한 없음")
    FORBIDDEN(HttpStatus.FORBIDDEN, "권한이 없습니다."),

    // ===== 토큰 관련 =====
    @Schema(description = "토큰 만료")
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),

    @Schema(description = "유효하지 않은 토큰")
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),

    // ===== 서버 내부 오류 =====
    @Schema(description = "알 수 없는 서버 오류")
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String defaultMessage;
}
