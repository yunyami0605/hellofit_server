package com.hellofit.hellofit_server.diet.log;

import com.hellofit.hellofit_server.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "diet_log_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DietLogItemEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diet_log_id", nullable = false, columnDefinition = "CHAR(36)")
    private DietLogEntity dietLog;

    @Column(nullable = false, length = 100)
    private String foodName;

    private Integer calories;   // 칼로리
    private Double protein; // 단백질
    private Double fat; // 지방
    private Double carbs;   // 탄소화물

    public static DietLogItemEntity create(DietLogEntity dietLog, String foodName,
                                           Integer calories, Double protein, Double fat, Double carbs) {
        DietLogItemEntity entity = new DietLogItemEntity();
        entity.dietLog = dietLog;
        entity.foodName = foodName;
        entity.calories = calories;
        entity.protein = protein;
        entity.fat = fat;
        entity.carbs = carbs;
        return entity;
    }
}
