package com.hellofit.hellofit_server.global.jwt;

import com.hellofit.hellofit_server.auth.constants.TokenStatus;
import com.hellofit.hellofit_server.user.UserEntity;
import com.hellofit.hellofit_server.user.UserRepository;
import com.hellofit.hellofit_server.user.exception.UserNotFoundException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
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

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final AuthenticationEntryPoint authenticationEntryPoint; // ✅ 추가
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain)
            throws ServletException, IOException {

        // 1. 요청에서 토큰 가져옴
        String token = resolveToken(request);

        if (StringUtils.hasText(token)) {
            try {
                if (jwtTokenProvider.validateToken(token).equals(TokenStatus.VALID) &&
                        SecurityContextHolder.getContext().getAuthentication() == null) {

                    // 2. token에서 user id, role 가져옴
                    UUID userId = jwtTokenProvider.getUserIdFromToken(token);

                    // 권한 추출(예: ["ROLE_USER", "ROLE_ADMIN"])
                    String role = jwtTokenProvider.getRoleFromToken(token);

                    // 3. 권한 객체 생성 및 request에 ip, sessionId 추가
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);

                    UserEntity user =  userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

                    // 인증 정보 구현체 (토큰 정보, role, 인증여부)
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    user,                  // principal = UUID
                                    null,                    // credentials
                                    List.of(authority)       // 단일 권한
                            );

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                }else{
                    SecurityContextHolder.clearContext();
                    authenticationEntryPoint.commence(
                            request, response,
                            new InsufficientAuthenticationException("Invalid token")
                    );
                    return;
                }
            } catch (Exception ex) {
                SecurityContextHolder.clearContext();
                // 핵심: 여기서 즉시 401 반환
                authenticationEntryPoint.commence(
                        request,
                        response,
                        new InsufficientAuthenticationException("Invalid token", ex)
                );
                return;
            }
        }else{
            // 토큰이 없는 경우 -> 401
             authenticationEntryPoint.commence(request, response,
                 new InsufficientAuthenticationException("Missing token"));
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
                || pathMatcher.match("/auth/refresh", path)
                || pathMatcher.match("/v3/api-docs/**", path)
                || pathMatcher.match("/swagger-ui/**", path)
                || pathMatcher.match("/swagger-resources/**", path)
                || pathMatcher.match("/h2-console/**", path);
    }
}
