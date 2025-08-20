package com.hellofit.hellofit_server.auth.exception;

import com.hellofit.hellofit_server.global.exception.BusinessException;
import com.hellofit.hellofit_server.global.exception.ErrorCode;

/*
 * rf 토큰 만료된 경우, 예외 처리
 * */
public class TokenExpiredException extends BusinessException {
    public TokenExpiredException(String expiredToken) {
        super(ErrorCode.TOKEN_EXPIRED, expiredToken);
    }

    public TokenExpiredException(String point, String expiredToken) {
        super(ErrorCode.TOKEN_EXPIRED, point, expiredToken);
    }
}
