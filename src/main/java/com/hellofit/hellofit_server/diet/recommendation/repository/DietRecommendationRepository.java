package com.hellofit.hellofit_server.diet.recommendation.repository;

import com.hellofit.hellofit_server.diet.recommendation.DietRecommendationEntity;
import com.hellofit.hellofit_server.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface DietRecommendationRepository extends JpaRepository<DietRecommendationEntity, UUID> {

    // 특정 유저, 특정 날짜 추천 조회
    List<DietRecommendationEntity> findByUserAndRecommendedDate(UserEntity user, LocalDate recommendedDate);

    // 특정 유저의 전체 추천 조회 (최신순)
    List<DietRecommendationEntity> findByUserOrderByRecommendedDateDesc(UserEntity user);

    // 특정 유저의 오늘 포함 3일치 추천 조회 (날짜 오름차순)
    List<DietRecommendationEntity> findByUserAndRecommendedDateBetweenOrderByRecommendedDateAsc(
        UserEntity user,
        LocalDate startDate,
        LocalDate endDate
    );
}
