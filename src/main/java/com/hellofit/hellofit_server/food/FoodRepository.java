package com.hellofit.hellofit_server.food;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FoodRepository extends JpaRepository<FoodEntity, UUID> {
    Optional<FoodEntity> findFirstByRepFoodName(String repFoodName);

    // 검색어가 있는 경우 (cursor가 없는 경우)
    @Query("SELECT f FROM FoodEntity f " +
        "WHERE LOWER(f.repFoodName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
        "ORDER BY f.createdAt DESC")
    List<FoodEntity> findFirstPage(String keyword, Pageable pageable);

    // cursor 이후 데이터 조회
    @Query("SELECT f FROM FoodEntity f " +
        "WHERE LOWER(f.repFoodName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
        "AND f.createdAt <= (SELECT f2.createdAt FROM FoodEntity f2 WHERE f2.id = :cursorId) " +
        "AND f.id < :cursorId " +
        "ORDER BY f.createdAt DESC")
    List<FoodEntity> findByCursor(String keyword, UUID cursorId, Pageable pageable);
}
