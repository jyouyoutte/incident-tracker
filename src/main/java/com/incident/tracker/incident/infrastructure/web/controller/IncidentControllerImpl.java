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

    @Override
    public IncidentResponseVo create(IncidentRequestVo request) {
        logger.info("HTTP POST /api/incidents - Creating new incident with title={}", request.getTitle());
        return service.createIncident(request);
    }

    @Override
    public List<IncidentResponseVo> getAll() {
        logger.info("HTTP GET /api/incidents - getAll");
        return service.getAllIncidents();
    }

    @Override
    public IncidentResponseVo getById(Long id) {
        logger.info("HTTP GET /api/incidents/{}", id);
        return service.getIncidentById(id);
    }

    @Override
    public IncidentResponseVo close(Long id) {
        logger.info("HTTP POST /api/incidents/{}/close", id);
        return service.closeIncident(id);
    }

    @Override
    public IncidentResponseVo update(Long id, IncidentPatchRequestVo request) {
        logger.info("HTTP PATCH /api/incidents/{} - update", id);
        return service.updateIncident(id, request);
    }
}