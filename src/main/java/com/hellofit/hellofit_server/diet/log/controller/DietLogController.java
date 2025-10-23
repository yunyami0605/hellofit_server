package com.hellofit.hellofit_server.diet.log.controller;

import com.hellofit.hellofit_server.diet.enums.MealType;
import com.hellofit.hellofit_server.diet.log.DietLogEntity;
import com.hellofit.hellofit_server.diet.log.dto.DietLogRequestDto;
import com.hellofit.hellofit_server.diet.log.dto.DietLogResponseDto;
import com.hellofit.hellofit_server.diet.log.service.DietLogItemService;
import com.hellofit.hellofit_server.diet.log.service.DietLogService;
import com.hellofit.hellofit_server.diet.recommendation.DietRecommendationEntity;
import com.hellofit.hellofit_server.diet.recommendation.service.DietRecommendationService;
import com.hellofit.hellofit_server.global.dto.MutationResponse;
import com.hellofit.hellofit_server.user.UserEntity;
import com.hellofit.hellofit_server.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "식단 기록(Log) API", description = "식단 기록 관리 API")
@RestController
@RequestMapping("/diets/logs")
@RequiredArgsConstructor
public class DietLogController {

    private final DietLogService logService;
    private final DietLogItemService logItemService;
    private final DietRecommendationService recommendationService;
    private final UserService userService;

    // 유저 자신 특정 날짜 로그 조회
    @Operation(summary = "유저 식단 특정 날짜 로그 조회 API", description = "특정 유저의 특정 날짜에 해당하는 모든 식단 로그를 조회합니다.")
    @GetMapping("/me")
    public ResponseEntity<List<DietLogEntity>> getLogs(
        @AuthenticationPrincipal UUID userId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        UserEntity user = userService.getUserById(userId, "DietLogController > getLogs");
        return ResponseEntity.ok(logService.getLogs(user, date));
    }

    // 기간별 유저 로그 조회
    @Operation(summary = "유저 식단 기간별 로그 조회 API", description = "특정 유저의 기간 내 모든 식단 로그를 조회합니다.")
    @GetMapping("/me/range")
    public ResponseEntity<List<DietLogResponseDto.Summary>> getLogsInRange(
        @AuthenticationPrincipal UUID userId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        UserEntity user = userService.getUserById(userId, "DietLogController > getLogsInRange");
        List<DietLogEntity> logs = logService.getLogsInRange(user, startDate, endDate);

        List<DietLogResponseDto.Summary> response = logs.stream()
                                                        .map(DietLogResponseDto.Summary::fromEntity)
                                                        .toList()
            ;

        return ResponseEntity.ok(response);
    }

    // 끼니 단위 로그 조회
    @Operation(summary = "유저 자신 식단 끼니 단위 로그 조회 API", description = "한끼 단위 로그 조회")
    @GetMapping("/{mealType}/me")
    public ResponseEntity<Optional<DietLogEntity>> getLog(
        @AuthenticationPrincipal UUID userId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
        @PathVariable MealType mealType
    ) {
        UserEntity user = userService.getUserById(userId, "DietLogController > getLog");
        return ResponseEntity.ok(logService.getLog(user, date, mealType));
    }

    // 로그 생성
    @Operation(summary = "유저 자신 식단 로그 생성 API", description = "추천 기반 또는 유저 직접 입력으로 식단 로그를 생성합니다.")
    @PostMapping("/me")
    @ResponseStatus(HttpStatus.CREATED)
    public MutationResponse createLog(
        @AuthenticationPrincipal UUID userId,
        @RequestBody DietLogRequestDto.Create request
    ) {
        UserEntity user = userService.getUserById(userId, "DietLogController > createLog");

        DietRecommendationEntity recommendation = request.getRecommendationId() == null ? null :
            recommendationService.getById(request.getRecommendationId());

        DietLogEntity log = logService.create(
            user,
            request.getMealType(),
            request.getLogDate(),
            request.getSource(),
            recommendation
        );

        // items 직접 추가 (recommendation 없이 직접 입력 시)
        if (recommendation == null && request.getItems() != null) {
            for (DietLogRequestDto.Create.FoodItemDto itemDto : request.getItems()) {
                logService.addItem(
                    log,
                    itemDto.getId()
                );
            }
        }

        return MutationResponse.builder()
                               .success(true)
                               .build();
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
