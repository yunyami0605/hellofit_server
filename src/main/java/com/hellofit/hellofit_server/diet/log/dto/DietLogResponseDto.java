package com.hellofit.hellofit_server.diet.log.dto;

import com.hellofit.hellofit_server.diet.enums.MealType;
import com.hellofit.hellofit_server.diet.log.DietLogEntity;
import com.hellofit.hellofit_server.diet.log.DietLogItemEntity;
import com.hellofit.hellofit_server.global.enums.RecordSource;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;


public class DietLogResponseDto {

    @Getter
    @Builder
    @Schema(name = "DietLogResponseDtoSummary", description = "식단 로그 응답 DTO")
    public static class Summary {
        @Schema(description = "로그 ID", example = "550e8400-e29b-41d4-a716-446655440000")
        private UUID id;

        @Schema(description = "유저 ID", example = "550e8400-e29b-41d4-a716-446655440000")
        private UUID userId;

        @Schema(description = "끼니 타입", example = "DINNER")
        private MealType mealType;

        @Schema(description = "로그 날짜", example = "2025-09-23")
        private LocalDate logDate;

        @Schema(description = "기록 소스", example = "USER")
        private RecordSource source;

        @Schema(description = "참조한 추천 ID", example = "550e8400-e29b-41d4-a716-446655440111")
        private UUID recommendationId;

        @Schema(description = "음식 리스트")
        private List<FoodSummary> foods;

        public static DietLogResponseDto.Summary fromEntity(DietLogEntity entity) {
            return DietLogResponseDto.Summary.builder()
                .id(entity.getId())
                .userId(entity.getUser()
                    .getId())
                .mealType(entity.getMealType())
                .logDate(entity.getLogDate())
                .source(entity.getSource())
                .foods(entity.getItems()
                    .stream()
                    .map(FoodSummary::fromEntity)
                    .toList())
                .recommendationId(entity.getRecommendation() != null ? entity.getRecommendation()
                    .getId() : null)
                .build();
        }
    }

    @Getter
    @Builder
    @Schema(description = "식단 로그 음식 DTO")
    public static class FoodSummary {
        private String foodName;
        private Integer calories;
        private Double protein;
        private Double fat;
        private Double carbs;

        public static FoodSummary fromEntity(DietLogItemEntity item) {
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
