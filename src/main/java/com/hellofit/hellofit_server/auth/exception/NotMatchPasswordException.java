package com.hellofit.hellofit_server.auth.exception;

import com.hellofit.hellofit_server.global.exception.BusinessException;
import com.hellofit.hellofit_server.global.exception.ErrorCode;

public class NotMatchPasswordException extends BusinessException {
    public NotMatchPasswordException() {
        super(ErrorCode.NOT_MATCH_PASSWORD, "");
    }
}
