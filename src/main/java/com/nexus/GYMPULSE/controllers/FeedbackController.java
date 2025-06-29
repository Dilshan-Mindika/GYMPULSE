package com.nexus.GYMPULSE.controllers;

import com.nexus.GYMPULSE.model.feedback.Feedback;
import com.nexus.GYMPULSE.requests.FeedbackRequest;
import com.nexus.GYMPULSE.service.interfaces.FeedbackService;
import jakarta.validation.Valid; // Import @Valid
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus; // Import HttpStatus
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/feedbacks")
public class FeedbackController {
    @Autowired
    private FeedbackService feedbackService;

    @PostMapping
    public ResponseEntity<Feedback> createFeedback(@Valid @RequestBody FeedbackRequest feedbackRequest) { // Added @Valid
        Feedback createdFeedback = feedbackService.createFeedback(feedbackRequest);
        return new ResponseEntity<>(createdFeedback, HttpStatus.CREATED); // Return 201 Created
    }

    @GetMapping
    public ResponseEntity<List<Feedback>> getAllFeedbacks() {
        List<Feedback> feedbacks = feedbackService.getAllFeedbacks();
        return ResponseEntity.ok(feedbacks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Feedback> getFeedbackById(@PathVariable String id) {
        // Service should throw ResourceNotFoundException if not found.
        return ResponseEntity.ok(feedbackService.getFeedbackById(id)
                .orElseThrow(() -> new com.nexus.GYMPULSE.exception.ResourceNotFoundException("Feedback", "id", id)));
    }

    @GetMapping("/trainer/{trainerId}")
    public ResponseEntity<List<Feedback>> getFeedbackByTrainerId(@PathVariable String trainerId) {
        List<Feedback> feedbacks = feedbackService.getFeedbackByTrainerId(trainerId);
        // Consider if this should return 404 if trainerId is valid but has no feedback, or just empty list.
        // Current behavior is empty list, which is often acceptable.
        return ResponseEntity.ok(feedbacks);
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<Feedback>> getFeedbackByMemberId(@PathVariable String memberId) {
        List<Feedback> feedbacks = feedbackService.getFeedbackByMemberId(memberId);
        // Similar consideration as above for trainerId.
        return ResponseEntity.ok(feedbacks);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Feedback> updateFeedback(@PathVariable String id, @Valid @RequestBody FeedbackRequest feedbackRequest) { // Added @Valid
        // Service should throw ResourceNotFoundException if not found.
        Feedback updatedFeedback = feedbackService.updateFeedback(id, feedbackRequest);
        return ResponseEntity.ok(updatedFeedback);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFeedback(@PathVariable String id) {
        // Service should throw ResourceNotFoundException if not found before deleting.
        feedbackService.deleteFeedbackById(id);
        return ResponseEntity.noContent().build();
    }
}
