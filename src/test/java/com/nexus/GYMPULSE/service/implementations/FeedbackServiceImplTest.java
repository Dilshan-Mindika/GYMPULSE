package com.nexus.GYMPULSE.service.implementations;

import com.nexus.GYMPULSE.exception.ResourceNotFoundException;
import com.nexus.GYMPULSE.model.feedback.Feedback;
import com.nexus.GYMPULSE.repositories.FeedbackRepository;
import com.nexus.GYMPULSE.requests.FeedbackRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeedbackServiceImplTest {

    @Mock
    private FeedbackRepository feedbackRepository;

    @InjectMocks
    private FeedbackServiceImpl feedbackService;

    private Feedback feedback1;
    private FeedbackRequest feedbackRequest;

    @BeforeEach
    void setUp() {
        // Feedback ID is String (MongoDB ObjectId)
        feedback1 = new Feedback("F001", "M001", "T001", "Great session!", 5, LocalDateTime.now());

        feedbackRequest = new FeedbackRequest();
        feedbackRequest.setMemberId("M002");
        feedbackRequest.setTrainerId("T002"); // Optional
        feedbackRequest.setComments("Very helpful advice.");
        feedbackRequest.setRating(4);
    }

    @Test
    void createFeedback_success() {
        when(feedbackRepository.save(any(Feedback.class))).thenAnswer(invocation -> {
            Feedback fb = invocation.getArgument(0);
            if (fb.getId() == null) {
                fb.setId("genFbID"); // Simulate ID generation
            }
            // Simulate timestamp generation if service sets it (current model does it automatically)
            if (fb.getTimestamp() == null) {
                 fb.setTimestamp(LocalDateTime.now());
            }
            return fb;
        });

        Feedback createdFeedback = feedbackService.createFeedback(feedbackRequest);

        assertNotNull(createdFeedback);
        assertEquals("M002", createdFeedback.getMemberId());
        assertEquals(4, createdFeedback.getRating());
        assertNotNull(createdFeedback.getId());
        assertNotNull(createdFeedback.getTimestamp());
        verify(feedbackRepository, times(1)).save(any(Feedback.class));
    }

    @Test
    void getAllFeedbacks_success() {
        Feedback feedback2 = new Feedback("F002", "M003", null, "Good gym.", 3, LocalDateTime.now());
        when(feedbackRepository.findAll()).thenReturn(Arrays.asList(feedback1, feedback2));

        List<Feedback> feedbacks = feedbackService.getAllFeedbacks();

        assertEquals(2, feedbacks.size());
        verify(feedbackRepository, times(1)).findAll();
    }

    @Test
    void getFeedbackById_found() {
        when(feedbackRepository.findById("F001")).thenReturn(Optional.of(feedback1));
        Optional<Feedback> foundFeedback = feedbackService.getFeedbackById("F001");
        assertTrue(foundFeedback.isPresent());
        assertEquals("Great session!", foundFeedback.get().getComments());
    }

    @Test
    void getFeedbackById_notFound() {
        when(feedbackRepository.findById("F999")).thenReturn(Optional.empty());
        Optional<Feedback> foundFeedback = feedbackService.getFeedbackById("F999");
        assertFalse(foundFeedback.isPresent());
    }

    @Test
    void getFeedbackByTrainerId_success() {
        when(feedbackRepository.findByTrainerId("T001")).thenReturn(Collections.singletonList(feedback1));
        List<Feedback> trainerFeedbacks = feedbackService.getFeedbackByTrainerId("T001");
        assertFalse(trainerFeedbacks.isEmpty());
        assertEquals("T001", trainerFeedbacks.get(0).getTrainerId());
    }

    @Test
    void getFeedbackByTrainerId_noneFound() {
         when(feedbackRepository.findByTrainerId("T999")).thenReturn(Collections.emptyList());
        List<Feedback> trainerFeedbacks = feedbackService.getFeedbackByTrainerId("T999");
        assertTrue(trainerFeedbacks.isEmpty());
    }


    @Test
    void getFeedbackByMemberId_success() {
        when(feedbackRepository.findByMemberId("M001")).thenReturn(Collections.singletonList(feedback1));
        List<Feedback> memberFeedbacks = feedbackService.getFeedbackByMemberId("M001");
        assertFalse(memberFeedbacks.isEmpty());
        assertEquals("M001", memberFeedbacks.get(0).getMemberId());
    }

    @Test
    void updateFeedback_success() {
        when(feedbackRepository.findById("F001")).thenReturn(Optional.of(feedback1));
        when(feedbackRepository.save(any(Feedback.class))).thenAnswer(invocation -> invocation.getArgument(0));

        feedbackRequest.setComments("Excellent session, much improved!");
        feedbackRequest.setRating(5);
        // MemberId and TrainerId are not updated in feedback, only comments and rating
        Feedback updatedFeedback = feedbackService.updateFeedback("F001", feedbackRequest);

        assertNotNull(updatedFeedback);
        assertEquals("Excellent session, much improved!", updatedFeedback.getComments());
        assertEquals(5, updatedFeedback.getRating());
        assertEquals("F001", updatedFeedback.getId()); // ID should remain
        assertEquals(feedback1.getMemberId(), updatedFeedback.getMemberId()); // MemberId should not change
        verify(feedbackRepository, times(1)).save(any(Feedback.class));
    }

    @Test
    void updateFeedback_notFound() {
        when(feedbackRepository.findById("F999")).thenReturn(Optional.empty());
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            feedbackService.updateFeedback("F999", feedbackRequest);
        });
        assertEquals("Feedback not found with id : 'F999'", exception.getMessage());
    }

    @Test
    void deleteFeedbackById_success() {
        when(feedbackRepository.existsById("F001")).thenReturn(true);
        doNothing().when(feedbackRepository).deleteById("F001");

        feedbackService.deleteFeedbackById("F001");
        verify(feedbackRepository, times(1)).deleteById("F001");
    }

    @Test
    void deleteFeedbackById_notFound() {
        when(feedbackRepository.existsById("F999")).thenReturn(false);
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            feedbackService.deleteFeedbackById("F999");
        });
        assertEquals("Feedback not found with id : 'F999'", exception.getMessage());
    }
}
