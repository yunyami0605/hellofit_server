package com.hellofit.hellofit_server.diet.log.service;

import com.hellofit.hellofit_server.diet.enums.MealType;
import com.hellofit.hellofit_server.diet.log.DietLogEntity;
import com.hellofit.hellofit_server.diet.log.repository.DietLogRepository;
import com.hellofit.hellofit_server.diet.recommendation.DietRecommendationEntity;
import com.hellofit.hellofit_server.global.enums.RecordSource;
import com.hellofit.hellofit_server.user.UserEntity;
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
public class DietLogService {

    private final DietLogRepository logRepository;

    public List<DietLogEntity> getLogs(UserEntity user, LocalDate date) {
        return logRepository.findByUserAndLogDate(user, date);
    }

    public Optional<DietLogEntity> getLog(UserEntity user, LocalDate date, MealType mealType) {
        return logRepository.findByUserAndLogDateAndMealType(user, date, mealType);
    }

    @Transactional
    public DietLogEntity create(UserEntity user, MealType mealType, LocalDate date,
                                RecordSource source, DietRecommendationEntity recommendation) {
        DietLogEntity log = DietLogEntity.create(user, mealType, date, source, recommendation);
        return logRepository.save(log);
    }

    @Transactional
    public void delete(UUID id) {
        logRepository.deleteById(id);
    }
}
