package com.hellofit.hellofit_server.diet.log.service;

import com.hellofit.hellofit_server.diet.enums.MealType;
import com.hellofit.hellofit_server.diet.log.DietLogEntity;
import com.hellofit.hellofit_server.diet.log.DietLogItemEntity;
import com.hellofit.hellofit_server.diet.log.repository.DietLogItemRepository;
import com.hellofit.hellofit_server.diet.log.repository.DietLogRepository;
import com.hellofit.hellofit_server.diet.recommendation.DietRecommendationEntity;
import com.hellofit.hellofit_server.food.FoodEntity;
import com.hellofit.hellofit_server.food.FoodRepository;
import com.hellofit.hellofit_server.global.enums.RecordSource;
import com.hellofit.hellofit_server.user.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DietLogService {

    private final DietLogRepository logRepository;
    private final DietLogItemRepository logItemRepository;
    private final FoodRepository foodRepository;

    public List<DietLogEntity> getLogs(UserEntity user, LocalDate date) {
        return logRepository.findByUserAndLogDate(user, date);
    }

    public Optional<DietLogEntity> getLog(UserEntity user, LocalDate date, MealType mealType) {
        return logRepository.findByUserAndLogDateAndMealType(user, date, mealType);
    }

    @Transactional
    public DietLogEntity create(UserEntity user, MealType mealType, LocalDate date,
                                RecordSource source, DietRecommendationEntity recommendation) {
        DietLogEntity log = DietLogEntity.create(user, mealType, date, source, recommendation);
        return logRepository.save(log);
    }

    @Transactional
    public void delete(UUID id) {
        logRepository.deleteById(id);
    }

    @Transactional
    public DietLogItemEntity addItem(DietLogEntity log, UUID foodId) {
        FoodEntity food = foodRepository.findById(foodId)
                                        .orElseThrow(() -> new IllegalArgumentException("해당 음식이 DB에 존재하지 않습니다: " + foodId));

        // weight 보정 (100g 기준 → 실제 DB weight 반영)
        double factor = (food.getWeight() != null && food.getWeight() > 0)
            ? food.getWeight() / 100.0
            : 1.0;

        DietLogItemEntity item = DietLogItemEntity.create(
            log,
            food.getRepFoodName(),
            (int) Math.round(food.getKcal() * factor),          // kcal은 정수 반올림
            roundOneDecimal(food.getProtein() * factor), // 단백질
            roundOneDecimal(food.getFat() * factor),     // 지방
            roundOneDecimal(food.getCarbs() * factor)    // 탄수화물
        );

        return logItemRepository.save(item);
    }

    private Double roundOneDecimal(Double value) {
        if (value == null) return null;
        return Math.round(value * 10.0) / 10.0;
    }

    @Transactional(readOnly = true)
    public List<DietLogEntity> getLogsInRange(UserEntity user, LocalDate startDate, LocalDate endDate) {
        return logRepository.findByUserAndLogDateBetween(user, startDate, endDate);
    }
}
