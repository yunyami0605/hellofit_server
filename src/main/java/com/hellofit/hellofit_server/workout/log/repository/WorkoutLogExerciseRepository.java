package com.hellofit.hellofit_server.workout.log.repository;

import com.hellofit.hellofit_server.workout.log.WorkoutLogEntity;
import com.hellofit.hellofit_server.workout.log.WorkoutLogExerciseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface WorkoutLogExerciseRepository extends JpaRepository<WorkoutLogExerciseEntity, UUID> {

    // 운동 로그에 속한 세부 운동 목록
    List<WorkoutLogExerciseEntity> findByWorkoutLog(WorkoutLogEntity log);
}
