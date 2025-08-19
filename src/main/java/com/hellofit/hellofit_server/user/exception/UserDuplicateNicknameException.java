package com.hellofit.hellofit_server.user.exception;

import com.hellofit.hellofit_server.global.exception.BusinessException;
import com.hellofit.hellofit_server.global.exception.ErrorCode;

import java.util.UUID;

/*
* 중복 닉네임 등록 요청 예외처리
* */
public class UserDuplicateNicknameException extends BusinessException {
    public UserDuplicateNicknameException(UUID id) {
        super(ErrorCode.DUPLICATE_NICKNAME, id.toString());
    }
}
