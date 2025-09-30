package com.hellofit.hellofit_server.auth.exception;

import com.hellofit.hellofit_server.global.exception.BusinessException;
import com.hellofit.hellofit_server.global.exception.ErrorCode;

/**
 * 카카오 인증 관련 요청 예외처리
 */
public class SocialAuthException {
    // 카카오 유저 정보 요청 실패
    public static class UserInfoRequestFail extends BusinessException {
        public UserInfoRequestFail(String point, String value, Throwable cause) {
            super(ErrorCode.KAKAO_USERINFO_REQUEST_FAIL, point, value, cause);
        }
    }

    // 카카오 사용자 인증 실패 (401)
    public static class AuthFail extends BusinessException {
        public AuthFail(String point, String value, Throwable cause) {
            super(ErrorCode.KAKAO_AUTH_FAIL, point, value, cause);
        }
    }

    // 카카오 서버 자체 오류 (5xx)
    public static class ServerError extends BusinessException {
        public ServerError(String point, String value, Throwable cause) {
            super(ErrorCode.KAKAO_SERVER_ERROR, point, value, cause);
        }
    }

    // 카카오 api 통신 불가능
    public static class ApiUnavailable extends BusinessException {
        public ApiUnavailable(String point, String value, Throwable cause) {
            super(ErrorCode.KAKAO_API_UNAVAILABLE, point, value, cause);
        }
    }

    // 지원하지 않는 소셜 종류 로그인 인경우
    public static class UnsupportedProvider extends BusinessException {
        public UnsupportedProvider(String point, String value) {
            super(ErrorCode.UNSUPPORTED_SOCIAL_PROVIDER, point, value);
        }
    }
}
