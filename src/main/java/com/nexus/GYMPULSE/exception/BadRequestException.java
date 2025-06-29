package com.nexus.GYMPULSE.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception to indicate a bad request (HTTP 400).
 * This is typically thrown when client input is malformed or invalid in a way
 * not covered by standard validation annotations (e.g., invalid query parameters).
 */
@ResponseStatus(HttpStatus.BAD_REQUEST) // Ensures Spring maps this exception to a 400 status code
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
