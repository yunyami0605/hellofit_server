package com.hellofit.hellofit_server.workout.recommendation.repository;

import com.hellofit.hellofit_server.workout.recommendation.WorkoutRecommendationEntity;
import com.hellofit.hellofit_server.workout.recommendation.WorkoutRecommendationExerciseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface WorkoutRecommendationExerciseRepository extends JpaRepository<WorkoutRecommendationExerciseEntity, UUID> {

    // 추천에 속한 운동 목록
    List<WorkoutRecommendationExerciseEntity> findByRecommendation(WorkoutRecommendationEntity recommendation);
}
