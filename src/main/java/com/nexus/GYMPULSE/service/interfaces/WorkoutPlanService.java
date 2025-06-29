package com.nexus.GYMPULSE.service.interfaces;

import java.util.List;
import java.util.Optional;

import com.nexus.GYMPULSE.model.workoutplan.DailyWorkout;
import com.nexus.GYMPULSE.model.workoutplan.WorkoutPlan;
import com.nexus.GYMPULSE.requests.WorkoutPlanRequest;
import com.nexus.GYMPULSE.model.workoutplan.strategies.WorkoutStrategy;

/**
 * Service interface for managing workout plans.
 */
public interface WorkoutPlanService {

    WorkoutPlan createWorkoutPlan(WorkoutPlanRequest workoutPlanRequest); // Changed signature

    List<WorkoutPlan> allWorkoutPlans();

    // Optional<WorkoutPlan> findWorkoutPlanByTrainerAndMemberId(String trainerId, String memberId); // Removed, use findWorkoutPlanByIds
    Optional<WorkoutPlan> findWorkoutPlanById(String id);

    List<WorkoutPlan> findWorkoutPlansByMemberId(String memberId);

    Optional<WorkoutPlan> findWorkoutPlanByIds(String trainerId, String memberId); // Kept this one

    // void deleteByTrainerAndMemberId(String trainerId, String memberId); // Removed, use deleteByIds

    WorkoutPlan updateWorkoutPlan(String id, WorkoutPlanRequest workoutPlanRequest);

    void deleteById(String id);

    void deleteByIds(String trainerId, String memberId);

    WorkoutPlan createWorkoutPlanWithStrategy(WorkoutPlanRequest workoutPlanRequest, WorkoutStrategy strategy); // Changed signature
}
