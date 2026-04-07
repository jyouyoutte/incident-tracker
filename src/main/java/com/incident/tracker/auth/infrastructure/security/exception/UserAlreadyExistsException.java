package com.incident.tracker.auth.infrastructure.security.exception;

/**
 * Exception for conflict status (HTTP 409)
 */
public class UserAlreadyExistsException extends IllegalStateException {
    public UserAlreadyExistsException(String username) {
        super("User with username " + username + " already exists");
    }
}
