package com.incident.tracker.domain;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "priority of the incident", allowableValues = {"CRITICAL","HIGH","MODERATE","LOW"}, example = "HIGH")
    private String priority;
    @Schema(description = "Status of the incident", allowableValues = {"OPEN", "IN_PROGRESS","RESOLVED","CLOSED"}, example = "OPEN")
    private String status;
    private String assignedDeveloper;
}
