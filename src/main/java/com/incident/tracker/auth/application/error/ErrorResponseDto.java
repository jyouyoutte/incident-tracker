package com.incident.tracker.auth.application.error;

import java.time.LocalDateTime;

public record ErrorResponseDto(String code, String message, LocalDateTime localDateTime) { }