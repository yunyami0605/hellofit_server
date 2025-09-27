package com.hellofit.hellofit_server.diet.recommendation.service;

import com.hellofit.hellofit_server.diet.enums.MealType;
import com.hellofit.hellofit_server.diet.log.DietLogEntity;
import com.hellofit.hellofit_server.diet.log.repository.DietLogRepository;
import com.hellofit.hellofit_server.diet.recommendation.DietRecommendationEntity;
import com.hellofit.hellofit_server.diet.recommendation.DietRecommendationItemEntity;
import com.hellofit.hellofit_server.diet.recommendation.client.LlmDietClient;
import com.hellofit.hellofit_server.diet.recommendation.dto.LLMDietRequestDto;
import com.hellofit.hellofit_server.diet.recommendation.dto.LLMDietResponseDto;
import com.hellofit.hellofit_server.diet.recommendation.repository.DietRecommendationItemRepository;
import com.hellofit.hellofit_server.diet.recommendation.repository.DietRecommendationRepository;
import com.hellofit.hellofit_server.global.enums.RecordSource;
import com.hellofit.hellofit_server.user.UserEntity;
import com.hellofit.hellofit_server.user.UserRepository;
import com.hellofit.hellofit_server.user.exception.UserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DietRecommendationGenerator {
    private final DietRecommendationRepository recommendationRepository;
    private final DietLogRepository dietLogRepository;
    private final DietRecommendationItemRepository itemRepository;
    private final UserRepository userRepository;
    private final LlmDietClient llmDietClient;

    @Transactional
    public void generateForUser(UserEntity user, LocalDate date, LLMDietRequestDto.DietAutoRequest req) {
        // 이미 DB에 3끼(아침/점심/저녁)가 모두 있으면 아예 LLM 호출 안 함
        List<DietRecommendationEntity> existing = recommendationRepository.findByUserAndRecommendedDate(user, date);
        if (existing.size() == 3) {
            log.info("이미 3끼 식단 존재 userId={} date={}", user.getId(), date);
            return;
        }

        // FastAPI 호출 (없는 경우만)
        LLMDietResponseDto.DietRecommendationDto[] recs = llmDietClient.regenerateDay(date, req);


        for (LLMDietResponseDto.DietRecommendationDto rec : recs) {
            log.info("@@@@@@@@@@@");
            log.info("mealType={}", rec.getMealType());
            log.info("date={}", rec.getDate());
            log.info("foods={}", rec.getFoods());

            MealType mealType = MealType.valueOf(rec.getMealType()
                .toUpperCase());

            // 끼니 단위로 중복 체크
            boolean exists = existing.stream()
                .anyMatch(r -> r.getMealType()
                    .equals(mealType));

            if (exists) {
                log.info("해당 끼니 이미 존재 userId={} date={} meal={}", user.getId(), date, mealType);
                continue;
            }

            // Recommendation 저장
            DietRecommendationEntity recommendation =
                DietRecommendationEntity.create(user, mealType, rec.getDate(), RecordSource.AI);
            recommendationRepository.save(recommendation);

            // Items 저장
            for (LLMDietResponseDto.FoodRecordDto food : rec.getFoods()) {
                DietRecommendationItemEntity item = DietRecommendationItemEntity.create(
                    recommendation,
                    food.getName(),
                    food.getCalories(),
                    food.getProtein(),
                    food.getFat(),
                    food.getCarbs()
                );
                itemRepository.save(item);
            }
        }
    }

    @Transactional
    public void generateDailyDietsForOneUser(UUID userId) {
        LocalDate today = LocalDate.now();
        UserEntity userEntity = userRepository.findById(userId)
            .orElseThrow(() -> new UserException.UserNotFoundException("DietRecommendationGenerator -> generateDailyDietsByPerson", userId));

        // A, B, C 순차 처리
        for (int offset = 0; offset <= 2; offset++) {
            log.info(String.valueOf(offset));
            LocalDate targetDate = today.plusDays(offset);
            try {
                if (userEntity.getProfile() == null) {
                    log.warn("유저 프로필 없음 → 스킵 userId={}", userId);
                    return;
                }

                LLMDietRequestDto.DietAutoRequest req = buildRequest(userEntity); // 유저 프로필 + history 구성
                generateForUser(userEntity, targetDate, req);
            } catch (Exception e) {
                log.error("식단 생성 실패 userId={} date={}", userId, targetDate, e);
            }
        }
    }

    @Transactional
    public void generateDailyDiets() {
        LocalDate today = LocalDate.now();
        List<UserEntity> users = userRepository.findAll();

        // A, B, C 순차 처리
        for (int offset = 0; offset <= 2; offset++) {
            log.info(String.valueOf(offset));
            LocalDate targetDate = today.plusDays(offset);
            users.parallelStream()
                .forEach(user -> {
                    try {
                        if (user.getProfile() == null) {
                            log.warn("유저 프로필 없음 → 스킵 userId={}", user.getId());
                            return;
                        }

                        LLMDietRequestDto.DietAutoRequest req = buildRequest(user); // 유저 프로필 + history 구성
                        generateForUser(user, targetDate, req);
                    } catch (Exception e) {
                        log.error("식단 생성 실패 userId={} date={}", user.getId(), targetDate, e);
                    }
                });
        }
    }

    private LLMDietRequestDto.DietAutoRequest buildRequest(UserEntity user) {
        // 1. 유저 프로필
        LLMDietRequestDto.UserProfileRequestDto userProfile =
            LLMDietRequestDto.UserProfileRequestDto.create(user.getProfile());

        // 2. 최근 30일 기록 불러오기
        LocalDate today = LocalDate.now();
        LocalDate cutoff = today.minusDays(30);
        List<DietLogEntity> logs = dietLogRepository.findByUserAndLogDateBetween(user, cutoff, today);

        // 3. history 변환
        Map<String, Map<String, LLMDietRequestDto.MealRecordDto>> history = logs.stream()
            .collect(Collectors.groupingBy(
                log -> log.getLogDate()
                    .toString(), // 날짜 string key
                Collectors.toMap(
                    log -> log.getMealType()
                        .name(), // 끼니 (BREAKFAST, LUNCH, DINNER)
                    log -> {
                        List<LLMDietRequestDto.FoodRecordDto> foods = log.getItems()
                            .stream()
                            .map(item -> new LLMDietRequestDto.FoodRecordDto(
                                item.getFoodName(),
                                item.getCalories(),
                                item.getProtein(),
                                item.getCarbs(),
                                item.getFat()
                            ))
                            .toList();

                        return new LLMDietRequestDto.MealRecordDto(foods, "user log"); // 설명: 직접 기록/추천 수락 여부 등
                    }
                )
            ));

        // 4. 최종 요청 DTO 반환
        return new LLMDietRequestDto.DietAutoRequest(userProfile, history);
    }

}
