package com.hellofit.hellofit_server.diet.recommendation.service;

import com.hellofit.hellofit_server.diet.enums.MealType;
import com.hellofit.hellofit_server.diet.recommendation.DietRecommendationEntity;
import com.hellofit.hellofit_server.diet.recommendation.repository.DietRecommendationRepository;
import com.hellofit.hellofit_server.global.enums.RecordSource;
import com.hellofit.hellofit_server.user.UserEntity;
import com.hellofit.hellofit_server.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DietRecommendationService {

    private final DietRecommendationRepository recommendationRepository;
    private final UserRepository userRepository;

    public List<DietRecommendationEntity> getRecommendations(UserEntity user, LocalDate date) {
        List<DietRecommendationEntity> recommendations;

        if (date == null) {
            LocalDate today = LocalDate.now();
            recommendations = recommendationRepository.findByUserAndRecommendedDateBetweenOrderByRecommendedDateAsc(
                user, today, today.plusDays(2)
            );
        } else {
            recommendations = recommendationRepository.findByUserAndRecommendedDate(user, date);
        }

        // 날짜 → 끼니 순서 정렬
        recommendations.sort(Comparator
            .comparing(DietRecommendationEntity::getRecommendedDate)
            .thenComparing(r -> r.getMealType()
                .ordinal())
        );

        return recommendations;
    }

    public DietRecommendationEntity getById(UUID id) {
        return recommendationRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("추천 식단을 찾을 수 없습니다. id=" + id));
    }

    @Transactional
    public DietRecommendationEntity create(UserEntity user, MealType mealType, LocalDate date, RecordSource source) {
        DietRecommendationEntity recommendation =
            DietRecommendationEntity.create(user, mealType, date, source);
        return recommendationRepository.save(recommendation);
    }

    @Transactional
    public void delete(UUID id) {
        recommendationRepository.deleteById(id);
    }
}
