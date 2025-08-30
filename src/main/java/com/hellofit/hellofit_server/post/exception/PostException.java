package com.hellofit.hellofit_server.post.exception;

import com.hellofit.hellofit_server.global.exception.BusinessException;
import com.hellofit.hellofit_server.global.exception.ErrorCode;

public class PostException  {

    public static class NotFound extends BusinessException{
        public NotFound(String point, String value){
            super(ErrorCode.POST_NOT_FOUND, point, value);
        }
    }
}
