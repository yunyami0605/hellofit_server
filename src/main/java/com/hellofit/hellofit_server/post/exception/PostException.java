package com.hellofit.hellofit_server.post.exception;

import com.hellofit.hellofit_server.global.exception.BusinessException;
import com.hellofit.hellofit_server.global.exception.ErrorCode;

import java.util.UUID;

public class PostException {

    public static class NotFound extends BusinessException {
        public NotFound(String point, UUID id) {
            super(ErrorCode.POST_NOT_FOUND, point, id.toString());
        }
    }
}
