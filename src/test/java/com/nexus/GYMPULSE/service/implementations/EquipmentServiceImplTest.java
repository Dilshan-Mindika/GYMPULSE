package com.nexus.GYMPULSE.service.implementations;

import com.nexus.GYMPULSE.exception.ResourceNotFoundException;
import com.nexus.GYMPULSE.model.equipment.Equipment;
import com.nexus.GYMPULSE.repositories.EquipmentRepository;
import com.nexus.GYMPULSE.requests.EquipmentRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EquipmentServiceImplTest {

    @Mock
    private EquipmentRepository equipmentRepository;

    @InjectMocks
    private EquipmentServiceImpl equipmentService;

    private Equipment equipment1;
    private EquipmentRequest equipmentRequest;

    @BeforeEach
    void setUp() {
        equipment1 = new Equipment("EQ001", "Treadmill", "Cardio", "LifeFitness", 5, true);

        equipmentRequest = new EquipmentRequest();
        equipmentRequest.setName("Dumbbell Set");
        equipmentRequest.setType("Strength");
        equipmentRequest.setBrand("Rogue");
        equipmentRequest.setQuantity(10);
        equipmentRequest.setAvailable(true);
        // ID is not set in request for addEquipment
    }

    @Test
    void addEquipment_success() {
        // Mock the save operation to return the equipment with a generated ID
        when(equipmentRepository.save(any(Equipment.class))).thenAnswer(invocation -> {
            Equipment eq = invocation.getArgument(0);
            if (eq.getId() == null) { // Simulate ID generation if not present
                eq.setId("genEQID");
            }
            return eq;
        });

        Equipment createdEquipment = equipmentService.addEquipment(equipmentRequest);

        assertNotNull(createdEquipment);
        assertEquals("Dumbbell Set", createdEquipment.getName());
        assertNotNull(createdEquipment.getId()); // Check that an ID was set/generated
        verify(equipmentRepository, times(1)).save(any(Equipment.class));
    }

    @Test
    void getAllEquipment_success() {
        Equipment equipment2 = new Equipment("EQ002", "Elliptical", "Cardio", "Precor", 3, true);
        when(equipmentRepository.findAll()).thenReturn(Arrays.asList(equipment1, equipment2));

        List<Equipment> equipmentList = equipmentService.getAllEquipment();

        assertEquals(2, equipmentList.size());
        verify(equipmentRepository, times(1)).findAll();
    }

    @Test
    void getEquipmentById_found() {
        when(equipmentRepository.findById("EQ001")).thenReturn(Optional.of(equipment1));

        Optional<Equipment> foundEquipment = equipmentService.getEquipmentById("EQ001");

        assertTrue(foundEquipment.isPresent());
        assertEquals("Treadmill", foundEquipment.get().getName());
    }

    @Test
    void getEquipmentById_notFound() {
        when(equipmentRepository.findById("EQ999")).thenReturn(Optional.empty());

        Optional<Equipment> foundEquipment = equipmentService.getEquipmentById("EQ999");

        assertFalse(foundEquipment.isPresent());
    }

    @Test
    void updateEquipment_success() {
        when(equipmentRepository.findById("EQ001")).thenReturn(Optional.of(equipment1));
        // Make sure the save mock returns the updated object
        when(equipmentRepository.save(any(Equipment.class))).thenAnswer(invocation -> invocation.getArgument(0));


        equipmentRequest.setName("Advanced Treadmill");
        Equipment updatedEquipment = equipmentService.updateEquipment("EQ001", equipmentRequest);

        assertNotNull(updatedEquipment);
        assertEquals("Advanced Treadmill", updatedEquipment.getName());
        assertEquals("EQ001", updatedEquipment.getId()); // Ensure ID remains the same
        verify(equipmentRepository, times(1)).save(any(Equipment.class));
    }

    @Test
    void updateEquipment_notFound() {
        when(equipmentRepository.findById("EQ999")).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            equipmentService.updateEquipment("EQ999", equipmentRequest);
        });

        assertEquals("Equipment not found with id : 'EQ999'", exception.getMessage());
        verify(equipmentRepository, never()).save(any(Equipment.class));
    }

    @Test
    void deleteEquipment_success() {
        when(equipmentRepository.existsById("EQ001")).thenReturn(true);
        doNothing().when(equipmentRepository).deleteById("EQ001");

        equipmentService.deleteEquipment("EQ001");

        verify(equipmentRepository, times(1)).deleteById("EQ001");
    }

    @Test
    void deleteEquipment_notFound() {
        when(equipmentRepository.existsById("EQ999")).thenReturn(false);

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            equipmentService.deleteEquipment("EQ999");
        });

        assertEquals("Equipment not found with id : 'EQ999'", exception.getMessage());
        verify(equipmentRepository, never()).deleteById(anyString());
    }
}
