package com.hellofit.hellofit_server.workout.log.service;

import com.hellofit.hellofit_server.workout.log.WorkoutLogEntity;
import com.hellofit.hellofit_server.workout.log.WorkoutLogExerciseEntity;
import com.hellofit.hellofit_server.workout.log.repository.WorkoutLogExerciseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WorkoutLogExerciseService {

    private final WorkoutLogExerciseRepository exerciseRepository;

    public List<WorkoutLogExerciseEntity> getExercises(WorkoutLogEntity log) {
        return exerciseRepository.findByWorkoutLog(log);
    }

    public WorkoutLogExerciseEntity getById(UUID id) {
        return exerciseRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("운동 로그 세부 항목을 찾을 수 없습니다. id=" + id));
    }

    @Transactional
    public WorkoutLogExerciseEntity create(
        WorkoutLogEntity log,
        String exerciseName,
        Integer sets,
        Integer repetitions,
        Integer durationMinutes,
        Integer caloriesBurned
    ) {
        WorkoutLogExerciseEntity entity =
            WorkoutLogExerciseEntity.create(log, exerciseName, sets, repetitions, durationMinutes, caloriesBurned);
        return exerciseRepository.save(entity);
    }

    @Transactional
    public void delete(UUID id) {
        exerciseRepository.deleteById(id);
    }
}
