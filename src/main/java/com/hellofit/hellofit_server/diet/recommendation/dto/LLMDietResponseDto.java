package com.hellofit.hellofit_server.diet.recommendation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

public class LLMDietResponseDto {
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class FoodRecordDto {
        private String name;
        private Integer calories;
        private Double protein;
        private Double carbs;
        private Double fat;
    }

    @Getter
    @Setter
    public static class DietRecommendationDto {
        @JsonProperty("recommendation_id")
        private String recommendationId;

        @JsonProperty("meal_type")
        private String mealType;  // BREAKFAST / LUNCH / DINNER
        private LocalDate date;
        private List<FoodRecordDto> foods;
        private String explanation;
    }
}
