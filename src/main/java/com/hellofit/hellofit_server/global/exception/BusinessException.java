package com.hellofit.hellofit_server.global.exception;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/*
* 비지니스 로직에 대한 예외처리
* */
@Slf4j
@Getter
public class BusinessException extends RuntimeException{
    private final ErrorCode code;
    private final String logMessage; // 로그 메세지

    // 에러 발생 값만 표시할 경우
    public BusinessException(ErrorCode code, String value){
        super(code.getMessage());
        this.code = code;
        this.logMessage = String.format("[CODE: %s] = %s",code.getMessage(), value );
        log.error(this.logMessage);
    }

    // point = 에러 발생 지점
    public BusinessException(ErrorCode code, String point, String value){
        super(code.getMessage());
        this.code = code;
        this.logMessage = String.format("[CODE: %s][POINT: %s] = %s",code.getMessage(), point, value );
        log.error(this.logMessage);
    }

    // 로그 포멧 형식이 있을 경우
    public BusinessException(ErrorCode code, String point, String logFormat, Object... args){
        super(code.getMessage());
        this.code = code;
        String addLogMessage = String.format(logFormat, args);
        this.logMessage = String.format("[CODE: %s][POINT: %s] = %s",code.getMessage(), point, addLogMessage );
        log.error(this.logMessage);
    }
}
