package com.incident.tracker.infrastructure.web;

import com.incident.tracker.domain.exception.IncidentAlreadyClosedException;
import com.incident.tracker.domain.exception.IncidentNotFoundException;
import com.incident.tracker.application.dto.error.ErrorResponseDto;
import com.incident.tracker.infrastructure.security.exception.UserAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseDto runtimeExceptionHandler(RuntimeException ex) {
        return new ErrorResponseDto("SERVER_ERROR", ex.getMessage());
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponseDto handleIllegalState(UserAlreadyExistsException ex) {
        return new ErrorResponseDto("USER_ALREADY_EXISTS", ex.getMessage());
    }
}
