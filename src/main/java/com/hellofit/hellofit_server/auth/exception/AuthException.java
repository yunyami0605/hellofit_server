package com.hellofit.hellofit_server.auth.exception;

import com.hellofit.hellofit_server.global.exception.BusinessException;
import com.hellofit.hellofit_server.global.exception.ErrorCode;

public class AuthException {
    /*
     * 잘못된 로그인 폼으로 접근 시, 예외처리
     * */
    public static class WrongLoginForm extends BusinessException {
        public WrongLoginForm(String point, String value) {
            super(ErrorCode.WRONG_LOGIN_FORM, point, value);
        }
    }

    /*
     * rf 토큰 만료된 경우, 예외 처리
     * */
    public static class TokenExpired extends BusinessException {
        public TokenExpired(String point, String expiredToken) {
            super(ErrorCode.TOKEN_EXPIRED, point, expiredToken);
        }
    }

    /*
     * 유효하지 않는 토큰으로 접근 시, 예외 처리
     * */
    public static class TokenInvalid extends BusinessException {
        public TokenInvalid(String point, String tokenKey, String invalidToken) {
            super(ErrorCode.TOKEN_INVALID, point, String.format("%s: %s", tokenKey, invalidToken));
        }
    }

}
