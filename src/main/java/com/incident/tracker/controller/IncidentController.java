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
        logger.info("HTTP POST /api/incidents - Creating new incident with title={}", request.getTitle());
        return service.createIncident(request);
    }

    @GetMapping
    public List<IncidentResponseDto> getAll() {
        logger.info("HTTP GET /api/incidents - Fetching all incidents");
        return service.getAllIncidents();
    }

    @GetMapping("/{id}")
    public IncidentResponseDto getById(@PathVariable Long id) {
        logger.info("HTTP GET /api/incidents/{}", id);
        return service.getIncidentById(id);
    }

    @PostMapping("/{id}/close")
    public IncidentResponseDto close(@PathVariable Long id) {
        logger.info("HTTP POST /api/incidents/{}/close", id);
        return service.closeIncident(id);
    }

    @PatchMapping("/{id}")
    public IncidentResponseDto update(@PathVariable Long id, @Valid @RequestBody IncidentPatchRequestDto request) {
        logger.info("HTTP PATCH /api/incidents/{} - Updating incident with title={}", id, request.getTitle());
        return service.updateIncident(id, request);
    }
}
