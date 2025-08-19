package com.hellofit.hellofit_server.auth;

import com.hellofit.hellofit_server.auth.dto.*;
import com.hellofit.hellofit_server.auth.exception.NotMatchPasswordException;
import com.hellofit.hellofit_server.auth.exception.TokenInvalidException;
import com.hellofit.hellofit_server.auth.exception.UnAuthorizedEmailException;
import com.hellofit.hellofit_server.auth.token.RefreshTokenEntity;
import com.hellofit.hellofit_server.auth.token.RefreshTokenRepository;
import com.hellofit.hellofit_server.global.jwt.JwtTokenProvider;
import com.hellofit.hellofit_server.user.UserEntity;
import com.hellofit.hellofit_server.user.UserRepository;
import com.hellofit.hellofit_server.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    // 회원가입
    public UUID signup(SignupRequestDto request) {
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
        return userRepository.save(userEntity).getId();
    }

    // 로그인
    public LoginResponseDto login(LoginRequestDto request) {
        // 1. 이메일로 유저 조회 -> 없으면 에러 반환
        UserEntity userEntity = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnAuthorizedEmailException());

        // 2. 비밀번호 확인
        if (!passwordEncoder.matches(request.getPassword(), userEntity.getPassword())) {
            throw new NotMatchPasswordException();
        }

        // 3. ac, rf token 생성
        String accessToken = jwtTokenProvider.generateAccessToken(userEntity.getId(), null);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userEntity.getId(), null);

        // 4. rf Token 저장
        refreshTokenRepository.save(
                RefreshTokenEntity.builder()
                        .userId(userEntity.getId())
                        .token(refreshToken)
                        .build()
        );

        // 5. ac, rf 토큰 반환 반환
        return LoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // Access Token 재발급
    public TokenRefreshResponseDto refreshAccessToken(TokenRefreshRequestDto request) {
        String refreshToken = request.getRefreshToken();

        // 1. 유효성 검사 -> 만료되면 에러 배출
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 Refresh Token입니다.");
        }

        // 2. 토큰에서 userId 추출
        UUID userId = jwtTokenProvider.getUserIdFromToken(refreshToken);

        // 3. 저장된 Refresh Token과 비교
        RefreshTokenEntity savedToken = refreshTokenRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("저장된 Refresh Token이 없습니다."));

        if (!savedToken.getToken().equals(refreshToken)) {
            throw new TokenInvalidException(refreshToken);
        }

        // 4. 새 Access Token 발급
        String newAccessToken = jwtTokenProvider.generateAccessToken(userId, null);

        return TokenRefreshResponseDto.builder()
                .accessToken(newAccessToken)
                .build();
    }

    // 로그아웃 -> rf token 제거
    public void logout(UUID userId){
        refreshTokenRepository.deleteById(userId);
    }
}
