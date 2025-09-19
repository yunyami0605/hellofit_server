package com.hellofit.hellofit_server.user.profile.dto;

import com.hellofit.hellofit_server.user.profile.UserProfileEntity;
import lombok.*;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;


public class UserProfileResponseDto {
    @Getter
    @Setter
    @Builder
    public static class Detail {
        UUID userId;
        UserProfileEntity.AgeGroup ageGroup;
        UserProfileEntity.Gender gender;
        BigDecimal height;
        BigDecimal weight;
        Integer sleepMinutes;
        Integer exerciseMinutes;
        Set<String> forbiddenFoods;

        public static UserProfileResponseDto.Detail fromEntity(UserProfileEntity userProfile) {
            return Detail.builder()
                .userId(userProfile.getUserId())
                .ageGroup(userProfile.getAgeGroup())
                .gender(userProfile.getGender())
                .height(userProfile.getHeight())
                .weight(userProfile.getWeight())
                .sleepMinutes(userProfile.getSleepMinutes())
                .exerciseMinutes(userProfile.getExerciseMinutes())
                .forbiddenFoods(userProfile.getForbiddenFoods())
                .build();
        }
    }
}
