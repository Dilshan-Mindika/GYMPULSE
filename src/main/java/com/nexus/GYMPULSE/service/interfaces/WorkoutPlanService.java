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

    /**
     * Creates a new workout plan.
     *
     * @param memberId the ID of the member
     * @param trainerId the ID of the trainer
     * @param startDate the start date of the workout plan
     * @param endDate the end date of the workout plan
     * @param dailyWorkouts the list of daily workouts
     * @return the created WorkoutPlan
     */
    WorkoutPlan createWorkoutPlan(String memberId, String trainerId, String startDate, String endDate, List<DailyWorkout> dailyWorkouts);

    /**
     * Retrieves all workout plans.
     *
     * @return a list of all WorkoutPlans
     */
    List<WorkoutPlan> allWorkoutPlans();

    /**
     * Finds a workout plan by trainer and member IDs.
     *
     * @param trainerId the ID of the trainer
     * @param memberId the ID of the member
     * @return an Optional containing the WorkoutPlan if found, or empty if not
     */
    Optional<WorkoutPlan> findWorkoutPlanByTrainerAndMemberId(String trainerId, String memberId);

    /**
     * Finds a workout plan by its ID.
     *
     * @param id the ID of the workout plan
     * @return an Optional containing the WorkoutPlan if found, or empty if not
     */
    Optional<WorkoutPlan> findWorkoutPlanById(String id);

    /**
     * Finds all workout plans associated with a specific member ID.
     *
     * @param memberId the ID of the member
     * @return a list of WorkoutPlans associated with the member
     */
    List<WorkoutPlan> findWorkoutPlansByMemberId(String memberId);

    Optional<WorkoutPlan> findWorkoutPlanByIds(String trainerId, String memberId);

    /**
     * Deletes a workout plan by trainer and member IDs.
     *
     * @param trainerId the ID of the trainer
     * @param memberId the ID of the member
     */
    void deleteByTrainerAndMemberId(String trainerId, String memberId);

    /**
     * Updates an existing workout plan.
     *
     * @param id the ID of the workout plan to update
     * @param workoutPlanRequest the new data for the workout plan
     * @return the updated WorkoutPlan
     */
    WorkoutPlan updateWorkoutPlan(String id, WorkoutPlanRequest workoutPlanRequest);

    /**
     * Deletes a workout plan by its ID.
     *
     * @param id the ID of the workout plan to delete
     */
    void deleteById(String id);

    /**
     * Deletes a workout plan by trainer and member IDs.
     *
     * @param trainerId the ID of the trainer
     * @param memberId the ID of the member
     */
    void deleteByIds(String trainerId, String memberId);

    /**
     * Creates a new workout plan using a specific strategy.
     *
     * @param memberId the ID of the member
     * @param trainerId the ID of the trainer
     * @param startDate the start date of the workout plan
     * @param endDate the end date of the workout plan
     * @param strategy the workout strategy to use
     * @return the created WorkoutPlan
     */
    WorkoutPlan createWorkoutPlanWithStrategy(String memberId, String trainerId, String startDate, String endDate, WorkoutStrategy strategy);
}
