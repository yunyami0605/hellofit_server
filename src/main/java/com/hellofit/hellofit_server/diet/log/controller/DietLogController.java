package com.hellofit.hellofit_server.diet.log.controller;

import com.hellofit.hellofit_server.diet.enums.MealType;
import com.hellofit.hellofit_server.diet.log.DietLogEntity;
import com.hellofit.hellofit_server.diet.log.service.DietLogItemService;
import com.hellofit.hellofit_server.diet.log.service.DietLogService;
import com.hellofit.hellofit_server.diet.recommendation.DietRecommendationEntity;
import com.hellofit.hellofit_server.diet.recommendation.service.DietRecommendationService;
import com.hellofit.hellofit_server.global.enums.RecordSource;
import com.hellofit.hellofit_server.user.UserEntity;
import com.hellofit.hellofit_server.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "식단 기록(Log) API", description = "식단 기록 관리 API")
@RestController
@RequestMapping("/api/diets/logs")
@RequiredArgsConstructor
public class DietLogController {

    private final DietLogService logService;
    private final DietLogItemService logItemService;
    private final DietRecommendationService recommendationService;
    private final UserService userService;

    // 특정 날짜 로그 조회
    @Operation(summary = "유저 식단 특정 날짜 로그 조회 API", description = "특정 유저의 특정 날짜에 해당하는 모든 식단 로그를 조회합니다.")
    @GetMapping
    public ResponseEntity<List<DietLogEntity>> getLogs(
        @RequestParam UUID userId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        UserEntity user = userService.getUserById(userId, "DietLogController > getLogs");
        return ResponseEntity.ok(logService.getLogs(user, date));
    }

    // 끼니 단위 로그 조회
    @Operation(summary = "끼니 단위 로그 조회 API", description = "한끼 단위 로그 조회")
    @GetMapping("/{mealType}")
    public ResponseEntity<Optional<DietLogEntity>> getLog(
        @RequestParam UUID userId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
        @PathVariable MealType mealType
    ) {
        UserEntity user = userService.getUserById(userId, "DietLogController > getLog");
        return ResponseEntity.ok(logService.getLog(user, date, mealType));
    }

    // 로그 생성
    @Operation(summary = "식단 로그 생성 API", description = "유저 ID, 끼니, 날짜, 소스를 기반으로 실제 식단 로그를 생성합니다.")
    @PostMapping
    public ResponseEntity<DietLogEntity> createLog(
        @RequestParam UUID userId,
        @RequestParam MealType mealType,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
        @RequestParam RecordSource source,
        @RequestParam(required = false) UUID recommendationId
    ) {
        UserEntity user = userService.getUserById(userId, "DietLogController > createLog");

        DietRecommendationEntity recommendation = recommendationId == null ? null :
            recommendationService.getById(recommendationId);

        DietLogEntity log = logService.create(user, mealType, date, source, recommendation);
        return ResponseEntity.ok(log);
    }

    // 로그 삭제
    @Operation(summary = "식단 로그 삭제 API", description = "로그 ID를 기반으로 식단 로그를 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLog(@PathVariable UUID id) {
        logService.delete(id);
        return ResponseEntity.noContent()
            .build();
    }
}
