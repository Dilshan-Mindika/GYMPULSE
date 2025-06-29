package com.nexus.GYMPULSE.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception to indicate that the maximum limit for creating trainers has been reached.
 * Responds with HTTP 409 Conflict.
 */
@ResponseStatus(HttpStatus.CONFLICT) // Maps to HTTP 409 Conflict
public class MaxTrainerLimitReachedException extends RuntimeException {
    public MaxTrainerLimitReachedException(String message) {
        super(message);
    }
}
