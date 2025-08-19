package com.hellofit.hellofit_server.user.exception;

import com.hellofit.hellofit_server.global.exception.BusinessException;
import com.hellofit.hellofit_server.global.exception.ErrorCode;

import java.util.UUID;

/*
* 유저 정보 없는 예외 처리
* */
public class UserNotFoundException extends BusinessException {
    public UserNotFoundException(UUID id) {
        super(ErrorCode.USER_NOT_FOUND, id.toString());
    }
}
