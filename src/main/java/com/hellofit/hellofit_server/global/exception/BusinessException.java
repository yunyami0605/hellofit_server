package com.hellofit.hellofit_server.global.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/*
 * 비지니스 로직에 대한 예외처리
 * */
@Slf4j
@Getter
public class BusinessException extends RuntimeException {
    private final ErrorCode code;
    private final String logMessage;

    // 단순 값만 표시할 때
    public BusinessException(ErrorCode code, String value) {
        super(code.getMessage());
        this.code = code;
        this.logMessage = String.format("[CODE: %s] = %s", code.getMessage(), value);
        log.warn(this.logMessage);
    }

    // 발생 지점 + 값
    public BusinessException(ErrorCode code, String point, String value) {
        super(code.getMessage());
        this.code = code;
        this.logMessage = String.format("[CODE: %s][POINT: %s] = %s", code.getMessage(), point, value);
        log.warn(this.logMessage);
    }

    // 포맷팅 로그
    public BusinessException(ErrorCode code, String point, String logFormat, Object... args) {
        super(code.getMessage());
        this.code = code;
        String addLogMessage = String.format(logFormat, args);
        this.logMessage = String.format("[CODE: %s][POINT: %s] = %s", code.getMessage(), point, addLogMessage);
        log.warn(this.logMessage);
    }

    // Throwable cause 포함
    public BusinessException(ErrorCode code, String point, String value, Throwable cause) {
        super(code.getMessage(), cause); // 원인 예외까지 RuntimeException에 전달
        this.code = code;
        this.logMessage = String.format("[CODE: %s][POINT: %s] = %s", code.getMessage(), point, value);
        log.error(this.logMessage, cause); // 원인까지 로그 출력
    }

    @Override
    public String toString() {
        return this.logMessage;
    }
}
