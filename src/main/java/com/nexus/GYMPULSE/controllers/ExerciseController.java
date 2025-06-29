package com.nexus.GYMPULSE.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nexus.GYMPULSE.model.workoutplan.Exercise;
import com.nexus.GYMPULSE.requests.ExerciseRequest;
import com.nexus.GYMPULSE.service.interfaces.ExerciseService;

import jakarta.validation.Valid; // Import @Valid

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/Exercises") // Base URL for Exercise endpoints
public class ExerciseController {

    @Autowired
    private ExerciseService exerciseService; // Injecting the ExerciseService for business logic

    // Endpoint to retrieve all exercises
    @GetMapping()
    public ResponseEntity<List<Exercise>> getAllExercises() {
        return ResponseEntity.ok(exerciseService.allExercises());
    }

    // Endpoint to create a new exercise
    @PostMapping()
    public ResponseEntity<Exercise> createExercise(@Valid @RequestBody ExerciseRequest exerciseRequest) { // Added @Valid
        // Assuming service createExercise will be updated to take ExerciseRequest
        Exercise createdExercise = exerciseService.createExercise(exerciseRequest);
        return new ResponseEntity<>(createdExercise, HttpStatus.CREATED);
    }

    // Endpoint to retrieve a specific exercise by its ID
    @GetMapping("/{id}")
    public ResponseEntity<Exercise> getExerciseById(@PathVariable String id) {
        return ResponseEntity.ok(exerciseService.exerciseById(id)
                .orElseThrow(() -> new com.nexus.GYMPULSE.exception.ResourceNotFoundException("Exercise", "id", id)));
    }

    // Endpoint to update an existing exercise by its ID
    @PutMapping("/{id}")
    public ResponseEntity<Exercise> updateExercise(@PathVariable String id, @Valid @RequestBody ExerciseRequest exerciseRequest) { // Added @Valid
        Exercise updatedExercise = exerciseService.updateExercise(id, exerciseRequest); // Service already throws if not found
        return ResponseEntity.ok(updatedExercise);
    }

    // Endpoint to clone an existing exercise by its ID
    @PostMapping("/{id}/clone")
    public ResponseEntity<Exercise> cloneExercise(@PathVariable String id) {
        Exercise clonedExercise = exerciseService.cloneExercise(id); // Service already throws if not found
        return ResponseEntity.ok(clonedExercise);
    }

    // Endpoint to delete an exercise by its ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExercise(@PathVariable String id) {
        exerciseService.deleteExerciseById(id); // Delete the exercise
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Return no content response
    }
}
