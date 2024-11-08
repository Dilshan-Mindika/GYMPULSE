package com.nexus.GYMPULSE.service.implementations;

import com.nexus.GYMPULSE.model.equipment.Equipment;
import com.nexus.GYMPULSE.repositories.EquipmentRepository;
import com.nexus.GYMPULSE.service.interfaces.EquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EquipmentServiceImpl implements EquipmentService {

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Override
    public Equipment addEquipment(Equipment equipment) {
        return equipmentRepository.save(equipment); // Save new equipment
    }

    @Override
    public List<Equipment> getAllEquipment() {
        return equipmentRepository.findAll(); // Retrieve all equipment
    }

    @Override
    public Optional<Equipment> getEquipmentById(String id) {
        return equipmentRepository.findById(id); // Find equipment by ID
    }

    @Override
    public Equipment updateEquipment(String id, Equipment equipment) {
        // Check if equipment exists
        if (equipmentRepository.existsById(id)) {
            equipment.setId(id); // Set the ID to the existing one
            return equipmentRepository.save(equipment); // Update equipment
        }
        return null; // Return null if equipment doesn't exist
    }

    @Override
    public void deleteEquipment(String id) {
        equipmentRepository.deleteById(id); // Delete equipment by ID
    }
}
