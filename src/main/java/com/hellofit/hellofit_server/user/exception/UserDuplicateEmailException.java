package com.hellofit.hellofit_server.user.exception;

import com.hellofit.hellofit_server.global.exception.BusinessException;
import com.hellofit.hellofit_server.global.exception.ErrorCode;

import java.util.UUID;

/*
* 이메일 중복 예외처리
* */
public class UserDuplicateEmailException extends BusinessException {
    public UserDuplicateEmailException(UUID id) {
        super(ErrorCode.DUPLICATE_EMAIL, id.toString());
    }
}
