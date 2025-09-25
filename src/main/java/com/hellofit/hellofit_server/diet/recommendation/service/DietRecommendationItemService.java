package com.hellofit.hellofit_server.diet.recommendation.service;

import com.hellofit.hellofit_server.diet.recommendation.DietRecommendationEntity;
import com.hellofit.hellofit_server.diet.recommendation.DietRecommendationItemEntity;
import com.hellofit.hellofit_server.diet.recommendation.repository.DietRecommendationItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DietRecommendationItemService {

    private final DietRecommendationItemRepository itemRepository;

    public List<DietRecommendationItemEntity> getItems(DietRecommendationEntity recommendation) {
        return itemRepository.findByRecommendation(recommendation);
    }

    public DietRecommendationItemEntity getById(UUID id) {
        return itemRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("추천 음식 아이템을 찾을 수 없습니다. id=" + id));
    }

    @Transactional
    public DietRecommendationItemEntity create(DietRecommendationEntity recommendation, String foodName,
                                               Integer calories, Double protein, Double fat, Double carbs) {
        DietRecommendationItemEntity item = DietRecommendationItemEntity.create(
            recommendation, foodName, calories, protein, fat, carbs
        );
        return itemRepository.save(item);
    }

    @Transactional
    public void delete(UUID id) {
        itemRepository.deleteById(id);
    }
}
