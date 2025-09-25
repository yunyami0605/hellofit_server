package com.hellofit.hellofit_server.workout.recommendation.dto;

import com.hellofit.hellofit_server.global.enums.RecordSource;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

public class WorkoutRecommendationRequestDto {

    @Getter
    @NoArgsConstructor
    @Schema(description = "운동 추천 생성 요청 DTO")
    public static class Create {
        @Schema(description = "유저 ID", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
        private UUID userId;

        @Schema(description = "추천 날짜", example = "2025-09-23", required = true)
        private LocalDate recommendedDate;

        @Schema(description = "추천 소스 (AI / USER)", example = "AI", required = true)
        private RecordSource source;
    }
}
