package com.nexus.GYMPULSE.repositories;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.nexus.GYMPULSE.model.workoutplan.Exercise;

@Repository
public interface ExerciseRepository extends MongoRepository<Exercise, ObjectId> {
    Optional<Exercise> findById(String id);
}
