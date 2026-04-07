package com.incident.tracker.incident.infrastructure.web.vo;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record IncidentResponseVO(
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
