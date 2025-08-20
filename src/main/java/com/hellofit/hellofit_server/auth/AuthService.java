package com.hellofit.hellofit_server.auth;

import com.hellofit.hellofit_server.auth.constants.TokenStatus;
import com.hellofit.hellofit_server.auth.dto.*;
import com.hellofit.hellofit_server.auth.exception.NotMatchPasswordException;
import com.hellofit.hellofit_server.auth.exception.TokenExpiredException;
import com.hellofit.hellofit_server.auth.exception.TokenInvalidException;
import com.hellofit.hellofit_server.auth.exception.UnAuthorizedEmailException;
import com.hellofit.hellofit_server.auth.token.RefreshTokenEntity;
import com.hellofit.hellofit_server.auth.token.RefreshTokenRepository;
import com.hellofit.hellofit_server.global.constants.AuthConstant;
import com.hellofit.hellofit_server.global.jwt.JwtTokenProvider;
import com.hellofit.hellofit_server.user.UserEntity;
import com.hellofit.hellofit_server.user.UserRepository;
import com.hellofit.hellofit_server.user.UserService;
import com.hellofit.hellofit_server.user.dto.UserMappingResponseDto;
import com.hellofit.hellofit_server.user.exception.UserNotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    /*
     * ac, rf, xsrf token 생성 및 헤더 설정
     * */
    public LoginResponseDto setAuthToken(UserEntity user, HttpServletResponse response){
        // 1. ac, rf token 생성
        String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), null);

        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId(), null);

        String xsrfToken = UUID.randomUUID().toString();

        // 2. rf Token 저장
        refreshTokenRepository.save(
                RefreshTokenEntity.builder()
                        .user(user)
                        .token(refreshToken)
                        .build()
        );

        // 3. 쿠키에 설정해서 응답
        ResponseCookie refreshCookie = ResponseCookie.from(AuthConstant.REFRESH_TOKEN_COOKIE, refreshToken)
                .httpOnly(true)
//                .secure(true)
                .sameSite("None")
                .path("/")
                // .domain(".example.com")
                .maxAge(AuthConstant.REFRESH_TOKEN_LIFETIME)
                .build();

        ResponseCookie xsrfCookie = ResponseCookie.from(AuthConstant.XSRF_TOKEN_COOKIE, xsrfToken)
                .path("/")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, xsrfCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        // 4. ac, rf 토큰 반환 반환
        return LoginResponseDto.builder().access(accessToken).id(user.getId()).build();
    }

    // 회원가입
    public LoginResponseDto signup(SignupRequestDto request, HttpServletResponse response) {
        // 1. 이메일 중복 여부 체크
        userService.checkDuplicateEmail(request.getEmail());

        // 2. : 닉네임 중복 체크
        userService.checkDuplicateNickname(request.getNickname());

        // 3. 비밀번호 암호화
        String encryptedPassword = passwordEncoder.encode(request.getPassword());

        // 4. 유저 정보 생성
        UserEntity userEntity = UserEntity.builder()
                .email(request.getEmail())
                .password(encryptedPassword)
                .nickname(request.getNickname())
                .isPrivacyAgree(request.getIsPrivacyAgree())
                .build();

        // 5. 유저 정보 저장
        UUID userId = userRepository.save(userEntity).getId();

        // 6. token 설정
        return setAuthToken(userEntity, response);
    }

    // 로그인
    public LoginResponseDto login(LoginRequestDto request, HttpServletResponse response) {
        // 1. 이메일로 유저 조회 -> 없으면 에러 반환
        UserEntity userEntity = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnAuthorizedEmailException());

        // 2. 비밀번호 확인
        if (!passwordEncoder.matches(request.getPassword(), userEntity.getPassword())) {
            throw new NotMatchPasswordException();
        }

        return setAuthToken(userEntity, response);
    }

    /*
    * 쿠키 조회 및 반환
    * */
    public Optional<String> getCookieValue(HttpServletRequest request, String cookieName) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals(cookieName)) {
                    return Optional.of(cookie.getValue());
                }
            }
        }
        return Optional.empty();
    }

    /*
    * ac 토큰 재발급 서비스 로직
    * */
    public TokenRefreshResponseDto refreshAccessToken(HttpServletRequest request) {
        String refreshToken = getCookieValue(request, AuthConstant.REFRESH_TOKEN_COOKIE).orElseThrow(() -> new TokenInvalidException(AuthConstant.REFRESH_TOKEN_COOKIE, ""));
        String xsrfToken = getCookieValue(request, AuthConstant.XSRF_TOKEN_COOKIE).orElseThrow(() -> new TokenInvalidException(AuthConstant.XSRF_TOKEN_COOKIE, ""));

        // 1. 유효성 검사 -> 만료되면 에러 배출
        // 1-1. rf 검사
        TokenStatus validatedTokenStatus =  jwtTokenProvider.validateToken(refreshToken);
        if (validatedTokenStatus.equals(TokenStatus.EXPIRED)) {
            // 만료된 rf 토큰으로 접근 시 -> db 저장된 rf 토큰 삭제
            UUID userId = jwtTokenProvider.getUserIdFromToken(refreshToken);

            refreshTokenRepository.deleteById(userId);

            throw new TokenExpiredException("ac refresh", refreshToken);
        }else if(validatedTokenStatus.equals(TokenStatus.INVALID)){
            // 유효하지 않는 rf 토큰으로 접근 시,
            throw new TokenInvalidException("rf invalid", AuthConstant.REFRESH_TOKEN_COOKIE, refreshToken);
        }

        String xsrfHeader = request.getHeader("X-XSRF-TOKEN");

        // 1-2. xsrf 검사
        if(xsrfHeader == null || !xsrfHeader.equals(xsrfToken)){
            throw new TokenInvalidException("xsrf invalid", AuthConstant.XSRF_TOKEN_COOKIE, xsrfToken);
        }

        // 2. 토큰에서 userId 추출
        UUID userId = jwtTokenProvider.getUserIdFromToken(refreshToken);

        // 3. 저장된 Refresh Token과 비교
        RefreshTokenEntity savedToken = refreshTokenRepository.findById(userId)
                .orElseThrow(() -> new TokenInvalidException("no saved rf", AuthConstant.REFRESH_TOKEN_COOKIE, refreshToken));

        if (!savedToken.getToken().equals(refreshToken)) {
            throw new TokenInvalidException("not match rf", AuthConstant.REFRESH_TOKEN_COOKIE, refreshToken);
        }

        // 4. 새 Access Token 발급
        String newAccessToken = jwtTokenProvider.generateAccessToken(userId, null);

        return TokenRefreshResponseDto.builder()
                .accessCookie(newAccessToken)
                .build();
    }

    // 로그아웃 -> rf token 제거
    public void logout(UUID userId){
        refreshTokenRepository.deleteById(userId);
    }

    /*
    * 본인 유저 정보 조회 서비스 로직
    * */
    public UserMappingResponseDto.Summary getAuthInfo(UUID userId){
        return this.userRepository.findById(userId).map((value) -> UserMappingResponseDto.Summary.fromEntity(value)).orElseThrow(() -> new UserNotFoundException(userId));
    }


}
