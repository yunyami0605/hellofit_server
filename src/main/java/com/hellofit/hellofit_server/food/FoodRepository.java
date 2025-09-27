package com.hellofit.hellofit_server.food;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FoodRepository extends JpaRepository<FoodEntity, UUID> {
    Optional<FoodEntity> findFirstByRepFoodName(String repFoodName);
}
