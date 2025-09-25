package com.hellofit.hellofit_server.diet.log;

import com.hellofit.hellofit_server.diet.recommendation.DietRecommendationEntity;
import com.hellofit.hellofit_server.diet.enums.MealType;
import com.hellofit.hellofit_server.global.entity.BaseEntity;
import com.hellofit.hellofit_server.global.enums.RecordSource;
import com.hellofit.hellofit_server.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "diet_logs",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "log_date", "meal_type"})}
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DietLogEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, columnDefinition = "CHAR(36)")
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MealType mealType;

    @Column(nullable = false)
    private LocalDate logDate;  // 식단 날짜

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RecordSource source; // AI / USER

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recommendation_id", columnDefinition = "CHAR(36)")
    private DietRecommendationEntity recommendation;

    @OneToMany(mappedBy = "dietLog", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DietLogItemEntity> items = new ArrayList<>();

    public static DietLogEntity create(UserEntity user, MealType mealType, LocalDate logDate,
                                       RecordSource source, DietRecommendationEntity recommendation) {
        DietLogEntity entity = new DietLogEntity();
        entity.user = user;
        entity.mealType = mealType;
        entity.logDate = logDate;
        entity.source = source;
        entity.recommendation = recommendation;
        return entity;
    }
}
