package com.hellofit.hellofit_server.diet.log.repository;

import com.hellofit.hellofit_server.diet.enums.MealType;
import com.hellofit.hellofit_server.diet.log.DietLogEntity;
import com.hellofit.hellofit_server.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DietLogRepository extends JpaRepository<DietLogEntity, UUID> {

    // 특정 유저, 날짜의 모든 식단 로그
    List<DietLogEntity> findByUserAndLogDate(UserEntity user, LocalDate logDate);

    // 특정 유저, 날짜, 끼니 로그 (unique 보장)
    Optional<DietLogEntity> findByUserAndLogDateAndMealType(UserEntity user, LocalDate logDate, MealType mealType);

    // 특정 유저, 기간 조회
    List<DietLogEntity> findByUserAndLogDateBetween(UserEntity user, LocalDate startDate, LocalDate endDate);
}
