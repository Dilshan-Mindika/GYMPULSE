package com.nexus.GYMPULSE.service.implementations;

import com.nexus.GYMPULSE.exception.ResourceNotFoundException;
import com.nexus.GYMPULSE.model.workoutplan.DailyWorkout;
import com.nexus.GYMPULSE.model.workoutplan.Exercise;
import com.nexus.GYMPULSE.repositories.DailyWorkoutRepository;
import com.nexus.GYMPULSE.requests.DailyWorkoutRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DailyWorkoutImplTest {

    @Mock
    private DailyWorkoutRepository dailyWorkoutRepository;

    @InjectMocks
    private DailyWorkoutImpl dailyWorkoutService; // Service implementation

    private DailyWorkout dailyWorkout1;
    private DailyWorkoutRequest dailyWorkoutRequest;
    private Exercise exercise1;

    @BeforeEach
    void setUp() {
        exercise1 = new Exercise("E001", "Chest Press", 3, 10, 60);
        List<Exercise> exercises = Collections.singletonList(exercise1);

        // ID for DailyWorkout is String (MongoDB ObjectId)
        dailyWorkout1 = new DailyWorkout("DW001", "MONDAY", exercises);

        dailyWorkoutRequest = new DailyWorkoutRequest();
        dailyWorkoutRequest.setDayOfWeek("TUESDAY");
        dailyWorkoutRequest.setExercises(exercises);
        // dailyWorkoutRequest.setId("someId"); // ID is not set for creation via request typically
    }

    @Test
    void createDailyWorkout_success() {
        when(dailyWorkoutRepository.insert(any(DailyWorkout.class))).thenAnswer(invocation -> {
            DailyWorkout dw = invocation.getArgument(0);
            if (dw.getId() == null) {
                dw.setId("genDWID"); // Simulate ID generation
            }
            return dw;
        });

        DailyWorkout createdDailyWorkout = dailyWorkoutService.createDailyWorkout(dailyWorkoutRequest);

        assertNotNull(createdDailyWorkout);
        assertEquals("TUESDAY", createdDailyWorkout.getDayOfWeek());
        assertNotNull(createdDailyWorkout.getId());
        assertFalse(createdDailyWorkout.getExercises().isEmpty());
        verify(dailyWorkoutRepository, times(1)).insert(any(DailyWorkout.class));
    }

    @Test
    void allDailyWorkouts_success() {
        DailyWorkout dailyWorkout2 = new DailyWorkout("DW002", "WEDNESDAY", Collections.emptyList());
        when(dailyWorkoutRepository.findAll()).thenReturn(Arrays.asList(dailyWorkout1, dailyWorkout2));

        List<DailyWorkout> workouts = dailyWorkoutService.allDailyWorkouts();

        assertEquals(2, workouts.size());
        verify(dailyWorkoutRepository, times(1)).findAll();
    }

    @Test
    void dailyWorkoutById_found() {
        when(dailyWorkoutRepository.findDailyWorkoutById("DW001")).thenReturn(Optional.of(dailyWorkout1));
        Optional<DailyWorkout> foundWorkout = dailyWorkoutService.dailyWorkoutById("DW001");
        assertTrue(foundWorkout.isPresent());
        assertEquals("MONDAY", foundWorkout.get().getDayOfWeek());
    }

    @Test
    void dailyWorkoutById_notFound() {
        when(dailyWorkoutRepository.findDailyWorkoutById("DW999")).thenReturn(Optional.empty());
        Optional<DailyWorkout> foundWorkout = dailyWorkoutService.dailyWorkoutById("DW999");
        assertFalse(foundWorkout.isPresent());
    }

    @Test
    void dailyWorkoutByDayOfWeek_found() {
        when(dailyWorkoutRepository.findDailyWorkoutByDayOfWeek("MONDAY")).thenReturn(Optional.of(dailyWorkout1));
        Optional<DailyWorkout> foundWorkout = dailyWorkoutService.dailyWorkoutByDayOfWeek("MONDAY");
        assertTrue(foundWorkout.isPresent());
        assertEquals("DW001", foundWorkout.get().getId());
    }

    @Test
    void updateDailyWorkout_success() {
        when(dailyWorkoutRepository.findDailyWorkoutById("DW001")).thenReturn(Optional.of(dailyWorkout1));
        when(dailyWorkoutRepository.save(any(DailyWorkout.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Exercise newExercise = new Exercise("E002", "Overhead Press", 3, 8, 75);
        dailyWorkoutRequest.setExercises(Collections.singletonList(newExercise));
        dailyWorkoutRequest.setDayOfWeek("MONDAY_UPDATED");

        DailyWorkout updatedWorkout = dailyWorkoutService.updateDailyWorkout("DW001", dailyWorkoutRequest);

        assertNotNull(updatedWorkout);
        assertEquals("MONDAY_UPDATED", updatedWorkout.getDayOfWeek());
        assertEquals(1, updatedWorkout.getExercises().size());
        assertEquals("Overhead Press", updatedWorkout.getExercises().get(0).getName());
        verify(dailyWorkoutRepository, times(1)).save(any(DailyWorkout.class));
    }

    @Test
    void updateDailyWorkout_notFound() {
        when(dailyWorkoutRepository.findDailyWorkoutById("DW999")).thenReturn(Optional.empty());
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            dailyWorkoutService.updateDailyWorkout("DW999", dailyWorkoutRequest);
        });
        assertEquals("DailyWorkout not found with id : 'DW999'", exception.getMessage());
    }

    @Test
    void deleteDailyWorkout_success() {
        when(dailyWorkoutRepository.findDailyWorkoutById("DW001")).thenReturn(Optional.of(dailyWorkout1));
        doNothing().when(dailyWorkoutRepository).delete(dailyWorkout1);

        dailyWorkoutService.deleteDailyWorkout("DW001");
        verify(dailyWorkoutRepository, times(1)).delete(dailyWorkout1);
    }

    @Test
    void deleteDailyWorkout_notFound() {
        when(dailyWorkoutRepository.findDailyWorkoutById("DW999")).thenReturn(Optional.empty());
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            dailyWorkoutService.deleteDailyWorkout("DW999");
        });
        assertEquals("DailyWorkout not found with id : 'DW999'", exception.getMessage());
    }
}
