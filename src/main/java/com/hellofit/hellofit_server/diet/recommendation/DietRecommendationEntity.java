package com.hellofit.hellofit_server.diet.recommendation;

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
@Table(name = "diet_recommendations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DietRecommendationEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, columnDefinition = "CHAR(36)")
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MealType mealType; // 아침/점심/저녁

    @Column(nullable = false)
    private LocalDate recommendedDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RecordSource source; // AI / USER

    @OneToMany(mappedBy = "recommendation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DietRecommendationItemEntity> items = new ArrayList<>();

    public static DietRecommendationEntity create(UserEntity user, MealType mealType, LocalDate recommendedDate, RecordSource source) {
        DietRecommendationEntity entity = new DietRecommendationEntity();
        entity.user = user;
        entity.mealType = mealType;
        entity.recommendedDate = recommendedDate;
        entity.source = source;
        return entity;
    }
}
