package com.hellofit.hellofit_server.user.exception;

import com.hellofit.hellofit_server.global.exception.BusinessException;
import com.hellofit.hellofit_server.global.exception.ErrorCode;
import lombok.Getter;

import java.util.UUID;

/**
 * 유저 exception wrapper class
 */
public class UserException {
    /**
     * 유저 이메일 중복 예외
     */
    public static class UserDuplicateEmailException extends BusinessException {
        public UserDuplicateEmailException(String point, String email) {
            super(ErrorCode.DUPLICATE_EMAIL, point, email);
        }
    }

    /**
     * 유저 닉네임 중복 예외
     */
    public static class UserDuplicateNicknameException extends BusinessException {
        public UserDuplicateNicknameException(String point, String nickname) {
            super(ErrorCode.DUPLICATE_NICKNAME, point, nickname);
        }
    }

    /**
     * 유저 not found 예외
     */
    public static class UserNotFoundException extends BusinessException {
        public UserNotFoundException(String point, UUID id) {
            super(ErrorCode.USER_NOT_FOUND, point, id.toString());
        }
    }

    /**
     * 소셜 로그인 유저 not found 예외
     */
    @Getter
    public static class SocialUserNotFoundException extends BusinessException {
        private final UUID userId;
        private final String provider;

        public SocialUserNotFoundException(String point, UUID id, String provider) {
            super(ErrorCode.USER_NOT_FOUND, point, id.toString());
            this.userId = id;
            this.provider = provider;
        }
    }

    /**
     * 이미 가입된 소셜 유저
     */
    public static class UserDuplicateSocialException extends BusinessException {
        public UserDuplicateSocialException(String point, String id) {
            super(ErrorCode.DUPLICATE_SOCIAL, point, id);
        }
    }
}
