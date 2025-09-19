package com.hellofit.hellofit_server.image.exception;

import com.hellofit.hellofit_server.global.exception.BusinessException;
import com.hellofit.hellofit_server.global.exception.ErrorCode;

import java.util.UUID;

public class ImageException {
    public static class ImageNotFoundException extends BusinessException {
        public ImageNotFoundException(String point, UUID id) {
            super(ErrorCode.IMAGE_NOT_FOUND, point, id.toString());
        }

    }
}
