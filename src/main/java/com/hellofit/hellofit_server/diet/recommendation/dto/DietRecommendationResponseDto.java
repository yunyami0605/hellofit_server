package com.hellofit.hellofit_server.diet.recommendation.dto;

import com.hellofit.hellofit_server.diet.enums.MealType;
import com.hellofit.hellofit_server.diet.recommendation.DietRecommendationEntity;
import com.hellofit.hellofit_server.diet.recommendation.DietRecommendationItemEntity;
import com.hellofit.hellofit_server.global.enums.RecordSource;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class DietRecommendationResponseDto {

    @Getter
    @Builder
    @Schema(description = "식단 추천 응답 DTO")
    public static class Summary {
        @Schema(description = "추천 ID", example = "550e8400-e29b-41d4-a716-446655440000")
        private UUID id;

        @Schema(description = "유저 ID", example = "550e8400-e29b-41d4-a716-446655440000")
        private UUID userId;

        @Schema(description = "끼니 타입", example = "BREAKFAST")
        private MealType mealType;

        @Schema(description = "추천 날짜", example = "2025-09-23")
        private LocalDate recommendedDate;

        @Schema(description = "추천 소스", example = "AI")
        private RecordSource source;

        @Schema(description = "추천 음식 리스트")
        private List<FoodSummary> foods;

        public static DietRecommendationResponseDto.Summary fromEntity(DietRecommendationEntity entity) {
            return DietRecommendationResponseDto.Summary.builder()
                .id(entity.getId())
                .userId(entity.getUser()
                    .getId())
                .mealType(entity.getMealType())
                .recommendedDate(entity.getRecommendedDate())
                .foods(entity.getItems()
                    .stream()
                    .map(FoodSummary::fromEntity)
                    .collect(Collectors.toList()))
                .source(entity.getSource())
                .build();
        }
    }

    @Getter
    @Builder
    @Schema(description = "추천 음식 요약 DTO")
    public static class FoodSummary {
        @Schema(description = "음식 이름", example = "닭가슴살 샐러드")
        private String foodName;

        @Schema(description = "칼로리", example = "350")
        private Integer calories;

        @Schema(description = "단백질(g)", example = "30.5")
        private Double protein;

        @Schema(description = "지방(g)", example = "10.2")
        private Double fat;

        @Schema(description = "탄수화물(g)", example = "25.7")
        private Double carbs;

        public static FoodSummary fromEntity(DietRecommendationItemEntity item) {
            return FoodSummary.builder()
                .foodName(item.getFoodName())
                .calories(item.getCalories())
                .protein(item.getProtein())
                .fat(item.getFat())
                .carbs(item.getCarbs())
                .build();
        }
    }
}
