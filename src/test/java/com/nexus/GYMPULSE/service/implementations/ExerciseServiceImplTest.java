package com.nexus.GYMPULSE.service.implementations;

import com.nexus.GYMPULSE.exception.ResourceNotFoundException;
import com.nexus.GYMPULSE.model.workoutplan.Exercise;
import com.nexus.GYMPULSE.repositories.ExerciseRepository;
import com.nexus.GYMPULSE.requests.ExerciseRequest;
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
class ExerciseServiceImplTest {

    @Mock
    private ExerciseRepository exerciseRepository;

    @InjectMocks
    private ExerciseServiceImpl exerciseService;

    private Exercise exercise1;
    private ExerciseRequest exerciseRequest;

    @BeforeEach
    void setUp() {
        // Note: Exercise ID is typically String (MongoDB ObjectId)
        exercise1 = new Exercise("E001", "Bench Press", 4, 8, 90);

        exerciseRequest = new ExerciseRequest();
        exerciseRequest.setName("Squats");
        exerciseRequest.setQuantitySets(3);
        exerciseRequest.setQuantityReps(10);
        exerciseRequest.setResTimeSeconds(120);
    }

    @Test
    void createExercise_success() {
        // Mock the save operation to return the exercise with a generated ID
        when(exerciseRepository.insert(any(Exercise.class))).thenAnswer(invocation -> {
            Exercise ex = invocation.getArgument(0);
            if (ex.getId() == null) { // Simulate ID generation by MongoDB if not set by service
                ex.setId("genExID");
            }
            return ex;
        });

        Exercise createdExercise = exerciseService.createExercise(exerciseRequest);

        assertNotNull(createdExercise);
        assertEquals("Squats", createdExercise.getName());
        assertNotNull(createdExercise.getId());
        verify(exerciseRepository, times(1)).insert(any(Exercise.class));
    }

    @Test
    void allExercises_success() {
        Exercise exercise2 = new Exercise("E002", "Deadlift", 3, 5, 180);
        when(exerciseRepository.findAll()).thenReturn(Arrays.asList(exercise1, exercise2));

        List<Exercise> exercises = exerciseService.allExercises();

        assertEquals(2, exercises.size());
        verify(exerciseRepository, times(1)).findAll();
    }

    @Test
    void exerciseById_found() {
        when(exerciseRepository.findById("E001")).thenReturn(Optional.of(exercise1));
        Optional<Exercise> foundExercise = exerciseService.exerciseById("E001");
        assertTrue(foundExercise.isPresent());
        assertEquals("Bench Press", foundExercise.get().getName());
    }

    @Test
    void exerciseById_notFound() {
        when(exerciseRepository.findById("E999")).thenReturn(Optional.empty());
        Optional<Exercise> foundExercise = exerciseService.exerciseById("E999");
        assertFalse(foundExercise.isPresent());
    }

    @Test
    void updateExercise_success() {
        when(exerciseRepository.findById("E001")).thenReturn(Optional.of(exercise1));
        when(exerciseRepository.save(any(Exercise.class))).thenAnswer(invocation -> invocation.getArgument(0));

        exerciseRequest.setName("Incline Bench Press");
        Exercise updatedExercise = exerciseService.updateExercise("E001", exerciseRequest);

        assertNotNull(updatedExercise);
        assertEquals("Incline Bench Press", updatedExercise.getName());
        assertEquals("E001", updatedExercise.getId());
        verify(exerciseRepository, times(1)).save(any(Exercise.class));
    }

    @Test
    void updateExercise_notFound() {
        when(exerciseRepository.findById("E999")).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            exerciseService.updateExercise("E999", exerciseRequest);
        });
        assertEquals("Exercise not found with id : 'E999'", exception.getMessage());
    }

    @Test
    void cloneExercise_success() {
        Exercise originalExercise = new Exercise("E001", "Original Exercise", 3, 10, 60);
        Exercise clonedExerciseMock = new Exercise(null, "Original Exercise", 3, 10, 60); // Clone will have null ID initially
        Exercise savedClonedExerciseMock = new Exercise("E002_clone", "Original Exercise", 3, 10, 60);


        when(exerciseRepository.findById("E001")).thenReturn(Optional.of(originalExercise));
        // Mock the save of the cloned exercise to return it with a new ID
        when(exerciseRepository.save(any(Exercise.class))).thenReturn(savedClonedExerciseMock);


        Exercise result = exerciseService.cloneExercise("E001");

        assertNotNull(result);
        assertEquals(originalExercise.getName(), result.getName());
        assertNotEquals(originalExercise.getId(), result.getId()); // ID should be different
        assertEquals("E002_clone", result.getId()); // Ensure it's the saved one with new ID
        verify(exerciseRepository, times(1)).findById("E001");
        verify(exerciseRepository, times(1)).save(any(Exercise.class)); // Verifying the cloned instance is saved
    }


    @Test
    void cloneExercise_notFound() {
        when(exerciseRepository.findById("E999")).thenReturn(Optional.empty());
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            exerciseService.cloneExercise("E999");
        });
        assertEquals("Exercise not found with id : 'E999'", exception.getMessage());
    }

    @Test
    void deleteExerciseById_success() {
        when(exerciseRepository.findById("E001")).thenReturn(Optional.of(exercise1)); // findById to get the entity
        doNothing().when(exerciseRepository).delete(exercise1); // delete the entity

        exerciseService.deleteExerciseById("E001");
        verify(exerciseRepository, times(1)).delete(exercise1);
    }

    @Test
    void deleteExerciseById_notFound() {
        when(exerciseRepository.findById("E999")).thenReturn(Optional.empty());
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            exerciseService.deleteExerciseById("E999");
        });
        assertEquals("Exercise not found with id : 'E999'", exception.getMessage());
    }
}
