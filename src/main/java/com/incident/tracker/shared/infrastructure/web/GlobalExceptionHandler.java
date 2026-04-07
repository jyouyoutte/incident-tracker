package com.incident.tracker.shared.infrastructure.web;

import com.incident.tracker.auth.application.error.ErrorResponseDto;
import com.incident.tracker.incident.domain.exception.IncidentAlreadyClosedException;
import com.incident.tracker.incident.domain.exception.IncidentNotFoundException;
import com.incident.tracker.auth.infrastructure.security.exception.UserAlreadyExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler(IncidentNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDto handleIncidentNotFound(IncidentNotFoundException ex) {
        logger.warn("Incident not found: {}", ex.getMessage());
        return new ErrorResponseDto("NOT_FOUND", ex.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(IncidentAlreadyClosedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponseDto handleIncidentClosed(IncidentAlreadyClosedException ex) {
        logger.warn("Incident already closed: {}", ex.getMessage());
        return new ErrorResponseDto("ALREADY_CLOSED", ex.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponseDto handleUserAlreadyExists(UserAlreadyExistsException ex) {
        logger.warn("User already exists: {}", ex.getMessage());
        return new ErrorResponseDto("USER_ALREADY_EXISTS", ex.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        logger.warn("Validation failed: {}", ex.getMessage());
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        String message = generateMessage(fieldErrors);
        if (message.isBlank()) message = "Validation failed";
        return new ErrorResponseDto("BAD_REQUEST", message, LocalDateTime.now());
    }

    @ExceptionHandler()
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleBindException(BindException ex) {
        logger.warn("Bind validation failed: {}", ex.getMessage());
        List<FieldError> fieldErrors = ex.getFieldErrors();
        String message = generateMessage(fieldErrors);
        if (message.isBlank()) message = "Validation failed";
        return new ErrorResponseDto("BAD_REQUEST", message, LocalDateTime.now());
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponseDto handleAccessDenied(AccessDeniedException ex) {
        logger.warn("Access denied");
        return new ErrorResponseDto("ACCESS_DENIED", "Access denied", LocalDateTime.now());
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponseDto handleAuthenticationException(Exception ex) {
        logger.warn("Authentication failed");
        return new ErrorResponseDto("UNAUTHORIZED", "Authentication failed", LocalDateTime.now());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseDto handleGlobalException(Exception ex) {
        logger.error("Unexpected error occurred", ex);
        return new ErrorResponseDto("SERVER_ERROR", "An unexpected error occurred", LocalDateTime.now());
    }

    private String generateMessage(List<FieldError> fieldErrors) {
        return fieldErrors!=null && !fieldErrors.isEmpty() ? fieldErrors.stream()
                .map(fe -> fe.getField() + " " + fe.getDefaultMessage())
                .collect(Collectors.joining(", ")) : "";
    }
}
