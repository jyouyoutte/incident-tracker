package com.incident.tracker.infrastructure.security.exception;

/**
 * Exception for user creation failure (HTTP 500)
 */
public class UserNotCreatedException extends RuntimeException {
    public UserNotCreatedException(String username) {
        super("User with username " + username + " not created: " + "An unexpected error occurred during user creation. Please try again later.");
    }
}
