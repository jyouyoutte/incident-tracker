package com.incident.tracker.incident.infrastructure.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class IncidentRequestVo {
        @NotBlank
        private String title;
        private String description;
        @Schema(description = "priority of the incident", allowableValues = {"CRITICAL","HIGH","MODERATE","LOW"}, example = "HIGH")
        private String priority;
        @NotBlank
        @Schema(description = "Status of the incident", allowableValues = {"OPEN", "IN_PROGRESS","RESOLVED","CLOSED"}, example = "OPEN")
        private String status;
}
