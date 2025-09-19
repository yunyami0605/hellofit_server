package com.hellofit.hellofit_server.auth;

import com.hellofit.hellofit_server.auth.dto.*;
import com.hellofit.hellofit_server.auth.exception.NotMatchPasswordException;
import com.hellofit.hellofit_server.auth.exception.UnAuthorizedEmailException;
import com.hellofit.hellofit_server.auth.token.RefreshTokenEntity;
import com.hellofit.hellofit_server.auth.token.RefreshTokenRepository;
import com.hellofit.hellofit_server.global.constants.AuthConstant;
import com.hellofit.hellofit_server.global.jwt.JwtTokenProvider;
import com.hellofit.hellofit_server.user.UserEntity;
import com.hellofit.hellofit_server.user.UserRepository;
import com.hellofit.hellofit_server.user.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    UserService userService;
    @Mock
    UserRepository userRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    JwtTokenProvider jwtTokenProvider;
    @Mock
    RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    AuthService authService;

    @Test
    void signupWhenEmailExistThenFail() {
        /*
         * signup -> 이미 존재하는 이메일로 테스트할 경우 중복 fail이 발생하는지 테스트
         */
        // given
        var request = new SignupRequestDto("test@test.com", "test1234", "test", true);

        // userService 중복 체크 시 바로 예외 던지도록 설정
        doThrow(new UserDuplicateEmailException(UUID.randomUUID()))
            .when(userService)
            .checkDuplicateEmail(request.getEmail());

        HttpServletResponse response = mock(HttpServletResponse.class);

        // when
        UserDuplicateEmailException ex = assertThrows(UserDuplicateEmailException.class,
            () -> authService.signup(request, response));

        // then
        assertThat(ex).hasMessageContaining("이미 가입된 이메일입니다.");
        verify(userService, times(1)).checkDuplicateEmail(request.getEmail());
        verify(userRepository, never()).save(any()); // 저장은 호출되지 않아야 함
    }

    @Test
    void signupThenSuccess() {
        /*
         * signup -> 정상 입력 시 유저 회원가입 성공 및 토큰 발급 여부
         */
        // given
        UUID userId = UUID.randomUUID();
        var request = new SignupRequestDto("test@test.com", "test1234", "test", true);

        var savedUserEntity = UserEntity.builder()
            .id(userId)
            .email(request.getEmail())
            .nickname(request.getNickname())
            .password("en_test1234")
            .build();

        // repository, encoder mocking
        doNothing().when(userService)
            .checkDuplicateEmail(request.getEmail());
        doNothing().when(userService)
            .checkDuplicateNickname(request.getNickname());
        when(passwordEncoder.encode(request.getPassword())).thenReturn("en_test1234");
        when(userRepository.save(any(UserEntity.class))).thenReturn(savedUserEntity);

        // HttpServletResponse mock
        HttpServletResponse response = mock(HttpServletResponse.class);

        // when
        LoginResponseDto loginResponse = authService.signup(request, response);

        // then
        assertThat(loginResponse).isNotNull();
//        assertThat(loginResponse.getId()).isEqualTo(userId);

        // 저장된 UserEntity 확인
        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(captor.capture());
        UserEntity captured = captor.getValue();

        assertThat(captured.getEmail()).isEqualTo(request.getEmail());
        assertThat(captured.getNickname()).isEqualTo(request.getNickname());
        assertThat(captured.getPassword()).isEqualTo("en_test1234");
    }

    @Test
    void loginThenSuccess() {
        /*
         * login -> 정상적으로 request 입력에 따른 (성공 테스트)
         */
        // given
        LoginRequestDto request = new LoginRequestDto("test@test.com", "test1234");

        UUID userId = UUID.randomUUID();
        UserEntity userEntity = UserEntity.builder()
            .id(userId)
            .email(request.getEmail())
            .password("encoded_pw")
            .build();

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(userEntity));
        when(passwordEncoder.matches(request.getPassword(), userEntity.getPassword())).thenReturn(true);
        when(jwtTokenProvider.generateAccessToken(userId, null)).thenReturn("AC");
        when(jwtTokenProvider.generateRefreshToken(userId, null)).thenReturn("RC");
        when(refreshTokenRepository.save(any(RefreshTokenEntity.class))).thenReturn(null);

        HttpServletResponse response = mock(HttpServletResponse.class);

        // when
        LoginResponseDto loginResponse = authService.login(request, response);

        // then
        assertThat(loginResponse).isNotNull();
        assertThat(loginResponse.getAccess()).isEqualTo("AC");
//        assertThat(loginResponse.getId()).isEqualTo(userId);

        // rf 저장 검증
        ArgumentCaptor<RefreshTokenEntity> captor = ArgumentCaptor.forClass(RefreshTokenEntity.class);
        verify(refreshTokenRepository).save(captor.capture());
        assertThat(captor.getValue()
            .getToken()).isEqualTo("RC");

        // 쿠키 헤더 세팅 확인
        verify(response, atLeastOnce()).addHeader(eq(HttpHeaders.SET_COOKIE), contains("RC"));
        verify(response, atLeastOnce()).addHeader(eq(HttpHeaders.SET_COOKIE), contains(AuthConstant.XSRF_TOKEN_COOKIE));
    }


    @Test
    void loginWhenEmailNotExistThenFail() {
        /*
         * login -> 이메일이 존재하지 않는 에러 메시지 확인 (실패 테스트)
         */
        // given
        LoginRequestDto request = new LoginRequestDto("test@test.com", "test1234");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        HttpServletResponse response = mock(HttpServletResponse.class);

        // when
        UnAuthorizedEmailException ex = assertThrows(UnAuthorizedEmailException.class,
            () -> authService.login(request, response));

        // then
        assertThat(ex).hasMessageContaining("가입되지 않은 이메일입니다.");
        verify(userRepository, times(1)).findByEmail(request.getEmail());
    }


    @Test
    void loginWhenPasswordNotMatchThenFail() {
        /*
         * login -> 비밀번호 일치하지 않을 경우 에러 메세지 확인 (실패 테스트)
         */
        // given
        LoginRequestDto request = new LoginRequestDto("test@test.com", "test1234");

        UserEntity userEntity = UserEntity.builder()
            .email("test@test.com")
            .password("encoded_pw")
            .build();

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(userEntity));
        when(passwordEncoder.matches(request.getPassword(), userEntity.getPassword())).thenReturn(false);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // when
        NotMatchPasswordException ex = assertThrows(NotMatchPasswordException.class,
            () -> authService.login(request, response));

        // then
        assertThat(ex).hasMessageContaining("비밀번호가 일치하지 않습니다.");
    }
}
