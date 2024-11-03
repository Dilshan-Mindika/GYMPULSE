package com.nexus.GYMPULSE.repositories;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.nexus.GYMPULSE.model.person.Trainer;

@Repository
public interface TrainerRepository extends MongoRepository<Trainer, ObjectId>{
    Optional<Trainer> findByTrainerId(String trainerId);
}
