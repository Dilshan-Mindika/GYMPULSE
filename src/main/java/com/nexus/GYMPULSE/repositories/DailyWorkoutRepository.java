package com.nexus.GYMPULSE.repositories;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.nexus.GYMPULSE.model.workoutplan.DailyWorkout;

@Repository
public interface DailyWorkoutRepository extends MongoRepository<DailyWorkout, ObjectId> {
    Optional<DailyWorkout> findDailyWorkoutById(String id);
    Optional<DailyWorkout> findDailyWorkoutByDayOfWeek(String dayOfWeek);
}
