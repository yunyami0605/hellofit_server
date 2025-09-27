package com.hellofit.hellofit_server.diet.recommendation.dto;

import com.hellofit.hellofit_server.user.profile.UserProfileEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class LLMDietRequestDto {
    @AllArgsConstructor
    @Getter
    public static class UserProfileRequestDto {
        private String age_group;
        private String gender;
        private BigDecimal height;
        private BigDecimal weight;
        private List<String> forbidden_foods;

        public static UserProfileRequestDto create(UserProfileEntity profile) {
            return new UserProfileRequestDto(
                mapAgeGroup(profile.getAgeGroup()),
                profile.getGender()
                    .name()
                    .toLowerCase(),
                profile.getHeight(),
                profile.getWeight(),
                profile.getForbiddenFoods()
                    .stream()
                    .toList()
            );
        }

        private static String mapAgeGroup(UserProfileEntity.AgeGroup group) {
            return group.name()
                .replace("AGE_", "")
                .toLowerCase();
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class DietAutoRequest {
        private UserProfileRequestDto user_profile;
        private Map<String, Map<String, MealRecordDto>> history;
    }

    @AllArgsConstructor
    @Getter
    public static class MealRecordDto {
        private List<FoodRecordDto> foods;
        private String explanation;
    }

    @AllArgsConstructor
    @Getter
    public static class FoodRecordDto {
        private String name;
        private Integer calories;
        private Double protein;
        private Double carbs;
        private Double fat;
    }
}
