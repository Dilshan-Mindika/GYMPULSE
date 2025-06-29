package com.nexus.GYMPULSE.service.interfaces;

import com.nexus.GYMPULSE.model.equipment.Equipment;
import com.nexus.GYMPULSE.requests.EquipmentRequest; // Import EquipmentRequest

import java.util.List;
import java.util.Optional;

public interface EquipmentService {
    Equipment addEquipment(EquipmentRequest equipmentRequest); // Changed to EquipmentRequest
    List<Equipment> getAllEquipment();
    Optional<Equipment> getEquipmentById(String id);
    Equipment updateEquipment(String id, EquipmentRequest equipmentRequest); // Changed to EquipmentRequest
    void deleteEquipment(String id);
}
