package com.nexus.GYMPULSE.repositories;

import com.nexus.GYMPULSE.model.equipment.Equipment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EquipmentRepository extends MongoRepository<Equipment, String> {
    // Custom query methods can be added here if needed
}
