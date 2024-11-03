package com.nexus.GYMPULSE.repositories;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.nexus.GYMPULSE.model.workoutplan.WorkoutPlan;

@Repository
public interface WorkoutPlanRepository extends MongoRepository<WorkoutPlan, String> {
    Optional<WorkoutPlan> findByMemberIdAndTrainerId(String memberId, String trainerId);
}
