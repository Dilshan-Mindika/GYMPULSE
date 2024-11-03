package com.nexus.GYMPULSE.service.interfaces;

import java.util.List;
import java.util.Optional;

import com.nexus.GYMPULSE.model.workoutplan.DailyWorkout;
import com.nexus.GYMPULSE.model.workoutplan.Exercise;
import com.nexus.GYMPULSE.requests.DailyWorkoutRequest;

public interface DailyWorkoutService {
    DailyWorkout createDailyWorkout(String dayOfWeek, List<Exercise> exercises);
    List<DailyWorkout> allDailyWorkouts();
    Optional<DailyWorkout> dailyWorkoutById(String id);
    Optional<DailyWorkout> dailyWorkoutByDayOfWeek(String dayOfWeek);
    DailyWorkout updateDailyWorkout(String id, DailyWorkoutRequest dailyWorkoutRequest);
    void deleteDailyWorkout(String id);
}
