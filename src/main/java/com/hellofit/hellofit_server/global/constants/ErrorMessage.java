package com.hellofit.hellofit_server.global.constants;

public class ErrorMessage {
    // ===== 공통 관련 =====
    public static final String INTERNAL_SERVER_ERROR = "알 수 없는 오류가 발생했습니다.";
    public static final String VALIDATION_FAILED = "요청 값이 유효하지 않습니다.";
    public static final String FORBIDDEN = "권한이 없습니다.";

    // ===== USER 관련 =====
    public static final String USER_NOT_FOUND = "요청한 사용자를 찾을 수 없습니다.";
    public static final String DUPLICATE_EMAIL = "이미 가입된 이메일입니다.";
    public static final String DUPLICATE_NICKNAME = "이미 사용중인 닉네임입니다.";

    // ===== USER 관련 =====
    public static final String USER_PROFILE_NOT_FOUND = "사용자 프로필이 없습니다.";
    public static final String USER_PROFILE_DUPLICATE = "이미 사용자 프로필이 존재합니다.";

    // ===== Auth 관련 =====
    public static final String UNAUTHORIZED = "인증이 필요합니다.";
    public static final String UNAUTHORIZED_EMAIL = "가입되지 않은 이메일입니다.";
    public static final String NOT_MATCH_PASSWORD = "비밀번호가 일치하지 않습니다.";
    public static final String WRONG_LOGIN_FORM = "아이디 또는 비밀번호가 잘못 되었습니다.";
    public static final String TOKEN_EXPIRED = "토큰이 만료되었습니다.";
    public static final String TOKEN_INVALID = "유효하지 않은 토큰입니다.";

    // ===== Post 관련 =====
    public static final String POST_NOT_FOUND = "게시글을 찾을 수 없습니다.";
}
