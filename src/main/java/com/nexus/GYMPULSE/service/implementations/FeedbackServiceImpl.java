package com.nexus.GYMPULSE.service.implementations;

import com.nexus.GYMPULSE.exception.ResourceNotFoundException; // Import custom exception
import com.nexus.GYMPULSE.model.feedback.Feedback;
import com.nexus.GYMPULSE.repositories.FeedbackRepository;
import com.nexus.GYMPULSE.requests.FeedbackRequest;
import com.nexus.GYMPULSE.service.interfaces.FeedbackService;
import org.slf4j.Logger; // Import SLF4J Logger
import org.slf4j.LoggerFactory; // Import SLF4J LoggerFactory
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
// import java.util.NoSuchElementException; // Replaced with custom exception
import java.util.Optional;

@Service
public class FeedbackServiceImpl implements FeedbackService {
    private static final Logger logger = LoggerFactory.getLogger(FeedbackServiceImpl.class); // SLF4J Logger

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Override
    public Feedback createFeedback(FeedbackRequest feedbackRequest) {
        Feedback feedback = new Feedback();
        feedback.setMemberId(feedbackRequest.getMemberId());
        feedback.setTrainerId(feedbackRequest.getTrainerId());
        feedback.setComments(feedbackRequest.getComments());
        feedback.setRating(feedbackRequest.getRating());
        Feedback savedFeedback = feedbackRepository.save(feedback);
        logger.info("Feedback created with ID: {}", savedFeedback.getId());
        return savedFeedback;
    }

    @Override
    public List<Feedback> getAllFeedbacks() {
        logger.info("Retrieving all feedbacks");
        return feedbackRepository.findAll();
    }

    @Override
    public Optional<Feedback> getFeedbackById(String id) {
        logger.info("Retrieving feedback by ID: {}", id);
        // Controller will handle ResourceNotFoundException if Optional is empty.
        return feedbackRepository.findById(id);
    }

    @Override
    public List<Feedback> getFeedbackByTrainerId(String trainerId) {
        logger.info("Retrieving feedbacks for trainer ID: {}", trainerId);
        return feedbackRepository.findByTrainerId(trainerId);
    }

    @Override
    public List<Feedback> getFeedbackByMemberId(String memberId) {
        logger.info("Retrieving feedbacks for member ID: {}", memberId);
        return feedbackRepository.findByMemberId(memberId);
    }

    @Override
    public Feedback updateFeedback(String id, FeedbackRequest feedbackRequest) {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback", "id", id));

        // MemberId and TrainerId are generally not updatable in a feedback.
        // Only comments and rating.
        feedback.setComments(feedbackRequest.getComments());
        feedback.setRating(feedbackRequest.getRating());
        Feedback updatedFeedback = feedbackRepository.save(feedback);
        logger.info("Feedback updated for ID: {}", id);
        return updatedFeedback;
    }

    @Override
    public void deleteFeedbackById(String id) {
        if (!feedbackRepository.existsById(id)) {
            throw new ResourceNotFoundException("Feedback", "id", id);
        }
        feedbackRepository.deleteById(id);
        logger.info("Feedback deleted with ID: {}", id);
    }
}
