package com.incident.tracker.incident.infrastructure.web.controller;

import com.incident.tracker.incident.application.dto.IncidentDto;
import com.incident.tracker.incident.application.service.IncidentService;
import com.incident.tracker.incident.infrastructure.web.mapper.IncidentWebMapper;
import com.incident.tracker.incident.infrastructure.web.vo.IncidentPatchRequestVo;
import com.incident.tracker.incident.infrastructure.web.vo.IncidentRequestVo;
import com.incident.tracker.incident.infrastructure.web.vo.IncidentResponseVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class IncidentControllerImpl implements IncidentController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final IncidentService service;
    private final IncidentWebMapper mapper;

    public IncidentControllerImpl(IncidentService service, IncidentWebMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @Override
    public IncidentResponseVo create(IncidentRequestVo request) {
        logger.info("HTTP POST /api/incidents - Creating new incident with title={}", request.getTitle());
        IncidentDto incidentDto = mapper.fromCreateRequestToDto(request);
        IncidentDto created = service.createIncident(incidentDto);
        return mapper.toWebResponse(created);
    }


        @Override
    public List<IncidentResponseVo> getAll() {
        logger.info("HTTP GET /api/incidents - getAll");
        return service.getAllIncidents()
                .stream()
                .map(mapper::toWebResponse)
                .collect(Collectors.toList());
    }

    @Override
    public IncidentResponseVo getById(Long id) {
        logger.info("HTTP GET /api/incidents/{}", id);
        return mapper.toWebResponse(service.getIncidentById(id));
    }

    @Override
    public IncidentResponseVo close(Long id) {
        logger.info("HTTP POST /api/incidents/{}/close", id);
        return mapper.toWebResponse(service.closeIncident(id));
    }

    @Override
    public IncidentResponseVo update(Long id, IncidentPatchRequestVo request) {
        logger.info("HTTP PATCH /api/incidents/{} - update", id);
        IncidentDto patchDto = mapper.fromPatchRequestToDto(request);
        return mapper.toWebResponse(service.updateIncident(id, patchDto));
    }
}