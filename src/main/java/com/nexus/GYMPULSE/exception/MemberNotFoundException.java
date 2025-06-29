package com.nexus.GYMPULSE.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception specifically for cases where a Member resource is not found.
 * Responds with HTTP 404 Not Found.
 */
@ResponseStatus(HttpStatus.NOT_FOUND) // Maps to HTTP 404 Not Found
public class MemberNotFoundException extends RuntimeException {
    public MemberNotFoundException(String message) {
        super(message);
    }
}
