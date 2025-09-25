package com.hellofit.hellofit_server.workout.log.dto;

import com.hellofit.hellofit_server.workout.log.WorkoutLogEntity;
import com.hellofit.hellofit_server.global.enums.RecordSource;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

public class WorkoutLogResponseDto {

    @Getter
    @Builder
    @Schema(description = "운동 로그 응답 DTO")
    public static class Summary {
        @Schema(description = "로그 ID", example = "550e8400-e29b-41d4-a716-446655440000")
        private UUID id;

        @Schema(description = "유저 ID", example = "550e8400-e29b-41d4-a716-446655440111")
        private UUID userId;

        @Schema(description = "로그 날짜", example = "2025-09-23")
        private LocalDate logDate;

        @Schema(description = "기록 소스", example = "USER")
        private RecordSource source;

        @Schema(description = "참조한 추천 ID", example = "550e8400-e29b-41d4-a716-446655440111")
        private UUID recommendationId;

        @Schema(description = "총 운동 시간 (분)", example = "60")
        private Integer totalMinutes;

        @Schema(description = "총 칼로리 소모", example = "500")
        private Integer totalCaloriesBurned;

        public static Summary fromEntity(WorkoutLogEntity entity) {
            return Summary.builder()
                .id(entity.getId())
                .userId(entity.getUser()
                    .getId())
                .logDate(entity.getLogDate())
                .source(entity.getSource())
                .recommendationId(entity.getRecommendation() != null ? entity.getRecommendation()
                    .getId() : null)
                .totalMinutes(entity.getTotalMinutes())
                .totalCaloriesBurned(entity.getTotalCaloriesBurned())
                .build();
        }
    }
}
