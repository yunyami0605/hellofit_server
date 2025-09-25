package com.hellofit.hellofit_server.workout.log.repository;

import com.hellofit.hellofit_server.user.UserEntity;
import com.hellofit.hellofit_server.workout.log.WorkoutLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkoutLogRepository extends JpaRepository<WorkoutLogEntity, UUID> {

    // 여러 건 조회 (보통 기간 검색이나 잘못된 데이터 처리용)
    List<WorkoutLogEntity> findByUserAndLogDate(UserEntity user, LocalDate logDate);

    // 단일 건 조회 (unique 보장)
    Optional<WorkoutLogEntity> findOneByUserAndLogDate(UserEntity user, LocalDate logDate);

    // 특정 유저, 기간별 조회
    List<WorkoutLogEntity> findByUserAndLogDateBetween(UserEntity user, LocalDate startDate, LocalDate endDate);
}
