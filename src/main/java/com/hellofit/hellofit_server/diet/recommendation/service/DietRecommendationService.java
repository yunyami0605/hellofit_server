package com.hellofit.hellofit_server.diet.recommendation.service;

import com.hellofit.hellofit_server.diet.enums.MealType;
import com.hellofit.hellofit_server.diet.recommendation.DietRecommendationEntity;
import com.hellofit.hellofit_server.diet.recommendation.repository.DietRecommendationRepository;
import com.hellofit.hellofit_server.global.enums.RecordSource;
import com.hellofit.hellofit_server.user.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DietRecommendationService {

    private final DietRecommendationRepository recommendationRepository;

    public List<DietRecommendationEntity> getRecommendations(UserEntity user, LocalDate date) {
        return recommendationRepository.findByUserAndRecommendedDate(user, date);
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
