package com.hellofit.hellofit_server.diet.log.dto;

import com.hellofit.hellofit_server.diet.enums.MealType;
import com.hellofit.hellofit_server.global.enums.RecordSource;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class DietLogRequestDto {

    @Getter
    @NoArgsConstructor
    @Schema(description = "식단 로그 생성 요청 DTO")
    public static class Create {
        @Schema(description = "유저 ID", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
        private UUID userId;

        @Schema(description = "끼니 타입", example = "DINNER", required = true)
        private MealType mealType;

        @Schema(description = "로그 날짜", example = "2025-09-23", required = true)
        private LocalDate logDate;

        @Schema(description = "기록 소스 (AI / USER)", example = "USER", required = true)
        private RecordSource source;

        @Schema(description = "선택한 추천 ID", example = "550e8400-e29b-41d4-a716-446655440111")
        private UUID recommendationId;
    }
}
