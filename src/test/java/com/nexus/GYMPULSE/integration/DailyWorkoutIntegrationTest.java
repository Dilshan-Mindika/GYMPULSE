package com.nexus.GYMPULSE.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexus.GYMPULSE.model.workoutplan.DailyWorkout;
import com.nexus.GYMPULSE.model.workoutplan.Exercise;
import com.nexus.GYMPULSE.repositories.DailyWorkoutRepository;
import com.nexus.GYMPULSE.requests.DailyWorkoutRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DailyWorkoutIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DailyWorkoutRepository dailyWorkoutRepository;

    private DailyWorkoutRequest validDailyWorkoutRequest;
    private Exercise sampleExercise;

    @BeforeEach
    void setUp() {
        dailyWorkoutRepository.deleteAll();

        sampleExercise = new Exercise("INT_E001", "Integration Squats", 3, 5, 120);
        // Note: For DailyWorkout tests, Exercise objects within DailyWorkouts are often embedded.
        // If Exercises were references (DBRefs), they would need to be saved separately first.
        // Assuming embedded for this test structure based on typical model.

        validDailyWorkoutRequest = new DailyWorkoutRequest();
        validDailyWorkoutRequest.setDayOfWeek("WEDNESDAY");
        validDailyWorkoutRequest.setExercises(Collections.singletonList(sampleExercise));
    }

    @AfterEach
    void tearDown() {
        dailyWorkoutRepository.deleteAll();
    }

    @Test
    void createDailyWorkout_withValidData_shouldReturnCreated() throws Exception {
        mockMvc.perform(post("/DailyWorkouts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDailyWorkoutRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.dayOfWeek", is("WEDNESDAY")))
                .andExpect(jsonPath("$.exercises", hasSize(1)))
                .andExpect(jsonPath("$.exercises[0].name", is("Integration Squats")))
                .andExpect(jsonPath("$.id", notNullValue()));
    }

    @Test
    void createDailyWorkout_withInvalidData_shouldReturnBadRequest() throws Exception {
        DailyWorkoutRequest invalidRequest = new DailyWorkoutRequest();
        invalidRequest.setDayOfWeek("INVALID_DAY"); // Invalid day
        invalidRequest.setExercises(Collections.emptyList()); // Empty exercises list

        mockMvc.perform(post("/DailyWorkouts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasItem("Day of the week must be a valid day (e.g., MONDAY, TUESDAY)")))
                .andExpect(jsonPath("$.errors", hasItem("Exercises list cannot be empty")));
    }

    @Test
    void getDailyWorkoutById_whenWorkoutExists_shouldReturnWorkout() throws Exception {
        DailyWorkout workout = new DailyWorkout(null, "FRIDAY", Collections.singletonList(sampleExercise));
        DailyWorkout savedWorkout = dailyWorkoutRepository.save(workout);

        mockMvc.perform(get("/DailyWorkouts/" + savedWorkout.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dayOfWeek", is("FRIDAY")))
                .andExpect(jsonPath("$.id", is(savedWorkout.getId())));
    }

    @Test
    void getDailyWorkoutById_whenWorkoutDoesNotExist_shouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/DailyWorkouts/nonExistentDWId"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getDailyWorkoutByDayOfWeek_whenWorkoutExists_shouldReturnWorkout() throws Exception {
        DailyWorkout workout = new DailyWorkout(null, "SATURDAY", Collections.singletonList(sampleExercise));
        dailyWorkoutRepository.save(workout);

        mockMvc.perform(get("/DailyWorkouts/day/SATURDAY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dayOfWeek", is("SATURDAY")));
    }

    @Test
    void getDailyWorkoutByDayOfWeek_whenWorkoutDoesNotExist_shouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/DailyWorkouts/day/SUNDAY"))
                .andExpect(status().isNotFound());
    }


    @Test
    void updateDailyWorkout_whenWorkoutExistsAndDataIsValid_shouldReturnOk() throws Exception {
        DailyWorkout existingWorkout = new DailyWorkout(null, "MONDAY", Collections.singletonList(sampleExercise));
        existingWorkout = dailyWorkoutRepository.save(existingWorkout);

        validDailyWorkoutRequest.setDayOfWeek("MONDAY_UPDATED");
        Exercise updatedExercise = new Exercise("INT_E002", "Updated Lunges", 3, 12, 60);
        validDailyWorkoutRequest.setExercises(Collections.singletonList(updatedExercise));


        mockMvc.perform(put("/DailyWorkouts/" + existingWorkout.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDailyWorkoutRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dayOfWeek", is("MONDAY_UPDATED")))
                .andExpect(jsonPath("$.exercises[0].name", is("Updated Lunges")))
                .andExpect(jsonPath("$.id", is(existingWorkout.getId())));

        Optional<DailyWorkout> updatedInDb = dailyWorkoutRepository.findById(existingWorkout.getId());
        assertTrue(updatedInDb.isPresent());
        assertEquals("MONDAY_UPDATED", updatedInDb.get().getDayOfWeek());
        assertEquals("Updated Lunges", updatedInDb.get().getExercises().get(0).getName());
    }


    @Test
    void deleteDailyWorkout_whenWorkoutExists_shouldReturnNoContent() throws Exception {
        DailyWorkout workoutToDelete = new DailyWorkout(null, "THURSDAY", Collections.singletonList(sampleExercise));
        workoutToDelete = dailyWorkoutRepository.save(workoutToDelete);

        mockMvc.perform(delete("/DailyWorkouts/" + workoutToDelete.getId()))
                .andExpect(status().isNoContent());

        assertTrue(dailyWorkoutRepository.findById(workoutToDelete.getId()).isEmpty());
    }
}
