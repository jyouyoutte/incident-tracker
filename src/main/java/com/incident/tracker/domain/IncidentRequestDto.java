package com.incident.tracker.domain;

import jakarta.validation.constraints.NotBlank;

public record IncidentRequestDto(
        @NotBlank
        String title,
        @NotBlank
        String description,
        String priority,
        String assignedDeveloper
) {
}
