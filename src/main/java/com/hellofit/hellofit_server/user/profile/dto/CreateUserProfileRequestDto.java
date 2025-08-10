package com.hellofit.hellofit_server.user.profile.dto;

import com.hellofit.hellofit_server.user.profile.UserProfileEntity;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateUserProfileRequestDto {
    @NotNull
    UserProfileEntity.AgeGroup ageGroup;

    @NotNull UserProfileEntity.Gender gender;

    @NotNull @DecimalMin("50.0") @DecimalMax("300.0") Double height;
    @NotNull @DecimalMin("20.0") @DecimalMax("500.0")  Double weight; // kg
    @NotNull @Min(0) @Max(24 * 60) Integer sleepMinutes;
    @NotNull @Min(0) @Max(24 * 60) Integer exerciseMinutes;
    // Bean Validation 2.0 컨테이너 요소 검증 (각 요소 길이/공백 체크)
    List<@NotBlank @Size(max = 100) String> forbiddenFoods;
}
