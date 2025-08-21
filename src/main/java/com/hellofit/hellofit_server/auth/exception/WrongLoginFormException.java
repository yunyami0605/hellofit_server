package com.hellofit.hellofit_server.auth.exception;

import com.hellofit.hellofit_server.global.exception.BusinessException;
import com.hellofit.hellofit_server.global.exception.ErrorCode;

/*
 * 잘못된 로그인 폼으로 접근 시, 예외처리
 * */
public class WrongLoginFormException extends BusinessException {
    public WrongLoginFormException(String value) {
        super(ErrorCode.WRONG_LOGIN_FORM, value);
    }
}
