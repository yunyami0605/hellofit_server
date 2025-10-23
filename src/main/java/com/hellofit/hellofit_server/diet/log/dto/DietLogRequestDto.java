package com.hellofit.hellofit_server.diet.log.dto;

import com.hellofit.hellofit_server.diet.enums.MealType;
import com.hellofit.hellofit_server.global.enums.RecordSource;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class DietLogRequestDto {

    @Getter
    @NoArgsConstructor
    @Schema(name = "DietLogRequestDto Create", description = "식단 로그 생성 요청 DTO")
    public static class Create {
        @Schema(description = "끼니 타입", example = "DINNER", required = true)
        private MealType mealType;

        @Schema(description = "로그 날짜", example = "2025-09-23", required = true)
        private LocalDate logDate;

        @Schema(description = "기록 소스 (AI / USER)", example = "USER", required = true)
        private RecordSource source;

        @Schema(description = "기록 음식 항목들 (직접 입력 시 필요)")
        private List<FoodItemDto> items;

        @Schema(description = "선택한 추천 ID", example = "550e8400-e29b-41d4-a716-446655440111")
        private UUID recommendationId;

        @Getter
        @Setter
        public static class FoodItemDto {
            private UUID id;
            private String foodName;
        }
    }
}
