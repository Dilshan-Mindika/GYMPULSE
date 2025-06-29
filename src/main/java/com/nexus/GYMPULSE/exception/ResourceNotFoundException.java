package com.nexus.GYMPULSE.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Generic custom exception for cases where any resource is not found.
 * Responds with HTTP 404 Not Found.
 * Provides constructors for a generic message or a more specific message
 * detailing the resource name, field, and value that was not found.
 */
@ResponseStatus(HttpStatus.NOT_FOUND) // Maps to HTTP 404 Not Found
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructs a new ResourceNotFoundException with the specified detail message.
     * @param message the detail message.
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a new ResourceNotFoundException with a formatted message.
     * @param resourceName Name of the resource (e.g., "User", "Product").
     * @param fieldName Name of the field used for lookup (e.g., "id", "username").
     * @param fieldValue Value of the field that was not found.
     */
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s : '%s'", resourceName, fieldName, fieldValue));
    }
}
