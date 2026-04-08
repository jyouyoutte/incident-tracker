package com.incident.tracker.incident.application.service;

import com.incident.tracker.incident.application.mapper.IncidentMapper;
import com.incident.tracker.incident.domain.exception.IncidentNotFoundException;
import com.incident.tracker.incident.domain.model.Incident;
import com.incident.tracker.incident.domain.port.IncidentRepositoryPort;
import com.incident.tracker.incident.infrastructure.web.vo.IncidentPatchRequestVo;
import com.incident.tracker.incident.infrastructure.web.vo.IncidentRequestVo;
import com.incident.tracker.incident.infrastructure.web.vo.IncidentResponseVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class IncidentService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final IncidentRepositoryPort incidentRepositoryPort;
    private final IncidentMapper incidentMapper;

    public IncidentService(IncidentRepositoryPort incidentRepository, IncidentMapper mapper) {
        this.incidentRepositoryPort = incidentRepository;
        this.incidentMapper = mapper;
    }

    @Transactional
    public IncidentResponseVo createIncident(IncidentRequestVo request) {
        logger.info("Creating incident");
        Incident incident = incidentMapper.toDomain(request);
        incident.create();
        Incident saved = incidentRepositoryPort.save(incident);
        logger.info("Incident created with id={} and title={}", saved.getId(), saved.getTitle());
        return incidentMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<IncidentResponseVo> getAllIncidents() {
        logger.info("Fetching all incidents");
        List<IncidentResponseVo> results = incidentRepositoryPort.findAll()
                .stream()
                .map(incidentMapper::toResponse)
                .toList();
        logger.info("Fetched {} incident(s)", results.size());
        return results;
    }

    @Transactional(readOnly = true)
    public IncidentResponseVo getIncidentById(Long id) {
        logger.info("Fetching incident with id={}", id);
        Incident incident = incidentRepositoryPort.findById(id)
                .orElseThrow(() -> new IncidentNotFoundException(id));
        return incidentMapper.toResponse(incident);
    }

    @Transactional
    public IncidentResponseVo closeIncident(Long id) {
        logger.info("Closing incident with id={}", id);
        Incident incident = incidentRepositoryPort.findById(id)
                .orElseThrow(() -> new IncidentNotFoundException(id));
        incident.close();
        Incident updated = incidentRepositoryPort.save(incident);
        logger.info("Incident closed");
        return incidentMapper.toResponse(updated);
    }

    @Transactional
    public IncidentResponseVo updateIncident(Long id, IncidentPatchRequestVo request) {
        logger.info("Updating incident with id={}", id);
        Incident incident = incidentRepositoryPort.findById(id)
                .orElseThrow(() -> new IncidentNotFoundException(id));

        incident.update(
                request.getTitle(),
                request.getDescription(),
                request.getPriority(),
                request.getStatus()
        );

        Incident updated = incidentRepositoryPort.save(incident);

        logger.info("Incident updated at {}", updated.getUpdatedAt());
        return incidentMapper.toResponse(updated);
    }
}
