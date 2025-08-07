package com.hellofit.hellofit_server.auth;

import com.hellofit.hellofit_server.auth.dto.LoginRequestDto;
import com.hellofit.hellofit_server.auth.dto.LoginResponseDto;
import com.hellofit.hellofit_server.auth.dto.SignupRequestDto;
import com.hellofit.hellofit_server.auth.token.RefreshToken;
import com.hellofit.hellofit_server.auth.token.RefreshTokenRepository;
import com.hellofit.hellofit_server.global.jwt.JwtTokenProvider;
import com.hellofit.hellofit_server.user.User;
import com.hellofit.hellofit_server.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    public UUID signup(SignupRequestDto request) {
        // 1 : 이메일 중복 체크
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        // 2. 비밀번호 암호화
        String encryptedPassword = passwordEncoder.encode(request.getPassword());

        // 3. User 엔티티 생성
        User user = User.builder()
                .email(request.getEmail())
                .password(encryptedPassword)
                .nickname(request.getNickname())
                .isPrivacyAgree(request.getIsPrivacyAgree())
                .build();

        // 4. 저장 ID 반환
        return userRepository.save(user).getId();
    }

    public LoginResponseDto login(LoginRequestDto request){
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException(("가입되지 않는 이메일입니다.")));

        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getEmail());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId(), user.getEmail());

        refreshTokenRepository.save(RefreshToken.builder().userId(user.getId()).token(accessToken).build());

        return LoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
