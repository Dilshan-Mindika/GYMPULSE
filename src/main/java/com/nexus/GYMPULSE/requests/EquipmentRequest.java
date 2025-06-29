package com.nexus.GYMPULSE.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EquipmentRequest {
    // ID is usually system-generated for new equipment, so no validation for creation.
    // Could be validated if part of an update path explicitly.
    private String id;           // Unique identifier

    @NotBlank(message = "Equipment name cannot be blank")
    @Size(min = 2, max = 100, message = "Equipment name must be between 2 and 100 characters")
    private String name;         // Name of the equipment

    @NotBlank(message = "Equipment type cannot be blank")
    @Size(min = 3, max = 50, message = "Equipment type must be between 3 and 50 characters")
    private String type;         // Type of equipment (e.g., Cardio, Strength)

    @NotBlank(message = "Equipment brand cannot be blank")
    @Size(min = 2, max = 50, message = "Equipment brand must be between 2 and 50 characters")
    private String brand;        // Brand of the equipment

    @Min(value = 0, message = "Quantity cannot be negative")
    private int quantity;        // Number of units available

    @NotNull(message = "Availability status cannot be null")
    private boolean available;   // Availability status
}
