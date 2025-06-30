package com.nexus.GYMPULSE.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexus.GYMPULSE.exception.GlobalExceptionHandler;
import com.nexus.GYMPULSE.exception.ResourceNotFoundException;
import com.nexus.GYMPULSE.model.workoutplan.Exercise;
import com.nexus.GYMPULSE.requests.ExerciseRequest;
import com.nexus.GYMPULSE.service.interfaces.ExerciseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

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
class ExerciseControllerTest {

    @Mock
    private ExerciseService exerciseService;

    @InjectMocks
    private ExerciseController exerciseController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private Exercise exercise1;
    private ExerciseRequest exerciseRequest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(exerciseController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();

        exercise1 = new Exercise("E001", "Push-ups", 3, 15, 60);

        exerciseRequest = new ExerciseRequest();
        exerciseRequest.setName("Push-ups");
        exerciseRequest.setQuantitySets(3);
        exerciseRequest.setQuantityReps(15);
        exerciseRequest.setResTimeSeconds(60);
    }

    @Test
    void getAllExercises_shouldReturnListOfExercises() throws Exception {
        List<Exercise> allExercises = Arrays.asList(exercise1);
        when(exerciseService.allExercises()).thenReturn(allExercises);

        mockMvc.perform(get("/Exercises"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Push-ups")));
    }

    @Test
    void createExercise_shouldReturnCreatedExercise() throws Exception {
        when(exerciseService.createExercise(any(ExerciseRequest.class))).thenReturn(exercise1);

        mockMvc.perform(post("/Exercises")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(exerciseRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Push-ups")));
    }

    @Test
    void getExerciseById_shouldReturnExerciseWhenFound() throws Exception {
        when(exerciseService.exerciseById("E001")).thenReturn(Optional.of(exercise1));

        mockMvc.perform(get("/Exercises/E001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Push-ups")));
    }

    @Test
    void getExerciseById_shouldReturnNotFoundWhenDoesNotExist() throws Exception {
        when(exerciseService.exerciseById("E999")).thenReturn(Optional.empty());

        mockMvc.perform(get("/Exercises/E999"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException));
    }

    @Test
    void updateExercise_shouldReturnUpdatedExercise() throws Exception {
        when(exerciseService.updateExercise(eq("E001"), any(ExerciseRequest.class))).thenReturn(exercise1);

        mockMvc.perform(put("/Exercises/E001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(exerciseRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Push-ups")));
    }

    @Test
    void updateExercise_shouldReturnNotFoundWhenDoesNotExist() throws Exception {
        when(exerciseService.updateExercise(eq("E999"), any(ExerciseRequest.class)))
            .thenThrow(new ResourceNotFoundException("Exercise", "id", "E999"));

        mockMvc.perform(put("/Exercises/E999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(exerciseRequest)))
                .andExpect(status().isNotFound());
    }


    @Test
    void cloneExercise_shouldReturnClonedExercise() throws Exception {
        Exercise clonedExercise = new Exercise("E001_clone", "Push-ups", 3, 15, 60);
        when(exerciseService.cloneExercise("E001")).thenReturn(clonedExercise);

        mockMvc.perform(post("/Exercises/E001/clone"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("E001_clone")))
                .andExpect(jsonPath("$.name", is("Push-ups")));
    }

    @Test
    void cloneExercise_shouldReturnNotFoundWhenOriginalDoesNotExist() throws Exception {
        when(exerciseService.cloneExercise("E999"))
            .thenThrow(new ResourceNotFoundException("Exercise", "id", "E999"));

        mockMvc.perform(post("/Exercises/E999/clone"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteExercise_shouldReturnNoContent() throws Exception {
        doNothing().when(exerciseService).deleteExerciseById("E001");

        mockMvc.perform(delete("/Exercises/E001"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteExercise_shouldReturnNotFoundWhenDoesNotExist() throws Exception {
        doThrow(new ResourceNotFoundException("Exercise", "id", "E999"))
            .when(exerciseService).deleteExerciseById("E999");

        mockMvc.perform(delete("/Exercises/E999"))
                .andExpect(status().isNotFound());
    }
}
