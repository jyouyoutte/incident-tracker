package com.incident.tracker.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(IncidentNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDto handleNotFound(IncidentNotFoundException ex) {
        return new ErrorResponseDto("NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(IncidentAlreadyClosedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponseDto handleIllegalState(IncidentAlreadyClosedException ex) {
        return new ErrorResponseDto("ALREADY_CLOSED", ex.getMessage());
    }
}
