package com.nexus.GYMPULSE.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nexus.GYMPULSE.model.workoutplan.WorkoutPlan;
import com.nexus.GYMPULSE.model.workoutplan.strategies.CardioStrategy;
import com.nexus.GYMPULSE.model.workoutplan.strategies.StrengthTrainingStrategy;
import com.nexus.GYMPULSE.model.workoutplan.strategies.WorkoutStrategy;
import com.nexus.GYMPULSE.requests.WorkoutPlanRequest;
import com.nexus.GYMPULSE.service.interfaces.WorkoutPlanService;

import jakarta.validation.Valid; // Import @Valid

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/WorkoutPlans") // Base URL for workout plan-related endpoints
public class WorkoutPlanController {

    @Autowired
    private WorkoutPlanService workoutPlanService; // Injecting the WorkoutPlanService to manage workout plan logic

    // Endpoint to retrieve all workout plans
    @GetMapping
    public ResponseEntity<List<WorkoutPlan>> getAllWorkoutPlans() {
        return ResponseEntity.ok(workoutPlanService.allWorkoutPlans());
    }

    // Endpoint to retrieve a specific workout plan by ID
    @GetMapping("/{id}")
    public ResponseEntity<WorkoutPlan> getWorkoutByIds(@PathVariable String id) { // Method name is getWorkoutByIds but path is /id
        return ResponseEntity.ok(workoutPlanService.findWorkoutPlanById(id)
                .orElseThrow(() -> new com.nexus.GYMPULSE.exception.ResourceNotFoundException("WorkoutPlan", "id", id)));
    }

    // Endpoint to retrieve workout plans by member ID
    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<WorkoutPlan>> getWorkoutPlansByMemberId(@PathVariable String memberId) {
        // This returns a list. If memberId is not found, it might return an empty list, which is fine.
        // No ResourceNotFoundException needed here unless a member must exist.
        return ResponseEntity.ok(workoutPlanService.findWorkoutPlansByMemberId(memberId));
    }

    // Endpoint to create a new workout plan
    @PostMapping
    public ResponseEntity<WorkoutPlan> createWorkoutPlan(@Valid @RequestBody WorkoutPlanRequest workoutPlanRequest) { // Added @Valid
        // Assuming service createWorkoutPlan will be updated
        WorkoutPlan createdPlan = workoutPlanService.createWorkoutPlan(workoutPlanRequest);
        return new ResponseEntity<>(createdPlan, HttpStatus.CREATED);
    }

    // Endpoint to update an existing workout plan by ID
    @PutMapping("/{id}")
    public ResponseEntity<WorkoutPlan> updateWorkoutPlan(@PathVariable String id, @Valid @RequestBody WorkoutPlanRequest workoutPlanRequest) { // Added @Valid
        // Service updateWorkoutPlan already throws ResourceNotFoundException if not found
        WorkoutPlan updatedPlan = workoutPlanService.updateWorkoutPlan(id, workoutPlanRequest);
        return ResponseEntity.ok(updatedPlan);
    }

    // Endpoint to delete a workout plan by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteByIds(@PathVariable String id) { // Method name is deleteByIds but path is /id
        // Service deleteById already throws ResourceNotFoundException if not found
        workoutPlanService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Endpoint to create a new workout plan with a specific strategy
    @PostMapping("/withStrategy")
    public ResponseEntity<WorkoutPlan> createWorkoutPlanWithStrategy(@Valid @RequestBody WorkoutPlanRequest workoutPlanRequest, @RequestParam String strategyType) { // Added @Valid
        WorkoutStrategy strategy;
        // Determine the workout strategy based on the request parameter
        switch (strategyType.toLowerCase()) {
            case "cardio":
                strategy = new CardioStrategy(); // Use cardio strategy
                break;
            case "strength":
                strategy = new StrengthTrainingStrategy(); // Use strength training strategy
                break;
            default:
                // Consider using a custom exception that maps to 400 Bad Request
                throw new com.nexus.GYMPULSE.exception.BadRequestException("Invalid strategy type: " + strategyType);
        }
        // Assuming service createWorkoutPlanWithStrategy will be updated
        WorkoutPlan createdPlan = workoutPlanService.createWorkoutPlanWithStrategy(workoutPlanRequest, strategy);
        return new ResponseEntity<>(createdPlan, HttpStatus.CREATED);
    }
}
