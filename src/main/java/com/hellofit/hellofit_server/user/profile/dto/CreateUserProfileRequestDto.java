package com.hellofit.hellofit_server.user.profile.dto;

import com.hellofit.hellofit_server.user.profile.UserProfileEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "사용자 프로필 생성 요청 DTO")
public class CreateUserProfileRequestDto {

    @NotNull
    @Schema(description = "연령대", example = "AGE_20S", allowableValues = {"AGE_10S", "AGE_20S", "AGE_30S", "AGE_40S", "AGE_50S"})
    private UserProfileEntity.AgeGroup ageGroup;

    @NotNull
    @Schema(description = "성별", example = "MALE", allowableValues = {"MALE", "FEMALE"})
    private UserProfileEntity.Gender gender;

    @NotNull
    @DecimalMin("50.0")
    @DecimalMax("300.0")
    @Schema(description = "키 (cm)", example = "175.5", minimum = "50.0", maximum = "300.0")
    private Double height;

    @NotNull
    @DecimalMin("20.0")
    @DecimalMax("500.0")
    @Schema(description = "몸무게 (kg)", example = "70.0", minimum = "20.0", maximum = "500.0")
    private Double weight;

    @NotNull
    @Min(0)
    @Max(1440)
    @Schema(description = "수면 시간 (분)", example = "420", minimum = "0", maximum = "1440")
    private Integer sleepMinutes;

    @NotNull
    @Min(0)
    @Max(1440)
    @Schema(description = "운동 시간 (분)", example = "60", minimum = "0", maximum = "1440")
    private Integer exerciseMinutes;

    @Schema(description = "금지 음식 목록", example = "[\"pizza\", \"ramen\"]", maxLength = 100)
    List<@NotBlank @Size(max = 100) String> forbiddenFoods;
}
