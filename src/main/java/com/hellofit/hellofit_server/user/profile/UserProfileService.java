package com.hellofit.hellofit_server.user.profile;

import com.hellofit.hellofit_server.global.dto.MutationResponse;
import com.hellofit.hellofit_server.user.UserEntity;
import com.hellofit.hellofit_server.user.UserRepository;
import com.hellofit.hellofit_server.user.UserService;
import com.hellofit.hellofit_server.user.profile.dto.CreateUserProfileRequestDto;
import com.hellofit.hellofit_server.user.profile.dto.UpdateUserProfileRequestDto;
import com.hellofit.hellofit_server.user.profile.dto.UserProfileResponseDto;
import com.hellofit.hellofit_server.user.profile.exception.UserProfileException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserProfileService {
    private final UserService userService;
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;

    /*
     * 사용자 프로필 생성 서비스 로직
     * */
    @Transactional
    public MutationResponse createProfile(UUID userID, CreateUserProfileRequestDto request) {
        UserEntity userEntity = userService.getUserById(userID, "createProfile");

        if (userProfileRepository.existsById(userID)) {
            throw new UserProfileException.UserProfileDuplicate("createProfile", userID);
        }

        UserProfileEntity profile = UserProfileEntity.create(userEntity,
            request.getAgeGroup(),
            request.getGender(),
            request.getHeight(),
            request.getWeight(),
            request.getSleepMinutes(),
            request.getExerciseMinutes(),
            request.getForbiddenFoods());

        userProfileRepository.save(profile);

        return MutationResponse.of(true);
    }

    /**
     * 특정 userId로 UserProfileEntity를 조회
     *
     * @param userId 조회할 유저의 ID
     * @param action 호출 맥락(예: "getProfileById", "patchProfile")
     * @return 조회된 UserProfileEntity
     * @throws UserProfileException.UserProfileNotFound 프로필이 존재하지 않을 경우
     */
    public UserProfileEntity getProfileEntityById(UUID userId, String action) {
        return userProfileRepository.findById(userId)
            .orElseThrow(() -> new UserProfileException.UserProfileNotFound(action, userId));
    }

    /*
     * 유저 프로필 조회 서비스 로직
     * */
    public UserProfileResponseDto.Detail getProfileById(UUID userId) {
        UserProfileEntity profile = getProfileEntityById(userId, "getProfileById");

        return UserProfileResponseDto.Detail.fromEntity(profile);
    }

    /*
     * 유저 정보 수정 서비스 로직
     * */
    @Transactional
    public UserProfileResponseDto.Detail patchProfile(UUID userId, UpdateUserProfileRequestDto request) {
        UserProfileEntity profile = getProfileEntityById(userId, "patchProfile");

        if (request.getAgeGroup() != null) profile.changeAgeGroup(request.getAgeGroup());
        if (request.getGender() != null) profile.changeGender(request.getGender());
        if (request.getHeight() != null) profile.changeHeight(request.getHeight());
        if (request.getWeight() != null) profile.changeWeight(request.getWeight());
        if (request.getSleepMinutes() != null) profile.changeSleepMinutes(request.getSleepMinutes());
        if (request.getExerciseMinutes() != null) profile.changeExerciseMinutes(request.getExerciseMinutes());

        if (request.getForbiddenFoods() != null) {
            profile.replaceForbiddenFoods(request.getForbiddenFoods());
        }

        return UserProfileResponseDto.Detail.fromEntity(profile);
    }
}
