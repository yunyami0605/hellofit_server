package com.hellofit.hellofit_server.workout.log.service;

import com.hellofit.hellofit_server.user.UserEntity;
import com.hellofit.hellofit_server.workout.log.WorkoutLogEntity;
import com.hellofit.hellofit_server.workout.log.repository.WorkoutLogRepository;
import com.hellofit.hellofit_server.workout.recommendation.WorkoutRecommendationEntity;
import com.hellofit.hellofit_server.global.enums.RecordSource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WorkoutLogService {

    private final WorkoutLogRepository logRepository;

    public List<WorkoutLogEntity> getLogs(UserEntity user, LocalDate date) {
        return logRepository.findByUserAndLogDate(user, date);
    }

    public Optional<WorkoutLogEntity> getLog(UserEntity user, LocalDate date) {
        return logRepository.findOneByUserAndLogDate(user, date);
    }

    @Transactional
    public WorkoutLogEntity create(UserEntity user, LocalDate date,
                                   RecordSource source, WorkoutRecommendationEntity recommendation,
                                   Integer totalMinutes, Integer totalCaloriesBurned) {
        WorkoutLogEntity log = WorkoutLogEntity.create(user, date, source, recommendation, totalMinutes, totalCaloriesBurned);
        return logRepository.save(log);
    }

    @Transactional
    public void delete(UUID id) {
        logRepository.deleteById(id);
    }
}
