package com.hellofit.hellofit_server.auth;

import com.hellofit.hellofit_server.auth.dto.*;
import com.hellofit.hellofit_server.auth.token.RefreshTokenEntity;
import com.hellofit.hellofit_server.auth.token.RefreshTokenRepository;
import com.hellofit.hellofit_server.global.jwt.JwtTokenProvider;
import com.hellofit.hellofit_server.user.UserEntity;
import com.hellofit.hellofit_server.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock JwtTokenProvider jwtTokenProvider;
    @Mock RefreshTokenRepository refreshTokenRepository;

    @InjectMocks AuthService authService;

    @Test
    void signupWhenEmailExistThenFail(){
        /*
        *  signup -> 이미 존재하는 이메일로 테스트할 경우 중복 fail이 발생하는지 테스트
        */
        // given
        var request = new SignupRequestDto("test@test.com", "test1234", "test", true);
        UserEntity existUser = UserEntity.builder().email("test@test.com").build();

        // 이메일 유저 조회 반환 설정
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(existUser));

        // when
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> authService.signup(request));

        // then
        assertThat(ex).hasMessageContaining("이미 존재하는 이메일입니다.");
        verify(userRepository, times(1)).findByEmail(request.getEmail());
    }

    @Test
    void signupThenSuccess(){
        /*
        * signup -> 입력에 따른 유저 회원가입 (=유저생성) 되는지 여부
        */
        // given
        UUID userId = UUID.randomUUID();
        var savedUserEntity = UserEntity.builder().id(userId).build();
        var request = new SignupRequestDto("test@test.com", "test1234", "test", true);

        when(this.userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(this.passwordEncoder.encode(request.getPassword())).thenReturn("en_test1234");
        when(this.userRepository.save(any(UserEntity.class))).thenReturn(savedUserEntity);

        // when
        UUID createdUserId = this.authService.signup(request);

        // then
        assertThat(createdUserId).isEqualTo(userId);

        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(this.userRepository.save(captor.capture()));

        assertThat(captor.getValue().getEmail()).isEqualTo(request.getEmail());
    }

    @Test
    void loginThenSuccess(){
        /*
        * login -> 정상적으로 request 입력에 따른 (성공 테스트)
        */
        // given
        LoginRequestDto request = new LoginRequestDto("test@test.com", "test1234");

        UUID userId = UUID.randomUUID();
        UserEntity userEntity = UserEntity.builder().id(userId).email(request.getEmail()).password(request.getPassword()).build();

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(userEntity));
        when(this.passwordEncoder.matches(request.getPassword(), userEntity.getPassword())).thenReturn(true);
        when(jwtTokenProvider.generateAccessToken(userId, request.getEmail())).thenReturn("AC");
        when(jwtTokenProvider.generateRefreshToken(userId, request.getEmail())).thenReturn("RC");
        when(this.refreshTokenRepository.save(any(RefreshTokenEntity.class))).thenReturn(null);

        // when
        LoginResponseDto response =  this.authService.login(request);

        // then
        assertThat(response.getAccessToken()).isEqualTo("AC");
        assertThat(response.getRefreshToken()).isEqualTo("RC");

        ArgumentCaptor<RefreshTokenEntity> captor = ArgumentCaptor.forClass(RefreshTokenEntity.class);
        verify(refreshTokenRepository).save(captor.capture());

        assertThat(captor.getValue().getToken()).isEqualTo("RC");
    }

    @Test
    void loginWhenEmailNotExistThenFail(){
        /*
         * login -> 이메일이 존재하지 않는 에러 메시지 확인 (실패 테스트)
         */
        // given
        LoginRequestDto request = new LoginRequestDto("test@test.com", "test1234");

        UUID userId = UUID.randomUUID();
        UserEntity userEntity = UserEntity.builder().id(userId).email(request.getEmail()).password(request.getPassword()).build();

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        // when
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> this.authService.login(request));

        // then
        assertThat(ex).hasMessageContaining("가입되지 않은 이메일입니다.");
        verify(userRepository, times(1)).findByEmail(request.getEmail());
    }

    @Test
    void loginWhenPasswordNotMatchThenFail(){
        /*
        * login -> 비밀번호 일치하지 않을 경우 에러 메세지 확인 (실패 테스트)
        * */
        // given
        LoginRequestDto request = new LoginRequestDto("test@test.com", "test1234");

        UserEntity userEntity = UserEntity.builder().email("test@test.com").password("test12345").build();

        when(this.userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(userEntity));
        when(passwordEncoder.matches(request.getPassword(), userEntity.getPassword())).thenReturn(false);

        // when
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> this.authService.login(request));

        // then
        assertThat(ex).hasMessageContaining("비밀번호가 일치하지 않습니다.");
    }

    @Test
    void refreshTokenThenSuccess(){
        /*
        * refreshAccessToken -> 정상적으로 토큰 발급 되는지 (성공 테스트)
        * */
        // given
        // given
        String rf = "RF";
        UUID uid = UUID.randomUUID();
        TokenRefreshRequestDto req = new TokenRefreshRequestDto(rf);

        when(jwtTokenProvider.validateToken(rf)).thenReturn(true);
        when(jwtTokenProvider.getUserIdFromToken(rf)).thenReturn(uid);
        when(refreshTokenRepository.findById(uid))
                .thenReturn(Optional.of(RefreshTokenEntity.builder().userId(uid).token(rf).build()));
        when(jwtTokenProvider.getEmailFromToken(rf)).thenReturn("test@test.com");
        when(jwtTokenProvider.generateAccessToken(uid, "test@test.com")).thenReturn("newac");

        // when
        TokenRefreshResponseDto res = authService.refreshAccessToken(req);

        // then
        assertThat(res.getAccessToken()).isEqualTo("newac");
        verify(jwtTokenProvider).validateToken(rf);
        verify(jwtTokenProvider).getUserIdFromToken(rf);
        verify(refreshTokenRepository).findById(uid);
        verify(jwtTokenProvider).getEmailFromToken(rf);
        verify(jwtTokenProvider).generateAccessToken(uid, "test@test.com");
        verifyNoMoreInteractions(jwtTokenProvider, refreshTokenRepository);
    }

}
