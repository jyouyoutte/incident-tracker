package com.incident.tracker.application.dto.error;

import java.time.LocalDateTime;

public record ErrorResponseDto(String code, String message, LocalDateTime localDateTime) { }