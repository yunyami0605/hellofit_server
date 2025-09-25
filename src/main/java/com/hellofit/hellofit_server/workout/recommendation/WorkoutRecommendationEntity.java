package com.hellofit.hellofit_server.workout.recommendation;

import com.hellofit.hellofit_server.global.entity.BaseEntity;
import com.hellofit.hellofit_server.global.enums.RecordSource;
import com.hellofit.hellofit_server.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "workout_recommendations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WorkoutRecommendationEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, columnDefinition = "CHAR(36)")
    private UserEntity user;

    @Column(nullable = false)
    private LocalDate recommendedDate;

    @Column(nullable = false, length = 20)
    private RecordSource source; // AI / USER

    @OneToMany(mappedBy = "recommendation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkoutRecommendationExerciseEntity> exercises = new ArrayList<>();

    public static WorkoutRecommendationEntity create(UserEntity user, LocalDate recommendedDate, RecordSource source) {
        WorkoutRecommendationEntity entity = new WorkoutRecommendationEntity();
        entity.user = user;
        entity.recommendedDate = recommendedDate;
        entity.source = source;
        return entity;
    }
}
