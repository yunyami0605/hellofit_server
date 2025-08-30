package com.hellofit.hellofit_server.global.exception;

/**
 * 공통적인 exception 모음
 */
public class CommonException {

    /**
     * 접근 권한 없는 예외처리
     */
    public static class Forbidden extends BusinessException {
        public Forbidden(String point, String value){
            super(ErrorCode.FORBIDDEN, point, value);
        }
    }
}
