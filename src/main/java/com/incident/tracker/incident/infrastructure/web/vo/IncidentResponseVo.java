package com.incident.tracker.incident.infrastructure.web.vo;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record IncidentResponseVo(
        Long id,
        String title,
        String description,
        String priority,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
