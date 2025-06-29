package com.nexus.GYMPULSE.controllers;

import com.nexus.GYMPULSE.model.equipment.Equipment;
import com.nexus.GYMPULSE.requests.EquipmentRequest; // Import EquipmentRequest
import com.nexus.GYMPULSE.service.interfaces.EquipmentService;
import jakarta.validation.Valid; // Import @Valid
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/equipment") // Base URL for equipment-related endpoints
public class EquipmentController {

    @Autowired
    private EquipmentService equipmentService;

    @PostMapping
    public ResponseEntity<Equipment> addEquipment(@Valid @RequestBody EquipmentRequest equipmentRequest) {
        // Assuming EquipmentService.addEquipment will be updated to take EquipmentRequest
        // or we map it here. For now, let's assume service update.
        Equipment newEquipment = equipmentService.addEquipment(equipmentRequest);
        return new ResponseEntity<>(newEquipment, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Equipment>> getAllEquipment() {
        List<Equipment> equipmentList = equipmentService.getAllEquipment();
        return ResponseEntity.ok(equipmentList); // Return list of equipment
    }

    @GetMapping("/{id}")
    public ResponseEntity<Equipment> getEquipmentById(@PathVariable String id) {
        // Service method getEquipmentById already returns Optional<Equipment>
        // The service should throw ResourceNotFoundException if not found,
        // which will be handled by GlobalExceptionHandler or @ResponseStatus.
        // Controller should then just return the object.
        return ResponseEntity.ok(equipmentService.getEquipmentById(id)
                .orElseThrow(() -> new com.nexus.GYMPULSE.exception.ResourceNotFoundException("Equipment", "id", id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Equipment> updateEquipment(@PathVariable String id, @Valid @RequestBody EquipmentRequest equipmentRequest) {
        // Assuming EquipmentService.updateEquipment will be updated
        Equipment updatedEquipment = equipmentService.updateEquipment(id, equipmentRequest);
        return ResponseEntity.ok(updatedEquipment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEquipment(@PathVariable String id) {
        equipmentService.deleteEquipment(id); // This service method should throw if not found
        return ResponseEntity.noContent().build(); // Return 204 No Content
    }
}
