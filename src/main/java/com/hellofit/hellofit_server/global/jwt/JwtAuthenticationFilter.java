package com.hellofit.hellofit_server.global.jwt;

import com.hellofit.hellofit_server.auth.constants.TokenStatus;
import com.hellofit.hellofit_server.user.UserEntity;
import com.hellofit.hellofit_server.user.UserRepository;
import com.hellofit.hellofit_server.user.exception.UserException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain)
        throws ServletException, IOException {

        // 1. 요청에서 토큰 가져옴
        String token = resolveToken(request);

        if (!StringUtils.hasText(token)) {
            // 토큰 없는 경우 패스
            filterChain.doFilter(request, response);
            return;
        }

        try {
            TokenStatus status = jwtTokenProvider.validateToken(token);
            if (status != TokenStatus.VALID) {
                throw new InsufficientAuthenticationException("Token invalid: " + status);
            }

            UUID userId = jwtTokenProvider.getUserIdFromToken(token);
            String role = jwtTokenProvider.getRoleFromToken(token);

            UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException.UserNotFoundException("JwtAuthenticationFilter > doFilterInternal", userId));

            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                    user.getId(),
                    null,
                    List.of(new SimpleGrantedAuthority(role))
                );

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        } catch (Exception ex) {
            SecurityContextHolder.clearContext();
            authenticationEntryPoint.commence(
                request, response,
                new InsufficientAuthenticationException("Invalid token", ex)
            );

            log.warn("JWT 인증 실패: {}", ex.getMessage());
            return;
        }

        filterChain.doFilter(request, response);
    }

    // request 토큰 검증 및 반환
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // 해당 경로는 토큰 검사 안함
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return pathMatcher.match("/auth/login", path)
            || pathMatcher.match("/auth/signup", path)
            || pathMatcher.match("/auth/xc", path)
            || pathMatcher.match("/auth/check-nickname", path)
            || pathMatcher.match("/auth/refresh", path)
            || pathMatcher.match("/v3/api-docs/**", path)
            || pathMatcher.match("/swagger-ui/**", path)
            || pathMatcher.match("/swagger-resources/**", path)
            || pathMatcher.match("/h2-console/**", path);
    }
}
