package com.hellofit.hellofit_server.user.profile.dto;

import com.hellofit.hellofit_server.user.profile.UserProfileEntity;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserProfileRequestDto {
    UserProfileEntity.AgeGroup ageGroup;
    UserProfileEntity.Gender gender;

    @DecimalMin("50.0")
    @DecimalMax("300.0")
    BigDecimal height;

    @DecimalMin("20.0")
    @DecimalMax("500.0")
    BigDecimal weight; // kg

    @Min(0)
    @Max(24 * 60)
    Integer sleepMinutes;

    @Min(0)
    @Max(24 * 60)
    Integer exerciseMinutes;
    
    List<@NotBlank @Size(max = 100) String> forbiddenFoods;
}
