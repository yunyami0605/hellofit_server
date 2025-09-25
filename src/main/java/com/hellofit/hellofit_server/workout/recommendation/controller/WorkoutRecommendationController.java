package com.hellofit.hellofit_server.workout.recommendation.controller;

import com.hellofit.hellofit_server.user.UserEntity;
import com.hellofit.hellofit_server.user.UserService;
import com.hellofit.hellofit_server.workout.recommendation.WorkoutRecommendationEntity;
import com.hellofit.hellofit_server.workout.recommendation.dto.WorkoutRecommendationRequestDto;
import com.hellofit.hellofit_server.workout.recommendation.dto.WorkoutRecommendationResponseDto;
import com.hellofit.hellofit_server.workout.recommendation.service.WorkoutRecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Tag(name = "Workout Recommendation API", description = "운동 추천 관리 API")
@RestController
@RequestMapping("/api/workouts/recommendations")
@RequiredArgsConstructor
public class WorkoutRecommendationController {

    private final WorkoutRecommendationService recommendationService;
    private final UserService userService;

    @Operation(summary = "운동 추천 조회", description = "특정 유저의 특정 날짜 운동 추천을 조회합니다.")
    @GetMapping("/{userId}")
    public ResponseEntity<List<WorkoutRecommendationResponseDto.Summary>> getRecommendations(
        @PathVariable UUID userId,
        @RequestParam String date
    ) {
        UserEntity user = userService.getUserById(userId, "WorkoutRecommendationController > getRecommendations");
        List<WorkoutRecommendationEntity> recommendations = recommendationService.getRecommendations(user, java.time.LocalDate.parse(date));
        return ResponseEntity.ok(
            recommendations.stream()
                .map(WorkoutRecommendationResponseDto.Summary::fromEntity)
                .collect(Collectors.toList())
        );
    }

    @Operation(summary = "운동 추천 생성", description = "유저 ID, 날짜, 소스를 기반으로 운동 추천을 생성합니다.")
    @PostMapping
    public ResponseEntity<WorkoutRecommendationResponseDto.Summary> createRecommendation(
        @RequestBody WorkoutRecommendationRequestDto.Create requestDto
    ) {
        UserEntity user = userService.getUserById(requestDto.getUserId(), "WorkoutRecommendationController > createRecommendation");
        WorkoutRecommendationEntity recommendation = recommendationService.create(user, requestDto.getRecommendedDate(), requestDto.getSource());
        return ResponseEntity.ok(WorkoutRecommendationResponseDto.Summary.fromEntity(recommendation));
    }

    @Operation(summary = "운동 추천 삭제", description = "추천 ID를 기반으로 운동 추천을 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecommendation(@PathVariable UUID id) {
        recommendationService.delete(id);
        return ResponseEntity.noContent()
            .build();
    }
}
