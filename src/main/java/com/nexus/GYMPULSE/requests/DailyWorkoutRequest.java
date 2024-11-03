package com.nexus.GYMPULSE.requests;

import java.util.List;

import com.nexus.GYMPULSE.model.workoutplan.Exercise;

import lombok.Data;

@Data
public class DailyWorkoutRequest {
    private String id;
    private String dayOfWeek;
    private List<Exercise> exercises;
}
