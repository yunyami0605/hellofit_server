package com.hellofit.hellofit_server.global.dto;

import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.Map;

/*
* error 응답 class
* */
public record ApiErrorResponse(
        int status,
        String message,
        String traceId,
        Instant timestamp,
        Map<String, Object> errorData
) {
    public static ApiErrorResponse of(HttpStatus status, String message, String traceId, Map<String, Object> errors) {
        return new ApiErrorResponse(status.value(), message, traceId, Instant.now(), errors);
    }
    public static ApiErrorResponse of(HttpStatus status, String message, String traceId) {
        return of(status, message, traceId, null);
    }
}