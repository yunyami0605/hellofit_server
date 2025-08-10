package com.hellofit.hellofit_server.user.profile;

import com.hellofit.hellofit_server.user.UserEntity;
import com.hellofit.hellofit_server.user.UserRepository;
import com.hellofit.hellofit_server.user.profile.dto.CreateUserProfileRequestDto;
import com.hellofit.hellofit_server.user.profile.dto.UpdateUserProfileRequestDto;
import com.hellofit.hellofit_server.user.profile.dto.UserProfileResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.LinkedHashSet;

@Service
@Transactional
@RequiredArgsConstructor
public class UserProfileService {
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;

    public UUID createProfile(UUID userId, CreateUserProfileRequestDto request){
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "User not found"));

        if(userProfileRepository.existsByUserId(userId)){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists");
        }

        UserProfileEntity profile = UserProfileEntity.builder()
                .user(user)
                .ageGroup(request.getAgeGroup())
                .gender(request.getGender())
                .height(request.getHeight())
                .weight(request.getWeight())
                .sleepMinutes(request.getSleepMinutes())
                .exerciseMinutes(request.getExerciseMinutes())
                .forbiddenFoods(normalizeFoods(request.getForbiddenFoods()))
                .build();

        userProfileRepository.save(profile);

        return profile.getUserId();
    }

    private Set<String> normalizeFoods(List<String> input) {
        if (input == null) return new HashSet<>();
        // 공백 트림, 빈 값 제거, 길이 제한, 중복 제거(Set)
        return input.stream()
                .map(s -> s == null ? null : s.trim())
                .filter(s -> s != null && !s.isEmpty())
                .map(s -> s.length() > 100 ? s.substring(0, 100) : s) // DB length=100 보호
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public UserProfileResponse getProfileById(UUID userId){
        UserProfileEntity userProfile = userProfileRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found"));

        return UserProfileResponse.builder()
                .userId(userId)
                .ageGroup(userProfile.getAgeGroup())
                .gender(userProfile.getGender())
                .height(userProfile.getHeight())
                .weight(userProfile.getWeight())
                .sleepMinutes(userProfile.getSleepMinutes())
                .exerciseMinutes(userProfile.getExerciseMinutes())
                .forbiddenFoods(userProfile.getForbiddenFoods())
                .build();
    }

    public UUID patchProfile(UUID userId, UpdateUserProfileRequestDto request) {
        UserProfileEntity profile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found"));

        if (request.getAgeGroup() != null) profile.setAgeGroup(request.getAgeGroup());
        if (request.getGender() != null)   profile.setGender(request.getGender());
        if (request.getHeight() != null)   profile.setHeight(request.getHeight());
        if (request.getWeight() != null)   profile.setWeight(request.getWeight());
        if (request.getSleepMinutes() != null) profile.setSleepMinutes(request.getSleepMinutes());
        if (request.getExerciseMinutes() != null) profile.setExerciseMinutes(request.getExerciseMinutes());

        if (request.getForbiddenFoods() != null) {
            profile.setForbiddenFoods(normalizeFoods(request.getForbiddenFoods()));
        }

        return profile.getUserId();
    }


}
