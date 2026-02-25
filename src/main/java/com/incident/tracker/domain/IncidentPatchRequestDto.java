package com.incident.tracker.domain;

import lombok.Getter;
import lombok.Setter;


/**
 * DTO for updating an existing incident. All fields are optional, allowing for partial updates.
 */
@Getter
@Setter
public class IncidentPatchRequestDto {
    private String title;
    private String description;
    private String priority;
    private String status;
    private String assignedDeveloper;
}
