package com.hellofit.hellofit_server.user.profile.dto;

import com.hellofit.hellofit_server.user.profile.UserProfileEntity;
import lombok.*;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileResponse {
    UUID userId;
    UserProfileEntity.AgeGroup ageGroup;
    UserProfileEntity.Gender gender;
    Double height;
    Double weight;
    Integer sleepMinutes;
    Integer exerciseMinutes;
    Set<String> forbiddenFoods;
}
