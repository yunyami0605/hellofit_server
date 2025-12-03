package com.hellofit.hellofit_server.chat;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice(basePackages = "com.hellofit.hellofit_server.chat")
public class ChatExceptionHandler {

    record ChatError(String code, String message, String field) {}

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ChatError> handleValidation(MethodArgumentNotValidException ex) {
        String field = ex.getBindingResult().getFieldError() != null ? ex.getBindingResult().getFieldError().getField() : null;
        String message = ex.getBindingResult().getFieldError() != null ? ex.getBindingResult().getFieldError().getDefaultMessage() : "INVALID_INPUT";
        return ResponseEntity.badRequest().body(new ChatError("INVALID_INPUT", message, field));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ChatError> handleIllegalArg(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(new ChatError("INVALID_INPUT", ex.getMessage(), null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ChatError> handleOthers(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ChatError("INTERNAL_ERROR", "Unexpected error", null));
    }
}


