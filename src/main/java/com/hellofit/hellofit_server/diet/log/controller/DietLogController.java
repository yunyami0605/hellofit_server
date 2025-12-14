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
import java.time.YearMonth;
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

    // 기간 내 영양소 합계 조회
    @Operation(summary = "기간 내 영양소 합계 조회", description = "유저가 기록한 식단 로그 기반으로 칼로리/단백질/지방/탄수화물 합계를 반환합니다. date(YYYY-MM-DD), month(YYYY-MM) 또는 startDate/endDate(YYYY-MM-DD)를 사용하세요.")
    @GetMapping("/me/macros")
    public ResponseEntity<DietLogResponseDto.MacrosSummary> getMacrosSummary(
        @AuthenticationPrincipal UUID userId,
        @RequestParam(required = false) String date,
        @RequestParam(required = false) String month,
        @RequestParam(required = false) String startDate,
        @RequestParam(required = false) String endDate
    ) {
        UserEntity user = userService.getUserById(userId, "DietLogController > getMacrosSummary");

        LocalDate start;
        LocalDate end;
        if (date != null) {
            start = LocalDate.parse(date);
            end = start;
        } else if (month != null) {
            YearMonth ym = YearMonth.parse(month); // expect YYYY-MM
            start = ym.atDay(1);
            end = ym.atEndOfMonth();
        } else {
            // 기본값: 오늘 하루
            start = startDate != null ? LocalDate.parse(startDate) : LocalDate.now();
            end = endDate != null ? LocalDate.parse(endDate) : start;
        }

        List<DietLogEntity> logs = logService.getLogsInRange(user, start, end);

        int calories = 0;
        double protein = 0d;
        double fat = 0d;
        double carbs = 0d;

        for (DietLogEntity log : logs) {
            if (log.getItems() == null) continue;
            for (var item : log.getItems()) {
                calories += item.getCalories() != null ? item.getCalories() : 0;
                protein += item.getProtein() != null ? item.getProtein() : 0d;
                fat += item.getFat() != null ? item.getFat() : 0d;
                carbs += item.getCarbs() != null ? item.getCarbs() : 0d;
            }
        }

        DietLogResponseDto.MacrosSummary summary = DietLogResponseDto.MacrosSummary.builder()
            .date(start.equals(end) ? start : null)
            .calories(calories)
            .protein(protein)
            .fat(fat)
            .carbs(carbs)
            .build();

        return ResponseEntity.ok(summary);
    }

    // 기간 내 일자별 영양소 합계
    @Operation(summary = "기간 내 일자별 영양소 합계", description = "month(YYYY-MM) 또는 startDate/endDate(YYYY-MM-DD)로 각 날짜별 칼로리/단백질/지방/탄수화물 합계를 반환합니다. month가 우선입니다.")
    @GetMapping("/me/macros/daily")
    public ResponseEntity<List<DietLogResponseDto.MacrosDaily>> getDailyMacros(
        @AuthenticationPrincipal UUID userId,
        @RequestParam(required = false) String month,
        @RequestParam(required = false) String startDate,
        @RequestParam(required = false) String endDate
    ) {
        UserEntity user = userService.getUserById(userId, "DietLogController > getDailyMacros");

        LocalDate start;
        LocalDate end;
        if (month != null) {
            YearMonth ym = YearMonth.parse(month);
            start = ym.atDay(1);
            end = ym.atEndOfMonth();
        } else {
            LocalDate now = LocalDate.now();
            start = startDate != null ? LocalDate.parse(startDate) : now.withDayOfMonth(1);
            end = endDate != null ? LocalDate.parse(endDate) : now.withDayOfMonth(now.lengthOfMonth());
        }

        List<DietLogEntity> logs = logService.getLogsInRange(user, start, end);

        // 날짜별 초기 합계 맵 (모든 날짜를 0으로 포함)
        var days = new java.util.LinkedHashMap<LocalDate, int[]>();
        for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
            // [calories, protein*10, fat*10, carbs*10] (double 누적을 위해 10배 스케일)
            days.put(d, new int[] {0, 0, 0, 0});
        }

        for (DietLogEntity log : logs) {
            if (log.getItems() == null) continue;
            int[] acc = days.get(log.getLogDate());
            if (acc == null) continue;
            for (var item : log.getItems()) {
                acc[0] += item.getCalories() != null ? item.getCalories() : 0;
                acc[1] += (int) Math.round((item.getProtein() != null ? item.getProtein() : 0d) * 10);
                acc[2] += (int) Math.round((item.getFat() != null ? item.getFat() : 0d) * 10);
                acc[3] += (int) Math.round((item.getCarbs() != null ? item.getCarbs() : 0d) * 10);
            }
        }

        List<DietLogResponseDto.MacrosDaily> result = new java.util.ArrayList<>();
        for (var entry : days.entrySet()) {
            int[] a = entry.getValue();
            result.add(DietLogResponseDto.MacrosDaily.builder()
                .date(entry.getKey())
                .calories(a[0])
                .protein(a[1] / 10.0)
                .fat(a[2] / 10.0)
                .carbs(a[3] / 10.0)
                .build());
        }
        return ResponseEntity.ok(result);
    }
}
