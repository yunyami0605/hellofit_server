package com.hellofit.hellofit_server.workout.log.controller;

import com.hellofit.hellofit_server.user.UserEntity;
import com.hellofit.hellofit_server.user.UserService;
import com.hellofit.hellofit_server.workout.log.WorkoutLogEntity;
import com.hellofit.hellofit_server.workout.log.dto.WorkoutLogRequestDto;
import com.hellofit.hellofit_server.workout.log.dto.WorkoutLogResponseDto;
import com.hellofit.hellofit_server.workout.log.service.WorkoutLogService;
import com.hellofit.hellofit_server.workout.recommendation.WorkoutRecommendationEntity;
import com.hellofit.hellofit_server.workout.recommendation.service.WorkoutRecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Tag(name = "Workout Log API", description = "운동 로그 관리 API")
@RestController
@RequestMapping("/api/workouts/logs")
@RequiredArgsConstructor
public class WorkoutLogController {

    private final WorkoutLogService logService;
    private final WorkoutRecommendationService recommendationService;
    private final UserService userService;

    @Operation(summary = "운동 로그 조회", description = "특정 유저의 특정 날짜 운동 로그를 조회합니다.")
    @GetMapping("/{userId}")
    public ResponseEntity<List<WorkoutLogResponseDto.Summary>> getLogs(
        @PathVariable UUID userId,
        @RequestParam String date
    ) {
        UserEntity user = userService.getUserById(userId, "WorkoutLogController > getLogs");
        List<WorkoutLogEntity> logs = logService.getLogs(user, java.time.LocalDate.parse(date));
        return ResponseEntity.ok(
            logs.stream()
                .map(WorkoutLogResponseDto.Summary::fromEntity)
                .collect(Collectors.toList())
        );
    }

    @Operation(summary = "운동 로그 생성", description = "유저 ID, 날짜, 소스를 기반으로 운동 로그를 생성합니다.")
    @PostMapping
    public ResponseEntity<WorkoutLogResponseDto.Summary> createLog(@RequestBody WorkoutLogRequestDto.Create requestDto) {
        UserEntity user = userService.getUserById(requestDto.getUserId(), "WorkoutLogController > createLog");
        WorkoutRecommendationEntity recommendation = requestDto.getRecommendationId() == null ? null :
            recommendationService.getById(requestDto.getRecommendationId());

        WorkoutLogEntity log = logService.create(
            user,
            requestDto.getLogDate(),
            requestDto.getSource(),
            recommendation,
            requestDto.getTotalMinutes(),
            requestDto.getTotalCaloriesBurned()
        );

        return ResponseEntity.ok(WorkoutLogResponseDto.Summary.fromEntity(log));
    }

    @Operation(summary = "운동 로그 삭제", description = "운동 로그 ID를 기반으로 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLog(@PathVariable UUID id) {
        logService.delete(id);
        return ResponseEntity.noContent()
            .build();
    }
}
