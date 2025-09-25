package com.hellofit.hellofit_server.workout.recommendation.service;

import com.hellofit.hellofit_server.user.UserEntity;
import com.hellofit.hellofit_server.workout.recommendation.WorkoutRecommendationEntity;
import com.hellofit.hellofit_server.workout.recommendation.repository.WorkoutRecommendationRepository;
import com.hellofit.hellofit_server.global.enums.RecordSource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WorkoutRecommendationService {

    private final WorkoutRecommendationRepository recommendationRepository;

    public List<WorkoutRecommendationEntity> getRecommendations(UserEntity user, LocalDate date) {
        return recommendationRepository.findByUserAndRecommendedDate(user, date);
    }

    public WorkoutRecommendationEntity getById(UUID id) {
        return recommendationRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("운동 추천을 찾을 수 없습니다. id=" + id));
    }

    @Transactional
    public WorkoutRecommendationEntity create(UserEntity user, LocalDate date, RecordSource source) {
        WorkoutRecommendationEntity recommendation =
            WorkoutRecommendationEntity.create(user, date, source);
        return recommendationRepository.save(recommendation);
    }

    @Transactional
    public void delete(UUID id) {
        recommendationRepository.deleteById(id);
    }
}
