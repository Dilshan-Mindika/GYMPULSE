package com.nexus.GYMPULSE.requests;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data // Added Lombok @Data
public class FeedbackRequest {

    @NotBlank(message = "Member ID cannot be blank")
    private String memberId; // ID of the member giving feedback

    // trainerId is optional, so no @NotBlank. If present, could have format validation.
    private String trainerId; // ID of the trainer (optional)

    @NotBlank(message = "Comments cannot be blank")
    @Size(min = 10, max = 1000, message = "Comments must be between 10 and 1000 characters")
    private String comments; // Feedback comments

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private int rating; // Rating out of 5

    // Getters and Setters are handled by @Data
}
