package com.nexus.GYMPULSE.service.interfaces;

import java.util.List;
import java.util.Optional;

import com.nexus.GYMPULSE.model.workoutplan.DailyWorkout;
import com.nexus.GYMPULSE.model.workoutplan.WorkoutPlan;
import com.nexus.GYMPULSE.requests.WorkoutPlanRequest;
import com.nexus.GYMPULSE.model.workoutplan.strategies.WorkoutStrategy;

public interface WorkoutPlanService {
    WorkoutPlan createWorkoutPlan(String memberId, String trainerId, String startDate, String endDate, List<DailyWorkout> dailyWorkouts);
    List<WorkoutPlan> allWorkoutPlans();
    Optional<WorkoutPlan> findWorkoutPlanByIds(String trainerId, String memberId);
    Optional<WorkoutPlan> findWorkoutPlanById(String id);
    List<WorkoutPlan> findWorkoutPlansByMemberId(String memberId);
    WorkoutPlan updateWorkoutPlan(String id, WorkoutPlanRequest workoutPlanRequest);
    void deleteById(String id);
    void deleteByIds(String trainerId, String memberId);
    WorkoutPlan createWorkoutPlanWithStrategy(String memberId, String trainerId, String startDate, String endDate, WorkoutStrategy strategy);
}
