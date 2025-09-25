package com.hellofit.hellofit_server.workout.recommendation.repository;

import com.hellofit.hellofit_server.user.UserEntity;
import com.hellofit.hellofit_server.workout.recommendation.WorkoutRecommendationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface WorkoutRecommendationRepository extends JpaRepository<WorkoutRecommendationEntity, UUID> {

    // 특정 유저, 날짜 추천 조회
    List<WorkoutRecommendationEntity> findByUserAndRecommendedDate(UserEntity user, LocalDate date);

    // 특정 유저 전체 추천 (최신순)
    List<WorkoutRecommendationEntity> findByUserOrderByRecommendedDateDesc(UserEntity user);
}
