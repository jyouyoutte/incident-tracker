package com.incident.tracker.incident.domain.exception;

/**
 * Exception for conflict status (HTTP 409)
 */
public class IncidentAlreadyClosedException extends IllegalStateException {
    public IncidentAlreadyClosedException(Long id) {
        super("Incident with id " + id + " is already closed");
    }
}
