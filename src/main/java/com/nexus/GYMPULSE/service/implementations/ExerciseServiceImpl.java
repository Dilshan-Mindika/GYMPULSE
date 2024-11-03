package com.nexus.GYMPULSE.service.implementations;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nexus.GYMPULSE.model.workoutplan.Exercise;
import com.nexus.GYMPULSE.repositories.ExerciseRepository;
import com.nexus.GYMPULSE.requests.ExerciseRequest;
import com.nexus.GYMPULSE.service.interfaces.ExerciseService;
import com.nexus.GYMPULSE.utils.GymLogger;

@Service
public class ExerciseServiceImpl implements ExerciseService {
    private GymLogger logger = GymLogger.getInstance();
    
    @Autowired
    private ExerciseRepository exerciseRepository;

    @Override
    public Exercise createExercise(String name, Integer quantitySets, Integer quantityReps, Integer resTimeSeconds) {
        Exercise exercise = exerciseRepository.insert(new Exercise(name, quantitySets, quantityReps, resTimeSeconds));
        logger.log("New Exercise created, Name: " + name);
        return exercise;
    }
    @Override
    public List<Exercise> allExercises() {
        return exerciseRepository.findAll();
    }
    @Override
    public Optional<Exercise> exerciseById(String id) {
        return exerciseRepository.findById(id);
    }
    @Override
    public Exercise updateExercise(String id, ExerciseRequest exerciseRequest) {
        Optional<Exercise> optionalExercise = exerciseRepository.findById(id);
        if(optionalExercise.isPresent()) {
            Exercise exercise = optionalExercise.get();
            exercise.setName(exerciseRequest.getName());
            exercise.setQuantityReps(exerciseRequest.getQuantityReps());
            exercise.setQuantitySets(exerciseRequest.getQuantitySets());
            exercise.setResTimeSeconds(exerciseRequest.getResTimeSeconds());
            return exerciseRepository.save(exercise);
        } else {
            throw new NoSuchElementException();
        }
    }
    @Override
    public Exercise cloneExercise(String id) {
        Optional<Exercise> optionalExercise = exerciseRepository.findById(id);
        if(optionalExercise.isPresent()) {
            Exercise original = optionalExercise.get();
            Exercise cloned = original.clone();
            return exerciseRepository.save(cloned);
        } else {
            throw new NoSuchElementException();
        }
    }
    @Override
    public void deleteExerciseById(String id) {
        Optional<Exercise> exercise = exerciseRepository.findById(id);
        if(exercise.isPresent()) {
            logger.log("Exercise deleted, ID: " + id);
            exerciseRepository.delete(exercise.get());
        } else {
            throw new NoSuchElementException();
        }
    }
}
