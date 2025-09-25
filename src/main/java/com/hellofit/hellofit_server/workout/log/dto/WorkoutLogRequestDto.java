package com.hellofit.hellofit_server.workout.log.dto;

import com.hellofit.hellofit_server.global.enums.RecordSource;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

public class WorkoutLogRequestDto {

    @Getter
    @NoArgsConstructor
    @Schema(description = "운동 로그 생성 요청 DTO")
    public static class Create {
        @Schema(description = "유저 ID", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
        private UUID userId;

        @Schema(description = "로그 날짜", example = "2025-09-23", required = true)
        private LocalDate logDate;

        @Schema(description = "기록 소스 (AI / USER)", example = "USER", required = true)
        private RecordSource source;

        @Schema(description = "참조 추천 ID", example = "550e8400-e29b-41d4-a716-446655440111")
        private UUID recommendationId;

        @Schema(description = "총 운동 시간 (분)", example = "60")
        private Integer totalMinutes;

        @Schema(description = "총 칼로리 소모", example = "500")
        private Integer totalCaloriesBurned;
    }
}
