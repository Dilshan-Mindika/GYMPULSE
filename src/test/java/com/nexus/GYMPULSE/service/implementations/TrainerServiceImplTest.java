package com.nexus.GYMPULSE.service.implementations;

import com.nexus.GYMPULSE.exception.MaxTrainerLimitReachedException;
import com.nexus.GYMPULSE.exception.ResourceNotFoundException;
import com.nexus.GYMPULSE.model.person.Trainer;
import com.nexus.GYMPULSE.repositories.TrainerRepository;
import com.nexus.GYMPULSE.requests.TrainerRequest;
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
class TrainerServiceImplTest {

    @Mock
    private TrainerRepository trainerRepository;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    private Trainer trainer1;
    private TrainerRequest trainerRequest;

    @BeforeEach
    void setUp() {
        trainer1 = new Trainer("T001", "Yoga", 50000.0, "CERT123", "Alice Wonderland", "1112223333", "1 Wonder Lane", "alice@example.com");

        trainerRequest = new TrainerRequest();
        trainerRequest.setFullName("Bob The Builder");
        trainerRequest.setEmail("bob.builder@example.com");
        trainerRequest.setPhoneNumber("4445556666");
        trainerRequest.setAddress("1 Build It Rd");
        trainerRequest.setSpeciality("Construction");
        trainerRequest.setSalary(60000.0);
        trainerRequest.setCertificationNumber("BUILD001");
    }

    @Test
    void createTrainer_success() {
        when(trainerRepository.findAll()).thenReturn(Collections.emptyList()); // No existing trainers
        when(trainerRepository.insert(any(Trainer.class))).thenAnswer(invocation -> {
            Trainer t = invocation.getArgument(0);
            // Simulate repository assigning the ID if it's part of the object before insert for some reason,
            // or just return the object as is if ID generation is purely internal to service.
            // The service's generateNextTrainerId should provide the ID.
            return t;
        });

        Trainer createdTrainer = trainerService.createTrainer(trainerRequest);

        assertNotNull(createdTrainer);
        assertEquals("Bob The Builder", createdTrainer.getFullName());
        assertEquals("0001", createdTrainer.getTrainerId()); // Expecting the first generated ID
        verify(trainerRepository, times(1)).insert(any(Trainer.class));
    }

    @Test
    void createTrainer_maxLimitReached() {
        List<Trainer> fullTrainerList = new java.util.ArrayList<>();
        for (int i = 1; i <= 9999; i++) {
            fullTrainerList.add(new Trainer(String.format("%04d", i), null, null, null, null, null, null, null));
        }
        when(trainerRepository.findAll()).thenReturn(fullTrainerList);

        Exception exception = assertThrows(MaxTrainerLimitReachedException.class, () -> {
            trainerService.createTrainer(trainerRequest);
        });

        assertEquals("Trainer limit reached (max 9999). Cannot create more trainers.", exception.getMessage());
        verify(trainerRepository, never()).insert(any(Trainer.class));
    }

    @Test
    void allTrainers_success() {
        Trainer trainer2 = new Trainer("T002", "Pilates", 52000.0, "CERT456", "Charlie Brown", "7778889999", "2 Snoopy St", "charlie@example.com");
        when(trainerRepository.findAll()).thenReturn(Arrays.asList(trainer1, trainer2));
        List<Trainer> trainers = trainerService.allTrainers();
        assertEquals(2, trainers.size());
        verify(trainerRepository, times(1)).findAll();
    }

    @Test
    void trainerById_found() {
        when(trainerRepository.findByTrainerId("T001")).thenReturn(Optional.of(trainer1));
        Optional<Trainer> foundTrainer = trainerService.trainerById("T001");
        assertTrue(foundTrainer.isPresent());
        assertEquals("Alice Wonderland", foundTrainer.get().getFullName());
    }

    @Test
    void trainerById_notFound() {
        when(trainerRepository.findByTrainerId("T999")).thenReturn(Optional.empty());
        Optional<Trainer> foundTrainer = trainerService.trainerById("T999");
        assertFalse(foundTrainer.isPresent());
    }

    @Test
    void updateTrainer_success() {
        when(trainerRepository.findByTrainerId("T001")).thenReturn(Optional.of(trainer1));
        when(trainerRepository.save(any(Trainer.class))).thenReturn(trainer1); // Return the modified trainer1

        trainerRequest.setFullName("Alice Updated Wonderland");
        Trainer updatedTrainer = trainerService.updateTrainer("T001", trainerRequest);

        assertNotNull(updatedTrainer);
        assertEquals("Alice Updated Wonderland", updatedTrainer.getFullName());
        verify(trainerRepository, times(1)).save(any(Trainer.class));
    }

    @Test
    void updateTrainer_notFound() {
        when(trainerRepository.findByTrainerId("T999")).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            trainerService.updateTrainer("T999", trainerRequest);
        });
        // Note: TrainerServiceImpl uses ResourceNotFoundException("Trainer", "trainerId", trainerId)
        assertEquals("Trainer not found with trainerId : 'T999'", exception.getMessage());
        verify(trainerRepository, never()).save(any(Trainer.class));
    }

    @Test
    void deleteByTrainerId_success() {
        when(trainerRepository.findByTrainerId("T001")).thenReturn(Optional.of(trainer1));
        doNothing().when(trainerRepository).delete(trainer1);

        trainerService.deleteByTrainerId("T001");

        verify(trainerRepository, times(1)).delete(trainer1);
    }

    @Test
    void deleteByTrainerId_notFound() {
        when(trainerRepository.findByTrainerId("T999")).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            trainerService.deleteByTrainerId("T999");
        });
        assertEquals("Trainer not found with trainerId : 'T999'", exception.getMessage());
        verify(trainerRepository, never()).delete(any(Trainer.class));
    }
}
