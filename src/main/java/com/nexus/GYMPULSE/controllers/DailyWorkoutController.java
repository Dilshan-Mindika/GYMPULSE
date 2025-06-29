package com.nexus.GYMPULSE.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nexus.GYMPULSE.model.workoutplan.DailyWorkout;
// import com.nexus.GYMPULSE.model.workoutplan.Exercise; // No longer directly used in controller
import com.nexus.GYMPULSE.requests.DailyWorkoutRequest;
import com.nexus.GYMPULSE.service.interfaces.DailyWorkoutService;

import jakarta.validation.Valid; // Import @Valid

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/DailyWorkouts") // Base URL for DailyWorkout endpoints
public class DailyWorkoutController {

    @Autowired
    private DailyWorkoutService dailyWorkoutService; // Injecting the service to handle business logic

    // Endpoint to get all daily workouts
    @GetMapping
    public ResponseEntity<List<DailyWorkout>> getAllDailyWorkouts() {
        return ResponseEntity.ok(dailyWorkoutService.allDailyWorkouts());
    }

    // Endpoint to get a specific daily workout by ID
    @GetMapping("/{id}")
    public ResponseEntity<DailyWorkout> getDailyWorkoutById(@PathVariable String id) {
        return ResponseEntity.ok(dailyWorkoutService.dailyWorkoutById(id)
                .orElseThrow(() -> new com.nexus.GYMPULSE.exception.ResourceNotFoundException("DailyWorkout", "id", id)));
    }

    // Endpoint to get daily workout by day of the week
    @GetMapping("/day/{dayOfWeek}")
    public ResponseEntity<DailyWorkout> getDailyWorkoutByDayOfWeek(@PathVariable String dayOfWeek) {
        return ResponseEntity.ok(dailyWorkoutService.dailyWorkoutByDayOfWeek(dayOfWeek)
                .orElseThrow(() -> new com.nexus.GYMPULSE.exception.ResourceNotFoundException("DailyWorkout", "dayOfWeek", dayOfWeek)));
    }

    // Endpoint to create a new daily workout plan
    @PostMapping
    public ResponseEntity<DailyWorkout> createDailyWorkoutPlan(@Valid @RequestBody DailyWorkoutRequest dailyWorkoutRequest) { // Changed to DailyWorkoutRequest and @Valid
        // Assuming service createDailyWorkout will be updated to take DailyWorkoutRequest
        DailyWorkout createdWorkout = dailyWorkoutService.createDailyWorkout(dailyWorkoutRequest);
        return new ResponseEntity<>(createdWorkout, HttpStatus.CREATED);
    }

    // Endpoint to update an existing daily workout by ID
    @PutMapping("/{id}")
    public ResponseEntity<DailyWorkout> updateDailyWorkout(@PathVariable String id, @Valid @RequestBody DailyWorkoutRequest dailyWorkoutRequest) { // Added @Valid
        DailyWorkout updatedWorkout = dailyWorkoutService.updateDailyWorkout(id, dailyWorkoutRequest); // Service already throws if not found
        return ResponseEntity.ok(updatedWorkout);
    }

    // Endpoint to delete a daily workout by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDailyWorkout(@PathVariable String id) {
        dailyWorkoutService.deleteDailyWorkout(id); // Delete the workout
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Return no content response
    }
}
