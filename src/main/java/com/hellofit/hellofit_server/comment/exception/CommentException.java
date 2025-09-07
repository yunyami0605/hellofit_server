package com.hellofit.hellofit_server.comment.exception;

import com.hellofit.hellofit_server.global.exception.BusinessException;
import com.hellofit.hellofit_server.global.exception.ErrorCode;

public class CommentException {

    public static class NotFound extends BusinessException {
        public NotFound(String point, String value) {
            super(ErrorCode.COMMENT_NOT_FOUND, point, value);
        }
    }
}
