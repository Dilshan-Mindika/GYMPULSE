package com.nexus.GYMPULSE.model.workoutplan.strategies;

import java.util.List;
import com.nexus.GYMPULSE.model.workoutplan.DailyWorkout;

public interface WorkoutStrategy {
    List<DailyWorkout> generateRoutine();
}
