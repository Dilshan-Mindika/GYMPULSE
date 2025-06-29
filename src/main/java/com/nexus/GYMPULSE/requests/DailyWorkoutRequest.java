package com.nexus.GYMPULSE.requests;

import java.util.List;

import com.nexus.GYMPULSE.model.workoutplan.Exercise;

import jakarta.validation.Valid; // For validating elements in the list
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

// Request object for creating or updating a DailyWorkout
@Data
public class DailyWorkoutRequest {
    // ID is usually system-generated, not validated on input for creation.
    private String id; // Unique identifier for the DailyWorkout

    @NotBlank(message = "Day of the week cannot be blank")
    @Pattern(regexp = "^(MONDAY|TUESDAY|WEDNESDAY|THURSDAY|FRIDAY|SATURDAY|SUNDAY)$",
             message = "Day of the week must be a valid day (e.g., MONDAY, TUESDAY)")
    private String dayOfWeek; // Day of the week for the workout

    @NotEmpty(message = "Exercises list cannot be empty")
    @Size(min = 1, message = "At least one exercise must be included")
    private List<@Valid Exercise> exercises; // List of exercises included in the workout. @Valid ensures nested validation if Exercise has constraints.
}
