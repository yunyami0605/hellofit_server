package com.hellofit.hellofit_server.diet.log.repository;

import com.hellofit.hellofit_server.diet.log.DietLogEntity;
import com.hellofit.hellofit_server.diet.log.DietLogItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DietLogItemRepository extends JpaRepository<DietLogItemEntity, UUID> {

    // 로그에 속한 음식 아이템들 조회
    List<DietLogItemEntity> findByDietLog(DietLogEntity dietLog);
}
