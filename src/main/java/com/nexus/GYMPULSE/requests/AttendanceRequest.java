package com.nexus.GYMPULSE.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data // Added Lombok @Data
public class AttendanceRequest {

    @NotBlank(message = "Member ID cannot be blank")
    private String memberId; // ID of the member

    @NotBlank(message = "Time slot ID cannot be blank")
    private String timeSlotId; // ID of the time slot

    @NotNull(message = "Date cannot be null")
    // @FutureOrPresent or @PastOrPresent might be applicable depending on business logic
    private LocalDate date; // date of attendance

    // 'attended' is a boolean, @NotNull can be used if it must be provided.
    // For primitive boolean, it defaults to false, so @NotNull might not be strictly needed
    // unless explicit true/false is required in the request.
    // Assuming it's fine as is, or @NotNull if explicit value is mandatory.
    @NotNull(message = "Attended status cannot be null")
    private Boolean attended; // attendance status - Changed to Boolean for @NotNull

    // Getters and Setters are handled by @Data
}
