package com.hellofit.hellofit_server.workout.recommendation.service;

import com.hellofit.hellofit_server.workout.recommendation.WorkoutRecommendationEntity;
import com.hellofit.hellofit_server.workout.recommendation.WorkoutRecommendationExerciseEntity;
import com.hellofit.hellofit_server.workout.recommendation.repository.WorkoutRecommendationExerciseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WorkoutRecommendationExerciseService {

    private final WorkoutRecommendationExerciseRepository exerciseRepository;

    public List<WorkoutRecommendationExerciseEntity> getExercises(WorkoutRecommendationEntity recommendation) {
        return exerciseRepository.findByRecommendation(recommendation);
    }

    public WorkoutRecommendationExerciseEntity getById(UUID id) {
        return exerciseRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("추천 운동을 찾을 수 없습니다. id=" + id));
    }

    @Transactional
    public WorkoutRecommendationExerciseEntity create(
        WorkoutRecommendationEntity recommendation,
        String exerciseName,
        Integer sets,
        Integer repetitions,
        Integer durationMinutes,
        Integer caloriesBurned
    ) {
        WorkoutRecommendationExerciseEntity entity =
            WorkoutRecommendationExerciseEntity.create(recommendation, exerciseName, sets, repetitions, durationMinutes, caloriesBurned);
        return exerciseRepository.save(entity);
    }

    @Transactional
    public void delete(UUID id) {
        exerciseRepository.deleteById(id);
    }
}
