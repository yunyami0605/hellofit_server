package com.hellofit.hellofit_server.diet.recommendation;

import com.hellofit.hellofit_server.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "diet_recommendation_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DietRecommendationItemEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recommendation_id", nullable = false, columnDefinition = "CHAR(36)")
    private DietRecommendationEntity recommendation;

    @Column(nullable = false, length = 100)
    private String foodName;

    private Integer calories;
    private Double protein;
    private Double fat;
    private Double carbs;

    public static DietRecommendationItemEntity create(DietRecommendationEntity recommendation, String foodName,
                                                      Integer calories, Double protein, Double fat, Double carbs) {
        DietRecommendationItemEntity entity = new DietRecommendationItemEntity();
        entity.recommendation = recommendation;
        entity.foodName = foodName;
        entity.calories = calories;
        entity.protein = protein;
        entity.fat = fat;
        entity.carbs = carbs;
        return entity;
    }
}
