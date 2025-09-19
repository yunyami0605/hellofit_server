package com.hellofit.hellofit_server.user;

import com.hellofit.hellofit_server.user.dto.CreateUserRequestDto;
import com.hellofit.hellofit_server.user.dto.UpdateUserRequestDto;
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
public class UserServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserService userService;

    @Test
    void createUserThenSuccess() {
        /*
          createUser 메서드 -> 유저가 정상적으로 생성되는지 테스트
         */
        // given
        var req = new CreateUserRequestDto("a@b.com", "pw", "nick", true);
        when(userRepository.findByEmail("a@b.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("pw")).thenReturn("ENC_PW");

        var savedId = UUID.randomUUID();
//        var savedEntity = UserEntity.builder()
//            .id(savedId)
//            .email("a@b.com")
//            .password("ENC_PW")
//            .nickname("nick")
//            .isPrivacyAgree(true)
//            .build();
//        when(userRepository.save(any(UserEntity.class))).thenReturn(savedEntity);
//
//        // when
//        UUID result = userService.createUser(req);
//
//        // then
//        assertThat(result).isEqualTo(savedId);
        verify(passwordEncoder).encode("pw");

        // 저장 시 비밀번호가 인코딩 값인지 확인
        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue()
            .getPassword()).isEqualTo("ENC_PW");
    }

    @Test
    void createUserWhenEmailExistThenFail() {
        /*
         * createUser -> 이미 존재하는 이메일로 유저를 만들었을 경우, 에러 반환 되는지 여부
         */
        // given
        // 1. 요청 생성
        CreateUserRequestDto request = new CreateUserRequestDto("test@test.com", "pw", "nick", true);

        // 2. 유저 객체 생성
        UUID expectedId = UUID.randomUUID();
//        UserEntity savedUser = UserEntity.builder()
//            .id(expectedId)
//            .email("test@test.com")
//            .password("ENC_PW")
//            .build();
//
//        // 3. 메서드 조건 설정 -> 유저 반환 -> isPresent에서 중복 에러 반환
//        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(savedUser));
//
//        // when & then
//        // assertThatThrownBy(() -> { userService.createUser(request); }).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("이미 존재하는 이메일");
//
//        UserDuplicateEmailException ex =
//            assertThrows(UserDuplicateEmailException.class, () -> userService.createUser(request));
//
//        assertThat(ex).hasMessageContaining("이미 가입된 이메일입니다.");
//
//        verify(userRepository, times(1)).findByEmail("test@test.com");
//        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getUserByIdThenSuccess() {
        /*
         * getUserById -> user id 넣었을 때, 정상적으로 유저 객체가 반환되는지
         */
        // given
        UUID id = UUID.randomUUID();

//        UserEntity userEntity = UserEntity.builder()
//            .id(id)
//            .build();
//
//        when(userRepository.findById(id)).thenReturn(Optional.of(userEntity));
//
//        // when
//        UserEntity result = userService.getUserById(id);
//
//        // then
//        assertThat(result).isSameAs(userEntity);
        verify(userRepository, times(1)).findById(id);
        verifyNoMoreInteractions(userRepository); // 다른 메서드를 호출했는지 체크
    }

    @Test
    void updateUserWhenUserNotFoundThenFail() {
        /*
         * updateUser -> 존재하지않는 id로 접근시 -> Not Found User 에러 반환 확인
         */
        // given
        UUID id = UUID.randomUUID();

        when(userRepository.findById(id)).thenReturn(Optional.empty());
        UpdateUserRequestDto request = new UpdateUserRequestDto("testNick");

        // when
//        UserNotFoundException ex = assertThrows(UserNotFoundException.class, () -> userService.updateUser(id, request));
//
//        // then
//        assertThat(ex).hasMessageContaining("요청한 사용자를 찾을 수 없습니다.");
        verify(userRepository, times(1)).findById(id);
    }

    @Test
    void updateUserWhenUserDuplicateNicknameThenFail() {
        /*
         * updateUser -> 중복 닉네임 수정 요청 -> "이미 사용중인 닉네임입니다." 에러 문구 반환 확인
         */
        // given
        UUID id = UUID.randomUUID();
//        UserEntity savedUser = UserEntity.builder()
//            .id(id)
//            .nickname("testNick")
//            .build();
//
//        when(userRepository.findById(id)).thenReturn(Optional.of(savedUser));
//        UpdateUserRequestDto request = new UpdateUserRequestDto("testNick");
//        when(userRepository.findByNickname(request.getNickname())).thenReturn(Optional.of(savedUser));
//
//
//        // when
//        UserDuplicateNicknameException ex = assertThrows(UserDuplicateNicknameException.class, () -> userService.updateUser(id, request));

        // then
//        assertThat(ex).hasMessageContaining("이미 사용중인 닉네임입니다.");
        verify(userRepository, times(1)).findById(id);
    }

    @Test
    void updateUserThenSuccess() {
        /*
         *  updateUser -> nickname, password 부분에서 유저 정보가 정상적으로 변경되었는지 테스트
         */

        // given
        // 1. updateUser 파라미터 만들기
        UUID id = UUID.randomUUID();
        UpdateUserRequestDto request = new UpdateUserRequestDto("test2");

        // 2. 기존 유저 만들기
//        UserEntity prevUserEntity = UserEntity.builder()
//            .id(id)
//            .nickname("test1")
//            .build();
//
//        // 3. mock findById
//        when(userRepository.findById(id)).thenReturn(Optional.of(prevUserEntity));
//
//        // 4. mock save
//        when(userRepository.save(any(UserEntity.class))).thenAnswer(inv -> inv.getArgument(0));
//
//        // when
//        UUID userId = userService.updateUser(id, request);

//        // then
//        assertThat(userId).isEqualTo(id);
//
//        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
//        verify(userRepository).save(captor.capture());

//        UserEntity savedUser = captor.getValue();
//        assertThat(savedUser.getNickname()).isEqualTo(request.getNickname());
    }
}
