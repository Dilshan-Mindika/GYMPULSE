package com.nexus.GYMPULSE.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nexus.GYMPULSE.model.person.Trainer;
import com.nexus.GYMPULSE.requests.TrainerRequest;
import com.nexus.GYMPULSE.service.interfaces.TrainerService;

@RestController
@RequestMapping("/Trainers")
public class TrainerController {
    @Autowired
    private TrainerService trainerService;

    @GetMapping
    public ResponseEntity<List<Trainer>> getAllTrainers() {
        return new ResponseEntity<List<Trainer>>(trainerService.allTrainers(), HttpStatus.OK);
    }
    @PostMapping
    public Trainer createTrainer(@RequestBody TrainerRequest trainerRequest) {
        String speciality = trainerRequest.getSpeciality();
        Double salary = trainerRequest.getSalary();
        String certificationNumber = trainerRequest.getCertificationNumber();
        String fullName = trainerRequest.getFullName();
        String phoneNumber = trainerRequest.getPhoneNumber();
        String address = trainerRequest.getAddress();
        String email = trainerRequest.getEmail();
        return trainerService.createTrainer(speciality, salary, certificationNumber, fullName, 
        phoneNumber, address, email);
    }
    @GetMapping("/{trainerId}")
    public ResponseEntity<Optional<Trainer>> getTrainerById(@PathVariable String trainerId) {
        return new ResponseEntity<Optional<Trainer>>(trainerService.trainerById(trainerId), HttpStatus.OK);
    }
    @PutMapping("/{trainerId}")
    public Trainer updateTrainer(@PathVariable String trainerId, @RequestBody TrainerRequest trainerRequest) {
        return trainerService.updateTrainer(trainerId, trainerRequest);
    }
    @DeleteMapping("/{trainerId}")
    public ResponseEntity<Void> deleteTrainer(@PathVariable String trainerId) {
        trainerService.deleteByTrainerId(trainerId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
