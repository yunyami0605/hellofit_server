package com.hellofit.hellofit_server.auth.exception;

import com.hellofit.hellofit_server.global.exception.BusinessException;
import com.hellofit.hellofit_server.global.exception.ErrorCode;

/*
* 가입되지 않는 이메일로 접근 시, 예외처리
* */
public class UnAuthorizedEmailException extends BusinessException {
    public UnAuthorizedEmailException() {
        super(ErrorCode.UNAUTHORIZED_EMAIL, "");
    }
}
