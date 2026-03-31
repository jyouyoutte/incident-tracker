package com.incident.tracker.infrastructure.security.exception;

/**
 * Exception for user creation failure (HTTP 500)
 */
public class UserNotCreatedException extends RuntimeException {
    public UserNotCreatedException(String message) {
        super(message);
    }

}
