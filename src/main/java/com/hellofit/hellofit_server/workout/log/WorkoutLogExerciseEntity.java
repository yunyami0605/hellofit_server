package com.hellofit.hellofit_server.workout.log;

import com.hellofit.hellofit_server.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "workout_log_exercises")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WorkoutLogExerciseEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_log_id", nullable = false, columnDefinition = "CHAR(36)")
    private WorkoutLogEntity workoutLog;

    @Column(nullable = false, length = 100)
    private String exerciseName;

    private Integer sets;   // 세트 수
    private Integer repetitions;   // 반복 횟수
    private Integer durationMinutes;    // 운동 지속 시간 (분 단위)
    private Integer caloriesBurned;     // 소모 칼로리

    public static WorkoutLogExerciseEntity create(WorkoutLogEntity workoutLog, String exerciseName,
                                                  Integer sets, Integer repetitions,
                                                  Integer durationMinutes, Integer caloriesBurned) {
        WorkoutLogExerciseEntity entity = new WorkoutLogExerciseEntity();
        entity.workoutLog = workoutLog;
        entity.exerciseName = exerciseName;
        entity.sets = sets;
        entity.repetitions = repetitions;
        entity.durationMinutes = durationMinutes;
        entity.caloriesBurned = caloriesBurned;
        return entity;
    }
}
