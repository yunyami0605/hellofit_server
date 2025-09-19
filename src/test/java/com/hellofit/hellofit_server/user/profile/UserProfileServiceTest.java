package com.hellofit.hellofit_server.user.profile;

import com.hellofit.hellofit_server.user.UserEntity;
import com.hellofit.hellofit_server.user.UserRepository;
import com.hellofit.hellofit_server.user.profile.dto.CreateUserProfileRequestDto;
import com.hellofit.hellofit_server.user.profile.dto.UpdateUserProfileRequestDto;
import com.hellofit.hellofit_server.user.profile.dto.UserProfileResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.*;

class UserProfileServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserProfileRepository userProfileRepository;

    @InjectMocks
    private UserProfileService userProfileService;

    private UUID userId;
    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userId = UUID.randomUUID();
//        userEntity = UserEntity.builder()
//            .id(userId)
//            .email("test1@test.com")
//            .password("test1234")
//            .nickname("test1")
//            .isPrivacyAgree(true)
//            .build();
    }

    @Test
    void createProfileThenSucess() {
        // given
//        CreateUserProfileRequestDto request = CreateUserProfileRequestDto.builder()
//            .ageGroup(UserProfileEntity.AgeGroup.AGE_20S)
//            .gender(UserProfileEntity.Gender.MALE)
//            .height(175.5)
//            .weight(70.0)
//            .sleepMinutes(420)
//            .exerciseMinutes(60)
//            .forbiddenFoods(List.of("pizza", "ramen"))
//            .build();
//
//        given(userRepository.findById(userId)).willReturn(Optional.of(userEntity));
//
//        given(userProfileRepository.existsByUserId(userId)).willReturn(false);
//
//        // ArgumentCaptor로 save()된 객체 캡처
//        ArgumentCaptor<UserProfileEntity> captor = ArgumentCaptor.forClass(UserProfileEntity.class);
//
//        // when
//        userProfileService.createProfile(userEntity, request);
//
//        // then
//        verify(userProfileRepository).save(captor.capture());
//
//        UserProfileEntity savedProfile = captor.getValue();
//        assertThat(savedProfile.getUser()).isEqualTo(userEntity);
//        assertThat(savedProfile.getAgeGroup()).isEqualTo(UserProfileEntity.AgeGroup.AGE_20S);
//        assertThat(savedProfile.getForbiddenFoods()).containsExactly("pizza", "ramen");

    }

    @Test
    void createProfileWhenProfileExistThenFail() {
        // given
        given(userRepository.findById(userId)).willReturn(Optional.of(userEntity));
        given(userProfileRepository.existsByUserId(userId)).willReturn(true);

        CreateUserProfileRequestDto request = CreateUserProfileRequestDto.builder()
            .build();

        // when
//        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> userProfileService.createProfile(userEntity, request));

        // then
//        assertThat(ex).hasMessageContaining("User already exists");
    }

    @Test
    void getProfileByIdThenSuccess() {
        // given
//        UserProfileEntity profile = UserProfileEntity.builder()
//            .userId(userId)
//            .user(userEntity)
//            .ageGroup(UserProfileEntity.AgeGroup.AGE_20S)
//            .gender(UserProfileEntity.Gender.MALE)
//            .height(170.0)
//            .weight(70.0)
//            .sleepMinutes(480)
//            .exerciseMinutes(30)
//            .forbiddenFoods(Set.of("milk"))
//            .build();
//
//        given(userProfileRepository.findById(userId)).willReturn(Optional.of(profile));
//
//        // when
//        UserProfileResponseDto response = userProfileService.getProfileById(userId);
//
//        // then
//        assertThat(response.getUserId()).isEqualTo(userId);
//        assertThat(response.getForbiddenFoods()).containsExactly("milk");
    }

    @Test
    void patchProfileThenSuccess() {

        // given
//        UserProfileEntity profile = UserProfileEntity.builder()
//            .userId(userId)
//            .user(userEntity)
//            .ageGroup(UserProfileEntity.AgeGroup.AGE_20S)
//            .gender(UserProfileEntity.Gender.MALE)
//            .height(175.0)
//            .weight(70.0)
//            .sleepMinutes(420)
//            .exerciseMinutes(60)
//            .forbiddenFoods(Set.of("pizza"))
//            .build();
//
//        given(userProfileRepository.findById(userId)).willReturn(Optional.of(profile));
//
//        UpdateUserProfileRequestDto request = UpdateUserProfileRequestDto.builder()
//            .height(180.0)
//            .forbiddenFoods(List.of("burger", "  ramen  "))
//            .build();

        // when
//        UUID result = userProfileService.patchProfile(userEntity, request);

        // then
//        assertThat(result).isEqualTo(userId);
//        assertThat(profile.getHeight()).isEqualTo(180.0);
//        assertThat(profile.getForbiddenFoods()).containsExactly("burger", "ramen");
    }
}
