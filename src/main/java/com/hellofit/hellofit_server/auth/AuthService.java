package com.hellofit.hellofit_server.auth;

import com.hellofit.hellofit_server.auth.constants.TokenStatus;
import com.hellofit.hellofit_server.auth.dto.*;
import com.hellofit.hellofit_server.auth.exception.*;
import com.hellofit.hellofit_server.auth.token.RefreshTokenEntity;
import com.hellofit.hellofit_server.auth.token.RefreshTokenRepository;
import com.hellofit.hellofit_server.comment.CommentRepository;
import com.hellofit.hellofit_server.global.constants.AuthConstant;
import com.hellofit.hellofit_server.global.jwt.JwtTokenProvider;
import com.hellofit.hellofit_server.post.PostRepository;
import com.hellofit.hellofit_server.user.UserEntity;
import com.hellofit.hellofit_server.user.UserRepository;
import com.hellofit.hellofit_server.user.UserService;
import com.hellofit.hellofit_server.user.dto.UserMappingResponseDto;
import com.hellofit.hellofit_server.user.enums.LoginProvider;
import com.hellofit.hellofit_server.user.exception.UserException;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final SocialClient socialClient;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    /**
     * [서비스 로직] 이메일 회원가입
     */
    public AuthResponseDto.Access signupByEmail(AuthRequestDto.EmailSignup request, HttpServletResponse response) {
        // 1. 이메일 중복 여부 체크
        userService.checkDuplicateEmail(request.getEmail(), "AuthService > signup");

        // 2. : 닉네임 중복 체크
        userService.checkDuplicateNickname(request.getNickname(), "AuthService > signup");

        // 3. 비밀번호 암호화
        String encryptedPassword = passwordEncoder.encode(request.getPassword());

        // 4. 유저 정보 생성
        UserEntity userEntity = UserEntity.createEmail(
            request.getEmail(),
            encryptedPassword,
            request.getNickname(),
            request.getIsPrivacyAgree());

        // 5. 유저 정보 저장
        UserEntity savedUserEntity = userRepository.save(userEntity);

        // 6. token 설정
        return setAuthToken(savedUserEntity, response);
    }

    /**
     * [서비스 로직] 이메일 로그인
     */
    @Transactional
    public AuthResponseDto.Access loginByEmail(AuthRequestDto.EmailLogin request, HttpServletResponse response) {
        // 1. 이메일로 유저 조회 -> 없으면 에러 반환
        UserEntity userEntity = userRepository.findByEmail(request.getEmail())
                                              .orElseThrow(() -> new AuthException.WrongLoginForm("AuthService > loginByEmail", request.getEmail()));

        // 2. 비밀번호 확인
        if (!passwordEncoder.matches(request.getPassword(), userEntity.getPassword())) {
            throw new AuthException.WrongLoginForm("AuthService > loginByEmail", request.getPassword());
        }

        // TODO: 나중에 디바이스 별 로그인 로직 추가하면, 로직 수정하기
        return setAuthToken(userEntity, response);
    }

    /**
     * [서비스 로직] 소셜 회원가입
     */
    public AuthResponseDto.Access signupBySocial(AuthRequestDto.SocialSignup request, HttpServletResponse response) {
        // 1. 이메일 중복 여부 체크
        userService.checkDuplicateEmail(request.getEmail(), "AuthService > signupBySocial");

        // 2. : 닉네임 중복 체크
        userService.checkDuplicateNickname(request.getNickname(), "AuthService > signupBySocial");

        Optional<UserEntity> isUserExist = userRepository.findBySocialIdAndProvider(request.getSocialId(), request.getProvider());

        if (isUserExist.isPresent()) {
            throw new UserException.UserDuplicateSocialException("AuthService > signupBySocial", request.getSocialId());
        }

        // 3. 유저 정보 생성
        UserEntity userEntity = UserEntity.createSocial(
            request.getEmail(),
            request.getNickname(),
            request.getIsPrivacyAgree(),
            request.getSocialId(),
            request.getProvider());

        // 4. 유저 정보 저장
        UserEntity savedUserEntity = userRepository.save(userEntity);

        // 5. token 설정
        return setAuthToken(savedUserEntity, response);
    }

    /**
     * [서비스 로직] 소셜 로그인 -> 404 면 회원가입 페이지로 이동
     */
    public Map<String, Object> loginBySocial(AuthRequestDto.SocialLogin request, HttpServletResponse response) {

        UserEntity userEntity;

        switch (request.getProvider()) {
            case KAKAO -> {
                // 1. 인가 코드 -> 카카오 토큰 발급
                AuthResponseDto.KakaoToken kakaoToken =
                    socialClient.getKakaoAccessToken(request.getCode());

                // 2. 액세스 토큰 -> 사용자 정보
                AuthResponseDto.KakaoUser kakaoUser =
                    socialClient.getKakaoUser(kakaoToken.getAccessToken());

                // 3. DB 조회
                return userRepository.findBySocialIdAndProvider(String.valueOf(kakaoUser.getId()), LoginProvider.KAKAO)
                                     .<Map<String, Object>>map(user -> {
                                         // 기존 유저 → 로그인 성공 응답
                                         var token = setAuthToken(user, response);
                                         return Map.of(
                                             "status", "SUCCESS",
                                             "accessToken", token.getAccess()
                                         );
                                     })
                                     .orElseGet(() -> Map.of(
                                         "status", "SIGNUP_REQUIRED",
                                         "provider", "KAKAO",
                                         "socialId", kakaoUser.getId(),
                                         "message", "회원가입이 필요합니다."
                                     ));

            }
            // 추가 소셜 적용
            default ->
                throw new SocialAuthException.UnsupportedProvider("AuthService > loginBySocial", "지원하지 않는 소셜 로그인 provider: " + request.getProvider());
        }


    }

    /**
     * [서비스 로직] ac 토큰 재발급
     */
    public TokenRefreshResponseDto refreshAccessToken(HttpServletRequest request) {
        String refreshToken = getCookieValue(request, AuthConstant.REFRESH_TOKEN_COOKIE).orElseThrow(() -> new AuthException.TokenInvalid("AuthService > refreshAccessToken", AuthConstant.REFRESH_TOKEN_COOKIE, ""));
        String xsrfToken = getCookieValue(request, AuthConstant.XSRF_TOKEN_COOKIE).orElseThrow(() -> new AuthException.TokenInvalid("AuthService > refreshAccessToken", AuthConstant.XSRF_TOKEN_COOKIE, ""));

        // 1. 유효성 검사 -> 만료되면 에러 배출
        // 1-1. rf 검사
        TokenStatus validatedTokenStatus = jwtTokenProvider.validateToken(refreshToken);
        if (validatedTokenStatus.equals(TokenStatus.EXPIRED)) {
            // 만료된 rf 토큰으로 접근 시 -> db 저장된 rf 토큰 삭제
            UUID userId = jwtTokenProvider.getUserIdFromToken(refreshToken);

            refreshTokenRepository.deleteById(userId);

            throw new AuthException.TokenExpired("ac refresh", refreshToken);
        } else if (validatedTokenStatus.equals(TokenStatus.INVALID)) {
            // 유효하지 않는 rf 토큰으로 접근 시,
            throw new AuthException.TokenInvalid("AuthService > refreshAccessToken1", AuthConstant.REFRESH_TOKEN_COOKIE, refreshToken);
        }

        String xsrfHeader = request.getHeader("X-XSRF-TOKEN");

        // 1-2. xsrf 검사
        if (xsrfHeader == null || !xsrfHeader.equals(xsrfToken)) {
            throw new AuthException.TokenInvalid("AuthService > refreshAccessToken2", AuthConstant.XSRF_TOKEN_COOKIE, xsrfToken);
        }

        // 2. 토큰에서 userId 추출
        UUID userId = jwtTokenProvider.getUserIdFromToken(refreshToken);

        // 3. 저장된 Refresh Token과 비교
        RefreshTokenEntity savedToken = refreshTokenRepository.findById(userId)
                                                              .orElseThrow(() -> new AuthException.TokenInvalid("AuthService > refreshAccessToken3", AuthConstant.REFRESH_TOKEN_COOKIE, refreshToken));

        if (!savedToken.getToken()
                       .equals(refreshToken)) {
            throw new AuthException.TokenInvalid("AuthService > refreshAccessToken4", AuthConstant.REFRESH_TOKEN_COOKIE, refreshToken);
        }

        // 4. 새 Access Token 발급
        String newAccessToken = jwtTokenProvider.generateAccessToken(userId, null);

        return TokenRefreshResponseDto.builder()
                                      .accessCookie(newAccessToken)
                                      .build();
    }

    /**
     * [서비스 로직] 로그아웃 -> rf token 제거
     */
    public void logout(UUID userId) {
        refreshTokenRepository.deleteById(userId);
    }

    /**
     * [서비스 로직] 본인 유저 정보 조회
     */
    public UserMappingResponseDto.Detail getAuthInfo(UUID userId) {
        UserEntity user = this.userService.getUserById(userId, "AuthService > getAuthInfo");
        Long postCount = this.postRepository.countByUser(user);
        Long commentCount = this.commentRepository.countByUser(user);

        return UserMappingResponseDto.Detail.fromEntity(user, postCount, commentCount);
    }

    /**
     * [서비스 로직] XSRF 토큰 발급
     */
    public Boolean postXSRFToken(HttpServletResponse response) {
        setXSRFToken(response);

        return true;
    }

    /**
     * [서비스 로직] 닉네임 중복 여부 확인
     */
    public AuthResponseDto.NicknameDuplicate checkNicknameDuplicate(String nickname) {
        Boolean isDuplicate = this.userRepository.findByNickname(nickname)
                                                 .isPresent();

        return AuthResponseDto.NicknameDuplicate.builder()
                                                .isDuplicate(isDuplicate)
                                                .build();
    }


    /*
     * [헬퍼 메서드] 쿠키 조회 및 반환
     * */
    public Optional<String> getCookieValue(HttpServletRequest request, String cookieName) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName()
                          .equals(cookieName)) {
                    return Optional.of(cookie.getValue());
                }
            }
        }
        return Optional.empty();
    }


    /*
     * [헬퍼 메서드] XSRF Token 추가 및 헤더 설정
     * */
    public void setXSRFToken(HttpServletResponse response) {
        String xsrfToken = UUID.randomUUID()
                               .toString();

        ResponseCookie xsrfCookie = ResponseCookie.from(AuthConstant.XSRF_TOKEN_COOKIE, xsrfToken)
                                                  .path("/")
                                                  .build()
            ;

        response.addHeader(HttpHeaders.SET_COOKIE, xsrfCookie.toString());
    }

    /*
     * [헬퍼 메서드]ac, rf, xsrf token 생성 및 헤더 설정
     * */
    @Transactional
    public AuthResponseDto.Access setAuthToken(UserEntity user, HttpServletResponse response) {
        String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), null);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId(), null);

        RefreshTokenEntity tokenEntity = refreshTokenRepository.findById(user.getId())
                                                               .orElse(null);

        if (tokenEntity == null) {
            // 신규 저장
            tokenEntity = RefreshTokenEntity.create(refreshToken, user);
            refreshTokenRepository.save(tokenEntity);
        } else {
            // 기존 값만 업데이트
            tokenEntity.updateToken(refreshToken);
        }

        ResponseCookie refreshCookie = ResponseCookie.from(AuthConstant.REFRESH_TOKEN_COOKIE, refreshToken)
                                                     .httpOnly(true)
                                                     .sameSite("None")
                                                     .path("/")
                                                     .maxAge(AuthConstant.REFRESH_TOKEN_LIFETIME)
                                                     .build()
            ;

        setXSRFToken(response);
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        return AuthResponseDto.Access.builder()
                                     .access(accessToken)
                                     .build();
    }
}
