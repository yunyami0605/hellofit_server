package com.hellofit.hellofit_server.global.exception;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

/*
* 비지니스 로직에 대한 예외처리
* */
@Slf4j
@Getter
public class BusinessException extends RuntimeException{
    private final ErrorCode code;
    private final String logMessage; // 로그 메세지

    public BusinessException(ErrorCode code, String key){
        super(code.getMessage());
        this.code = code;
        this.logMessage = code.getMessage() + ": " + key;
        log.error(this.logMessage);
    }

    // 로그 포멧 형식이 있을 경우
    public BusinessException(ErrorCode code, String logFormat, Object... args){
        super(code.getMessage());
        this.code = code;
        this.logMessage = String.format(logFormat, args);
        log.error(this.logMessage);
    }
}
