package com.nexus.GYMPULSE.requests;

import java.util.List;

import com.nexus.GYMPULSE.model.workoutplan.DailyWorkout;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

// Request object for creating or updating a Workout Plan
@Data
public class WorkoutPlanRequest {
    // ID is usually system-generated (e.g., memberId + trainerId in current service), not validated on input for creation.
    private String id; // Unique identifier for the workout plan

    @NotBlank(message = "Member ID cannot be blank")
    private String memberId; // ID of the member associated with the workout plan

    @NotBlank(message = "Trainer ID cannot be blank")
    private String trainerId; // ID of the trainer overseeing the workout plan

    @NotBlank(message = "Start date cannot be blank")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Start date must be in YYYY-MM-DD format")
    private String startDate; // Start date of the workout plan

    @NotBlank(message = "End date cannot be blank")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "End date must be in YYYY-MM-DD format")
    // Consider adding custom validation to ensure endDate is after startDate
    private String endDate; // End date of the workout plan

    @NotEmpty(message = "Daily workouts list cannot be empty")
    @Size(min = 1, message = "At least one daily workout must be included")
    private List<@Valid DailyWorkout> dailyWorkouts; // List of daily workouts included in the plan. @Valid for nested validation.
}
