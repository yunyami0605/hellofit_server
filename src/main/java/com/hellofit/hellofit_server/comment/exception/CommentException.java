package com.hellofit.hellofit_server.comment.exception;

import com.hellofit.hellofit_server.global.exception.BusinessException;
import com.hellofit.hellofit_server.global.exception.ErrorCode;

import java.util.UUID;

public class CommentException {

    public static class NotFound extends BusinessException {
        public NotFound(String point, UUID id) {
            super(ErrorCode.COMMENT_NOT_FOUND, point, id.toString());
        }
    }
}
