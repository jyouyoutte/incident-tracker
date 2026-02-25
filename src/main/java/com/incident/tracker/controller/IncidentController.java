package com.incident.tracker.controller;

import com.incident.tracker.domain.IncidentPatchRequestDto;
import com.incident.tracker.domain.IncidentRequestDto;
import com.incident.tracker.domain.IncidentResponseDto;
import com.incident.tracker.service.IncidentService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/incidents")
public class IncidentController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final IncidentService service;

    public IncidentController(IncidentService service) {
        this.service = service;
    }

    @PostMapping
    public IncidentResponseDto create(@Valid @RequestBody IncidentRequestDto request) {
        return service.createIncident(request);
    }

    @GetMapping
    public List<IncidentResponseDto> getAll() {
        return service.getAllIncidents();
    }

    @PostMapping("/{id}/close")
    public IncidentResponseDto close(@PathVariable Long id) {
        return service.closeIncident(id);
    }

    @PatchMapping("/{id}")
    public IncidentResponseDto update(@PathVariable Long id, @Valid @RequestBody IncidentPatchRequestDto request) {
        return service.updateIncident(id, request);
    }

}
