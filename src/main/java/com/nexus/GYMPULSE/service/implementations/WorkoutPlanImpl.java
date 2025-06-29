package com.nexus.GYMPULSE.service.implementations;

import java.util.List;
// import java.util.NoSuchElementException; // Replaced with custom exception
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nexus.GYMPULSE.exception.ResourceNotFoundException; // Import custom exception
import com.nexus.GYMPULSE.model.workoutplan.DailyWorkout;
import com.nexus.GYMPULSE.model.workoutplan.WorkoutPlan;
import com.nexus.GYMPULSE.model.workoutplan.strategies.WorkoutStrategy;
import com.nexus.GYMPULSE.repositories.WorkoutPlanRepository;
import com.nexus.GYMPULSE.requests.WorkoutPlanRequest;
import com.nexus.GYMPULSE.service.interfaces.WorkoutPlanService;
// import com.nexus.GYMPULSE.utils.GymLogger; // Will be removed

@Service
public class WorkoutPlanImpl implements WorkoutPlanService {

    private static final Logger logger = LoggerFactory.getLogger(WorkoutPlanImpl.class); // SLF4J Logger

    @Autowired
    private WorkoutPlanRepository workoutPlanRepository;

    @Override
    public WorkoutPlan createWorkoutPlan(WorkoutPlanRequest workoutPlanRequest) { // Changed signature
        String id = generateWorkoutPlanId(workoutPlanRequest.getMemberId(), workoutPlanRequest.getTrainerId());
        WorkoutPlan workoutPlan = new WorkoutPlan(
                id,
                workoutPlanRequest.getMemberId(),
                workoutPlanRequest.getTrainerId(),
                workoutPlanRequest.getStartDate(),
                workoutPlanRequest.getEndDate(),
                workoutPlanRequest.getDailyWorkouts()
        );

        // Save the workout plan in the database
        WorkoutPlan savedPlan = workoutPlanRepository.insert(workoutPlan);
        logger.info("New Workout Plan created, ID: {}", savedPlan.getId()); // SLF4J logging
        return savedPlan;
    }

    @Override
    public List<WorkoutPlan> allWorkoutPlans() {
        return workoutPlanRepository.findAll();
    }

    // Removed findWorkoutPlanByTrainerAndMemberId as redundant with findWorkoutPlanByIds
    // @Override
    // public Optional<WorkoutPlan> findWorkoutPlanByTrainerAndMemberId(String trainerId, String memberId) {
    //      // This was not implemented. Assuming it should use the repository method.
    //     return workoutPlanRepository.findByMemberIdAndTrainerId(memberId, trainerId);
    // }

    @Override
    public Optional<WorkoutPlan> findWorkoutPlanById(String id) {
        return workoutPlanRepository.findById(id);
    }

    @Override
    public List<WorkoutPlan> findWorkoutPlansByMemberId(String memberId) {
        return workoutPlanRepository.findAll().stream()
                .filter(workoutPlan -> memberId.equals(workoutPlan.getMemberId()))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<WorkoutPlan> findWorkoutPlanByIds(String trainerId, String memberId) {
        return workoutPlanRepository.findByMemberIdAndTrainerId(memberId, trainerId);
    }

    // Removed deleteByTrainerAndMemberId as redundant with deleteByIds
    // @Override
    // public void deleteByTrainerAndMemberId(String trainerId, String memberId) {
    //     String id = generateWorkoutPlanId(memberId, trainerId);
    //     WorkoutPlan workoutPlan = workoutPlanRepository.findByMemberIdAndTrainerId(memberId, trainerId)
    //             .orElseThrow(() -> new ResourceNotFoundException("WorkoutPlan", "memberId and trainerId", memberId + " & " + trainerId));
    //
    //     logger.info("Workout Plan deleted, ID: {}", id); // SLF4J logging
    //     workoutPlanRepository.delete(workoutPlan);
    // }

    @Override
    public WorkoutPlan updateWorkoutPlan(String id, WorkoutPlanRequest workoutPlanRequest) {
        WorkoutPlan workoutPlan = workoutPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WorkoutPlan", "id", id));

        // Consider if memberId and trainerId should be updatable as they form the ID.
        // If they are part of the ID, changing them would mean creating a new entity or handling ID changes carefully.
        // For now, allowing update as per original logic.
        workoutPlan.setMemberId(workoutPlanRequest.getMemberId());
        workoutPlan.setTrainerId(workoutPlanRequest.getTrainerId());
        workoutPlan.setStartDate(workoutPlanRequest.getStartDate());
        workoutPlan.setEndDate(workoutPlanRequest.getEndDate());
        workoutPlan.setDailyWorkouts(workoutPlanRequest.getDailyWorkouts());
        logger.info("Workout Plan updated, ID: {}", id); // SLF4J logging
        return workoutPlanRepository.save(workoutPlan);
    }

    @Override
    public void deleteById(String id) {
        WorkoutPlan workoutPlan = workoutPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WorkoutPlan", "id", id));

        logger.info("Workout Plan removed, ID: {}", id); // SLF4J logging
        workoutPlanRepository.delete(workoutPlan);
    }

    @Override
    public void deleteByIds(String trainerId, String memberId) {
        // Implemented to match deleteByTrainerAndMemberId logic for consistency
        String id = generateWorkoutPlanId(memberId, trainerId);
        WorkoutPlan workoutPlan = workoutPlanRepository.findByMemberIdAndTrainerId(memberId, trainerId)
                .orElseThrow(() -> new ResourceNotFoundException("WorkoutPlan", "memberId and trainerId", memberId + " & " + trainerId));

        logger.info("Workout Plan (deleteByIds) deleted, ID: {}", id); // SLF4J logging
        workoutPlanRepository.delete(workoutPlan);
    }

    @Override
    public WorkoutPlan createWorkoutPlanWithStrategy(WorkoutPlanRequest workoutPlanRequest, WorkoutStrategy strategy) { // Changed signature
        String id = generateWorkoutPlanId(workoutPlanRequest.getMemberId(), workoutPlanRequest.getTrainerId());
        List<DailyWorkout> dailyWorkouts = strategy.generateRoutine();

        // Create the workout plan with the strategy-generated workouts
        WorkoutPlan workoutPlan = new WorkoutPlan(
                id,
                workoutPlanRequest.getMemberId(),
                workoutPlanRequest.getTrainerId(),
                workoutPlanRequest.getStartDate(),
                workoutPlanRequest.getEndDate(),
                dailyWorkouts // These come from the strategy
        );

        // Save the workout plan in the database
        WorkoutPlan savedPlan = workoutPlanRepository.insert(workoutPlan);
        logger.info("Workout Plan with strategy created, ID: {}", savedPlan.getId()); // SLF4J logging
        return savedPlan;
    }

    // Helper method for consistent ID generation
    private String generateWorkoutPlanId(String memberId, String trainerId) {
        return memberId + "_" + trainerId; // Changed to avoid ambiguity if memberId or trainerId contains numbers
    }
}
