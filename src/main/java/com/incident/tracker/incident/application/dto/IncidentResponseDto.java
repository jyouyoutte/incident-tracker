package com.incident.tracker.incident.application.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record IncidentResponseDto(
        @NotBlank
        Long id,
        String title,
        @NotBlank
        String description,
        String priority,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
