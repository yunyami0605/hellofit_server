package com.hellofit.hellofit_server.diet.recommendation.controller;

import com.hellofit.hellofit_server.diet.recommendation.DietRecommendationEntity;
import com.hellofit.hellofit_server.diet.recommendation.dto.DietRecommendationRequestDto;
import com.hellofit.hellofit_server.diet.recommendation.dto.DietRecommendationResponseDto;
import com.hellofit.hellofit_server.diet.recommendation.service.DietRecommendationService;
import com.hellofit.hellofit_server.user.UserEntity;
import com.hellofit.hellofit_server.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Tag(name = "식단 추천 API", description = "식단 추천 관리 API")
@RestController
@RequestMapping("/api/diets/recommendations")
@RequiredArgsConstructor
public class DietRecommendationController {

    private final DietRecommendationService recommendationService;
    private final UserService userService;

    @Operation(summary = "유저 식단 추천 조회 API", description = "특정 유저의 특정 날짜에 해당하는 추천 식단을 조회합니다.")
    @GetMapping()
    public ResponseEntity<List<DietRecommendationResponseDto.Summary>> getRecommendations(
        @AuthenticationPrincipal UUID userId,
        @RequestParam(required = false) String date
    ) {
        UserEntity user = userService.getUserById(userId, "DietRecommendationController > getRecommendations");
        List<DietRecommendationEntity> recommendations = (date == null)
            ? recommendationService.getRecommendations(user, null)
            : recommendationService.getRecommendations(user, java.time.LocalDate.parse(date));

        return ResponseEntity.ok(
            recommendations.stream()
                .map(DietRecommendationResponseDto.Summary::fromEntity)
                .collect(Collectors.toList())
        );
    }

    @Operation(summary = "식단 추천 생성 API", description = "유저 ID, 끼니, 날짜, 소스를 기반으로 추천 식단을 생성합니다.")
    @PostMapping
    public DietRecommendationResponseDto.Summary createRecommendation(
        @RequestBody DietRecommendationRequestDto.Create requestDto
    ) {
        UserEntity user = userService.getUserById(requestDto.getUserId(), "DietRecommendationController > create");
        DietRecommendationEntity recommendation = recommendationService.create(
            user,
            requestDto.getMealType(),
            requestDto.getRecommendedDate(),
            requestDto.getSource()
        );
        return DietRecommendationResponseDto.Summary.fromEntity(recommendation);
    }

    @Operation(summary = "식단 추천 삭제 API", description = "추천 ID를 기반으로 추천 식단을 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecommendation(@PathVariable UUID id) {
        recommendationService.delete(id);
        return ResponseEntity.noContent()
            .build();
    }
}
