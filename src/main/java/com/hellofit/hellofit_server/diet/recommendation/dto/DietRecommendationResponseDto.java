package com.hellofit.hellofit_server.diet.recommendation.dto;

import com.hellofit.hellofit_server.diet.enums.MealType;
import com.hellofit.hellofit_server.diet.recommendation.DietRecommendationEntity;
import com.hellofit.hellofit_server.global.enums.RecordSource;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

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

        public static DietRecommendationResponseDto.Summary fromEntity(DietRecommendationEntity entity) {
            return DietRecommendationResponseDto.Summary.builder()
                .id(entity.getId())
                .userId(entity.getUser()
                    .getId())
                .mealType(entity.getMealType())
                .recommendedDate(entity.getRecommendedDate())
                .source(entity.getSource())
                .build();
        }
    }
}
