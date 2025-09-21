package com.hellofit.hellofit_server.global.exception;

/**
 * 공통적인 exception 모음
 */
public class CommonException {

    /**
     * 접근 권한 없는 예외처리
     */
    public static class Forbidden extends BusinessException {
        public Forbidden(String point, String value) {
            super(ErrorCode.FORBIDDEN, point, value);
        }
    }

    public static class BadRequest extends BusinessException {
        public BadRequest(String point, String value) {
            super(ErrorCode.VALIDATION_FAILED, point, value);
        }

        // 로그 포멧 형식이 있을 경우
        public BadRequest(String point, String logFormat, Object... args) {
            super(ErrorCode.VALIDATION_FAILED, point, logFormat, args);
        }
    }
}
