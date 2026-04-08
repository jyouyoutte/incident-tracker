package com.incident.tracker.incident.infrastructure.web.controller;

import com.incident.tracker.incident.application.service.IncidentService;
import com.incident.tracker.incident.infrastructure.web.vo.IncidentPatchRequestVo;
import com.incident.tracker.incident.infrastructure.web.vo.IncidentRequestVo;
import com.incident.tracker.incident.infrastructure.web.vo.IncidentResponseVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class IncidentControllerImpl implements IncidentController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final IncidentService service;

    public IncidentControllerImpl(IncidentService service) {
        this.service = service;
    }

    public IncidentResponseVo create(IncidentRequestVo request) {
        logger.info("HTTP POST /api/incidents - Creating new incident with title={}", request.getTitle());
        return service.createIncident(request);
    }

    public List<IncidentResponseVo> getAll() {
        logger.info("HTTP GET /api/incidents - Fetching all incidents");
        return service.getAllIncidents();
    }

    public IncidentResponseVo getById(Long id) {
        logger.info("HTTP GET /api/incidents/{}", id);
        return service.getIncidentById(id);
    }

    public IncidentResponseVo close(Long id) {
        logger.info("HTTP POST /api/incidents/{}/close", id);
        return service.closeIncident(id);
    }

    public IncidentResponseVo update(Long id, IncidentPatchRequestVo request) {
        logger.info("HTTP PATCH /api/incidents/{} - Updating incident with title={}", id, request.getTitle());
        return service.updateIncident(id, request);
    }
}