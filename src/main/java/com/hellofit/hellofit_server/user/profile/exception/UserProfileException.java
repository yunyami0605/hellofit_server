package com.hellofit.hellofit_server.user.profile.exception;

import com.hellofit.hellofit_server.global.exception.BusinessException;
import com.hellofit.hellofit_server.global.exception.ErrorCode;

import java.util.UUID;

public class UserProfileException {

    // 유저 프로필 중복 추가
    public static class UserProfileDuplicate extends BusinessException {
        public UserProfileDuplicate(String point, UUID id){
            super(ErrorCode.USER_PROFILE_DUPLICATE, point, id.toString());
        }
    }

    // 유저 프로필 없는 경우
    public static class UserProfileNotFound extends BusinessException {
        public UserProfileNotFound(String point, UUID id){
            super(ErrorCode.USER_PROFILE_NOT_FOUND, point, id.toString());
        }
    }


}
