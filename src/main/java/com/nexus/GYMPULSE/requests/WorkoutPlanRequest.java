package com.nexus.GYMPULSE.requests;

import java.util.List;

import com.nexus.GYMPULSE.model.workoutplan.DailyWorkout;

import lombok.Data;

@Data
public class WorkoutPlanRequest {
    private String id;
    private String memberId;
    private String trainerId;
    private String startDate;
    private String endDate;
    private List<DailyWorkout> dailyWorkouts;
}
