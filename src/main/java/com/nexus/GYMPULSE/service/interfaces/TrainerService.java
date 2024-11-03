package com.nexus.GYMPULSE.service.interfaces;

import com.nexus.GYMPULSE.model.person.Trainer;
import com.nexus.GYMPULSE.requests.TrainerRequest;

import java.util.List;
import java.util.Optional;

public interface TrainerService {
    Trainer createTrainer(String speciality, Double salary, String certificationNumber, String fullName, 
    String phoneNumber, String address, String email);
    List<Trainer> allTrainers();
    Optional<Trainer> trainerById(String trainerId);
    Trainer updateTrainer(String trainerId, TrainerRequest trainerRequest);
    void deleteByTrainerId(String trainerId);
}
