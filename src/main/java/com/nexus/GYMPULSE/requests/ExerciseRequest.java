package com.nexus.GYMPULSE.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

// Request object for creating or updating an Exercise
@Data
public class ExerciseRequest {

    @NotBlank(message = "Exercise name cannot be blank")
    @Size(min = 2, max = 100, message = "Exercise name must be between 2 and 100 characters")
    private String name; // Name of the exercise

    @NotNull(message = "Quantity of sets cannot be null")
    @Min(value = 1, message = "Quantity of sets must be at least 1")
    private Integer quantitySets; // Number of sets for the exercise

    @NotNull(message = "Quantity of reps cannot be null")
    @Min(value = 1, message = "Quantity of reps must be at least 1")
    private Integer quantityReps; // Number of repetitions per set

    @NotNull(message = "Rest time cannot be null")
    @Min(value = 0, message = "Rest time cannot be negative") // 0 is acceptable if no rest for certain routines
    private Integer resTimeSeconds; // Rest time in seconds between sets
}
