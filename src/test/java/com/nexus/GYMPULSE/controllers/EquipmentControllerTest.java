package com.nexus.GYMPULSE.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexus.GYMPULSE.exception.ResourceNotFoundException;
import com.nexus.GYMPULSE.model.equipment.Equipment;
import com.nexus.GYMPULSE.requests.EquipmentRequest;
import com.nexus.GYMPULSE.service.interfaces.EquipmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EquipmentControllerTest {

    @Mock
    private EquipmentService equipmentService;

    @InjectMocks
    private EquipmentController equipmentController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private Equipment equipment1;
    private EquipmentRequest equipmentRequest;

    @BeforeEach
    void setUp() {
        // Initialize GlobalExceptionHandler to handle ResourceNotFoundException for 404
        mockMvc = MockMvcBuilders.standaloneSetup(equipmentController)
                .setControllerAdvice(new com.nexus.GYMPULSE.exception.GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();

        equipment1 = new Equipment("EQ001", "Treadmill", "Cardio", "LifeFitness", 5, true);

        equipmentRequest = new EquipmentRequest();
        equipmentRequest.setName("Treadmill");
        equipmentRequest.setType("Cardio");
        equipmentRequest.setBrand("LifeFitness");
        equipmentRequest.setQuantity(5);
        equipmentRequest.setAvailable(true);
    }

    @Test
    void addEquipment_shouldReturnCreatedEquipment() throws Exception {
        when(equipmentService.addEquipment(any(EquipmentRequest.class))).thenReturn(equipment1);

        mockMvc.perform(post("/equipment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(equipmentRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Treadmill")));
    }

    @Test
    void getAllEquipment_shouldReturnListOfEquipment() throws Exception {
        List<Equipment> allEquipment = Arrays.asList(equipment1);
        when(equipmentService.getAllEquipment()).thenReturn(allEquipment);

        mockMvc.perform(get("/equipment"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Treadmill")));
    }

    @Test
    void getEquipmentById_shouldReturnEquipmentWhenFound() throws Exception {
        when(equipmentService.getEquipmentById("EQ001")).thenReturn(Optional.of(equipment1));

        mockMvc.perform(get("/equipment/EQ001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Treadmill")));
    }

    @Test
    void getEquipmentById_shouldReturnNotFoundWhenDoesNotExist() throws Exception {
        when(equipmentService.getEquipmentById("EQ999")).thenReturn(Optional.empty());
        // Controller's getEquipmentById now throws ResourceNotFoundException via .orElseThrow()

        mockMvc.perform(get("/equipment/EQ999"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andExpect(jsonPath("$.message", is("Equipment not found with id : 'EQ999'")));
    }


    @Test
    void updateEquipment_shouldReturnUpdatedEquipment() throws Exception {
        when(equipmentService.updateEquipment(eq("EQ001"), any(EquipmentRequest.class))).thenReturn(equipment1);

        mockMvc.perform(put("/equipment/EQ001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(equipmentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Treadmill")));
    }

    @Test
    void updateEquipment_shouldReturnNotFoundWhenDoesNotExist() throws Exception {
        when(equipmentService.updateEquipment(eq("EQ999"), any(EquipmentRequest.class)))
                .thenThrow(new ResourceNotFoundException("Equipment", "id", "EQ999"));

        mockMvc.perform(put("/equipment/EQ999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(equipmentRequest)))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andExpect(jsonPath("$.message", is("Equipment not found with id : 'EQ999'")));
    }


    @Test
    void deleteEquipment_shouldReturnNoContent() throws Exception {
        doNothing().when(equipmentService).deleteEquipment("EQ001");

        mockMvc.perform(delete("/equipment/EQ001"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteEquipment_shouldReturnNotFoundWhenDoesNotExist() throws Exception {
        // Mock service to throw ResourceNotFoundException when trying to delete non-existent equipment
        doThrow(new ResourceNotFoundException("Equipment", "id", "EQ999"))
                .when(equipmentService).deleteEquipment("EQ999");

        mockMvc.perform(delete("/equipment/EQ999"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andExpect(jsonPath("$.message", is("Equipment not found with id : 'EQ999'")));
    }

    // Test for @Valid (input validation)
    @Test
    void addEquipment_withInvalidData_shouldReturnBadRequest() throws Exception {
        EquipmentRequest invalidRequest = new EquipmentRequest();
        invalidRequest.setName(""); // Blank name
        invalidRequest.setQuantity(-1); // Negative quantity

        // No need to mock equipmentService.addEquipment for this validation test,
        // as the request should fail before hitting the service.
        // The GlobalExceptionHandler is needed to format the 400 response.

        mockMvc.perform(post("/equipment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(jsonPath("$.errors", hasItem("Equipment name cannot be blank")))
                .andExpect(jsonPath("$.errors", hasItem("Quantity cannot be negative")));
    }
}
