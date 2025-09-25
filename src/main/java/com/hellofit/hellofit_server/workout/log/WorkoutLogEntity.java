package com.hellofit.hellofit_server.workout.log;

import com.hellofit.hellofit_server.global.entity.BaseEntity;
import com.hellofit.hellofit_server.global.enums.RecordSource;
import com.hellofit.hellofit_server.user.UserEntity;
import com.hellofit.hellofit_server.workout.recommendation.WorkoutRecommendationEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "workout_logs",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "log_date"})}
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WorkoutLogEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, columnDefinition = "CHAR(36)")
    private UserEntity user;

    @Column(nullable = false)
    private LocalDate logDate;

    @Column(nullable = false, length = 20)
    private RecordSource source; // AI / USER

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recommendation_id", columnDefinition = "CHAR(36)")
    private WorkoutRecommendationEntity recommendation;

    private Integer totalMinutes;
    private Integer totalCaloriesBurned;

    @OneToMany(mappedBy = "workoutLog", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkoutLogExerciseEntity> exercises = new ArrayList<>();

    public static WorkoutLogEntity create(UserEntity user, LocalDate logDate, RecordSource source,
                                          WorkoutRecommendationEntity recommendation,
                                          Integer totalMinutes, Integer totalCaloriesBurned) {
        WorkoutLogEntity entity = new WorkoutLogEntity();
        entity.user = user;
        entity.logDate = logDate;
        entity.source = source;
        entity.recommendation = recommendation;
        entity.totalMinutes = totalMinutes;
        entity.totalCaloriesBurned = totalCaloriesBurned;
        return entity;
    }
}
