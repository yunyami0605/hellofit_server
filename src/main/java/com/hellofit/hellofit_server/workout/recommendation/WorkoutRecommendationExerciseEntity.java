package com.hellofit.hellofit_server.workout.recommendation;

import com.hellofit.hellofit_server.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "workout_recommendation_exercises")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WorkoutRecommendationExerciseEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recommendation_id", nullable = false, columnDefinition = "CHAR(36)")
    private WorkoutRecommendationEntity recommendation;

    @Column(nullable = false, length = 100)
    private String exerciseName;

    private Integer sets;
    private Integer repetitions;
    private Integer durationMinutes;
    private Integer caloriesBurned;

    public static WorkoutRecommendationExerciseEntity create(WorkoutRecommendationEntity recommendation,
                                                             String exerciseName,
                                                             Integer sets, Integer repetitions,
                                                             Integer durationMinutes, Integer caloriesBurned) {
        WorkoutRecommendationExerciseEntity entity = new WorkoutRecommendationExerciseEntity();
        entity.recommendation = recommendation;
        entity.exerciseName = exerciseName;
        entity.sets = sets;
        entity.repetitions = repetitions;
        entity.durationMinutes = durationMinutes;
        entity.caloriesBurned = caloriesBurned;
        return entity;
    }
}
