package com.hellofit.hellofit_server.global.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

/**
 * 401 응답을 JSON 바디로 내려주는 EntryPoint.
 * - 토큰 만료/무효를 구분해 클라이언트가 분기할 수 있도록 code/message 제공
 */
@Slf4j
public class JsonAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        String code = "UNAUTHORIZED";
        String message = "유효하지 않은 인증입니다.";

        log.info("here!!!");
        log.info("here!!!");
        log.info("here!!!");

        Throwable cause = authException.getCause();
        if (cause instanceof ExpiredJwtException) {
            code = "TOKEN_EXPIRED";
            message = "토큰이 만료되었습니다.";
        } else if (cause instanceof JwtException || cause instanceof IllegalArgumentException) {
            code = "TOKEN_INVALID";
            message = "유효하지 않은 토큰입니다.";
        }

        log.info(message);

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":\"" + code + "\",\"message\":\"" + message + "\"}");
        response.getWriter().flush();
    }
}


