package com.hellofit.hellofit_server.food;

import com.hellofit.hellofit_server.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "foods")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodEntity extends BaseEntity {
    @Column(nullable = false)
    private String foodCode;       // 식품코드

    @Column(nullable = false)
    private String foodName;       // 식품명

    @Column(nullable = false)
    private String category;       // 식품대분류명

    @Column(nullable = false)
    private String repFoodName;    // 대표식품명

    @Column(nullable = false)
    private Float kcal;            // 에너지(kcal)

    @Column(nullable = false)
    private Float protein;         // 단백질(g)

    @Column(nullable = false)
    private Float fat;             // 지방(g)

    @Column(nullable = false)
    private Float carbs;           // 탄수화물(g)

    @Column(nullable = false)
    private Float sugar;           // 당류(g)

    @Column(nullable = false)
    private Float calcium;         // 칼슘(mg)

    @Column(nullable = false)
    private Float sodium;          // 나트륨(mg)

    @Column(nullable = false)
    private Float cholesterol;     // 콜레스테롤(mg)

    @Column(nullable = false)
    private Float weight;          // 식품중량

    @Column(nullable = false)
    private LocalDate dataDate;    // 데이터기준일자
}
