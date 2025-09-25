package com.hellofit.hellofit_server.diet.log.service;

import com.hellofit.hellofit_server.diet.log.DietLogEntity;
import com.hellofit.hellofit_server.diet.log.DietLogItemEntity;
import com.hellofit.hellofit_server.diet.log.repository.DietLogItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DietLogItemService {

    private final DietLogItemRepository logItemRepository;

    public List<DietLogItemEntity> getItems(DietLogEntity log) {
        return logItemRepository.findByDietLog(log);
    }

    public DietLogItemEntity getById(UUID id) {
        return logItemRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("식단 로그 아이템을 찾을 수 없습니다. id=" + id));
    }

    @Transactional
    public DietLogItemEntity create(DietLogEntity log, String foodName,
                                    Integer calories, Double protein, Double fat, Double carbs) {
        DietLogItemEntity item = DietLogItemEntity.create(log, foodName, calories, protein, fat, carbs);
        return logItemRepository.save(item);
    }

    @Transactional
    public void delete(UUID id) {
        logItemRepository.deleteById(id);
    }
}
