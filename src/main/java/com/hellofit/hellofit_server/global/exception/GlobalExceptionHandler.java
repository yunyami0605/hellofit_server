package com.hellofit.hellofit_server.global.exception;

import com.hellofit.hellofit_server.global.dto.ApiErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.ConstraintViolationException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice(basePackages = "com.hellofit.hellofit_server")
public class GlobalExceptionHandler {

    private String traceId() { return MDC.get("traceId"); }

    // 1) 비즈니스 예외 (도메인)
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorResponse> handleBusiness(BusinessException ex) {
        var code = ex.getCode();

        ApiErrorResponse body = ApiErrorResponse.of(
                code.getStatus(),           // enum이 가진 HttpStatus
                code.getMessage(),   // 기본 노출 메시지
                traceId()
        );
        return ResponseEntity.status(code.getStatus()).body(body);
    }

    // 2) Spring의 상태 지정 예외
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiErrorResponse> handleResponseStatus(ResponseStatusException ex) {
        HttpStatus status = ex.getStatusCode() instanceof HttpStatus http ? http : HttpStatus.BAD_REQUEST;
        ApiErrorResponse body = ApiErrorResponse.of(status, ex.getReason(), traceId());
        return ResponseEntity.status(status).body(body);
    }

    // 3) 인증/인가
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiErrorResponse> handleAuth(AuthenticationException ex) {
        ApiErrorResponse body = ApiErrorResponse.of(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", traceId());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        ApiErrorResponse body = ApiErrorResponse.of(HttpStatus.FORBIDDEN, "FORBIDDEN",  traceId());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    // 4) 검증 오류 - @Valid 바인딩 (RequestBody)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, Object> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(f ->
                errors.put(f.getField(), f.getDefaultMessage()));
        ApiErrorResponse body = ApiErrorResponse.of(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR"
                , traceId(), errors);
        return ResponseEntity.badRequest().body(body);
    }

    // 5) 검증 오류 - @Validated on @RequestParam/@PathVariable
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, Object> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(v -> errors.put(v.getPropertyPath().toString(), v.getMessage()));
        ApiErrorResponse body = ApiErrorResponse.of(HttpStatus.BAD_REQUEST, "CONSTRAINT_VIOLATION", traceId(), errors);
        return ResponseEntity.badRequest().body(body);
    }

    // 6) 타입 불일치 (예: PathVariable UUID 파싱 실패)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        ApiErrorResponse body = ApiErrorResponse.of(HttpStatus.BAD_REQUEST, "TYPE_MISMATCH", traceId());
        return ResponseEntity.badRequest().body(body);
    }

    // 7) JSON 파싱/바디 누락
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleNotReadable(HttpMessageNotReadableException ex) {
        ApiErrorResponse body = ApiErrorResponse.of(HttpStatus.BAD_REQUEST, "MESSAGE_NOT_READABLE",
                 traceId());
        return ResponseEntity.badRequest().body(body);
    }

    // 8) 필수 파라미터 누락, 바인딩 오류
    @ExceptionHandler({MissingServletRequestParameterException.class, BindException.class})
    public ResponseEntity<ApiErrorResponse> handleMissingParam(Exception ex) {
        ApiErrorResponse body = ApiErrorResponse.of(HttpStatus.BAD_REQUEST, "BAD_REQUEST", traceId());
        return ResponseEntity.badRequest().body(body);
    }

    // 9) 메서드/미디어 타입 미지원
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        ApiErrorResponse body = ApiErrorResponse.of(HttpStatus.METHOD_NOT_ALLOWED, "METHOD_NOT_ALLOWED", traceId());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(body);
    }



    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiErrorResponse> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex) {
        ApiErrorResponse body = ApiErrorResponse.of(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "UNSUPPORTED_MEDIA_TYPE", traceId());
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(body);
    }

    // 10) 존재하지 않는 URL (404 Not Found)
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(NoHandlerFoundException ex) {
        ApiErrorResponse body = ApiErrorResponse.of(
                HttpStatus.NOT_FOUND,
                "NOT_FOUND",
                traceId(),
                Map.of("path", ex.getRequestURL())
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    // 11) 마지막 방어선
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleEtc(Exception ex) {
        log.error("Unhandled exception", ex);
        ApiErrorResponse body = ApiErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR",
                 traceId());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
