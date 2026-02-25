package com.incident.tracker.domain;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class IncidentRequestDto{
        @NotBlank
        private String title;
        private String description;
        private String priority;
        @NotBlank
        private String status;
}
