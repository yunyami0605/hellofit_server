package com.hellofit.hellofit_server.auth.exception;

import com.hellofit.hellofit_server.global.exception.BusinessException;
import com.hellofit.hellofit_server.global.exception.ErrorCode;

/*
* 유효하지 않는 토큰으로 접근 시, 예외 처리
* */
public class TokenInvalidException extends BusinessException {
    public TokenInvalidException(String tokenKey, String invalidToken) {
        super(ErrorCode.TOKEN_INVALID, String.format("%s: %s", tokenKey, invalidToken));
    }

    public TokenInvalidException(String point, String tokenKey, String invalidToken) {
        super(ErrorCode.TOKEN_INVALID, point, String.format("%s: %s", tokenKey, invalidToken));
    }
}
