package com.incident.tracker.incident.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class IncidentDto  {
    private Long id;
    private String title;
    private String description;
    private String priority;
    private String incidentStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String assignedDeveloper;
}
