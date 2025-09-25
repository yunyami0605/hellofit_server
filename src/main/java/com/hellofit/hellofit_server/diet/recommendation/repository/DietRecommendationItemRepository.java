package com.hellofit.hellofit_server.diet.recommendation.repository;

import com.hellofit.hellofit_server.diet.recommendation.DietRecommendationEntity;
import com.hellofit.hellofit_server.diet.recommendation.DietRecommendationItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DietRecommendationItemRepository extends JpaRepository<DietRecommendationItemEntity, UUID> {

    // 추천에 속한 항목 조회
    List<DietRecommendationItemEntity> findByRecommendation(DietRecommendationEntity recommendation);
}
