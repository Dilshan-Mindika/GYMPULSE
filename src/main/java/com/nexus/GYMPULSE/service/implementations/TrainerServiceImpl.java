package com.nexus.GYMPULSE.service.implementations;

import java.util.List;
// import java.util.NoSuchElementException; // Replaced with custom exception
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nexus.GYMPULSE.exception.MaxTrainerLimitReachedException; // Import custom exception
import com.nexus.GYMPULSE.exception.ResourceNotFoundException; // Import custom exception
import com.nexus.GYMPULSE.model.person.Trainer;
import com.nexus.GYMPULSE.repositories.TrainerRepository;
import com.nexus.GYMPULSE.requests.TrainerRequest;
import com.nexus.GYMPULSE.service.interfaces.TrainerService;
// import com.nexus.GYMPULSE.utils.GymLogger; // Will be removed

@Service
public class TrainerServiceImpl implements TrainerService {

    private static final Logger logger = LoggerFactory.getLogger(TrainerServiceImpl.class); // SLF4J Logger

    @Autowired
    private TrainerRepository trainerRepository;

    @Override
    public Trainer createTrainer(TrainerRequest trainerRequest) { // Changed signature
        // Generate a unique trainer ID
        String trainerId = generateNextTrainerId();
        if (trainerId != null) {
            Trainer trainer = new Trainer(
                    trainerId,
                    trainerRequest.getSpeciality(),
                    trainerRequest.getSalary(),
                    trainerRequest.getCertificationNumber(),
                    trainerRequest.getFullName(),
                    trainerRequest.getPhoneNumber(),
                    trainerRequest.getAddress(),
                    trainerRequest.getEmail()
            );
            trainerRepository.insert(trainer);
            logger.info("New Trainer created, Trainer ID: {}", trainerId); // SLF4J logging
            return trainer;
        } else {
            // Throw custom exception for trainer limit
            throw new MaxTrainerLimitReachedException("Trainer limit reached (max 9999). Cannot create more trainers.");
        }
    }

    @Override
    public List<Trainer> allTrainers() {
        // Retrieve all trainers from the repository
        return trainerRepository.findAll();
    }

    // Removed findTrainerById as it's redundant with trainerById
    // @Override
    // public Optional<Trainer> findTrainerById(String trainerId) {
    //     return trainerRepository.findByTrainerId(trainerId);
    // }

    @Override
    public Optional<Trainer> trainerById(String trainerId) {
        // Find a trainer by their trainer ID
        return trainerRepository.findByTrainerId(trainerId);
    }

    @Override
    public Trainer updateTrainer(String trainerId, TrainerRequest trainerRequest) {
        // Update an existing trainer's details
        Trainer trainer = trainerRepository.findByTrainerId(trainerId)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer", "trainerId", trainerId));

        trainer.setSpeciality(trainerRequest.getSpeciality());
        trainer.setSalary(trainerRequest.getSalary());
        trainer.setCertificationNumber(trainerRequest.getCertificationNumber());
        trainer.setFullName(trainerRequest.getFullName());
        trainer.setPhoneNumber(trainerRequest.getPhoneNumber());
        trainer.setAddress(trainerRequest.getAddress());
        trainer.setEmail(trainerRequest.getEmail());
        logger.info("Trainer updated, Trainer ID: {}", trainerId); // SLF4J logging
        return trainerRepository.save(trainer);
    }

    @Override
    public void deleteByTrainerId(String trainerId) {
        // Delete a trainer by their trainer ID
        Trainer trainer = trainerRepository.findByTrainerId(trainerId)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer", "trainerId", trainerId));

        logger.info("Trainer deleted, ID: {}", trainerId); // SLF4J logging
        trainerRepository.delete(trainer);
    }

    private String generateNextTrainerId() {
        // Generate the next unique trainer ID.
        // Current implementation iterates from 1 to 9999.
        // LIMITATIONS: (Similar to generateNextMemberId)
        // 1. Performance: Can be inefficient with many trainers.
        // 2. Scalability: Hardcoded limit of 9999 trainers.
        // 3. Concurrency: Not safe for concurrent requests.
        // Consider alternatives like UUIDs or database sequences if the ID format is flexible.
        // Generate the next unique trainer ID
        List<Trainer> trainers = allTrainers();
        Set<String> usedIds = trainers.stream().map(Trainer::getTrainerId).collect(Collectors.toSet());

        for (int i = 1; i <= 9999; i++) {
            String candidateId = String.format("%04d", i);
            if (!usedIds.contains(candidateId)) {
                return candidateId; // Return the first unused ID
            }
        }
        return null; // No available ID found
    }
}
