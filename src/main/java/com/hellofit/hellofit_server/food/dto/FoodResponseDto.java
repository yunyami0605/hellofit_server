package com.hellofit.hellofit_server.food.dto;

import com.hellofit.hellofit_server.food.FoodEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

public class FoodResponseDto {
    @Getter
    @Builder
    public static class Summary {
        @Schema(description = "음식 ID")
        private UUID id;

        @Schema(description = "대표 식품명")
        private String repFoodName;

        @Schema(description = "카테고리")
        private String category;

        @Schema(description = "칼로리(kcal)")
        private Float kcal;

        @Schema(description = "단백질(g)")
        private Float protein;

        @Schema(description = "탄수화물(g)")
        private Float carbs;

        @Schema(description = "지방(g)")
        private Float fat;

        @Schema(description = "데이터 기준일자")
        private LocalDate dataDate;

        public static Summary fromEntity(FoodEntity entity) {
            return Summary.builder()
                .id(entity.getId())
                .repFoodName(entity.getRepFoodName())
                .category(entity.getCategory())
                .kcal(entity.getKcal())
                .protein(entity.getProtein())
                .carbs(entity.getCarbs())
                .fat(entity.getFat())
                .dataDate(entity.getDataDate())
                .build();
        }
    }
}
