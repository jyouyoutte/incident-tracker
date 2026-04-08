package com.incident.tracker.incident.application.service;

import com.incident.tracker.incident.infrastructure.web.vo.IncidentPatchRequestVo;
import com.incident.tracker.incident.infrastructure.web.vo.IncidentRequestVo;
import com.incident.tracker.incident.infrastructure.web.vo.IncidentResponseVo;
import com.incident.tracker.incident.domain.exception.IncidentAlreadyClosedException;
import com.incident.tracker.incident.domain.exception.IncidentNotFoundException;
import com.incident.tracker.incident.domain.port.IncidentRepositoryPort;
import com.incident.tracker.mapper.IncidentMapper;
import com.incident.tracker.incident.infrastructure.persistence.entity.IncidentEntity;
import com.incident.tracker.incident.infrastructure.persistence.entity.IncidentStatusEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class IncidentService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final IncidentRepositoryPort incidentRepository;
    private final IncidentMapper mapper;

    public IncidentService(IncidentRepositoryPort incidentRepository, IncidentMapper mapper) {
        this.incidentRepository = incidentRepository;
        this.mapper = mapper;
    }

    @Transactional
    public IncidentResponseVo createIncident(IncidentRequestVo request) {
        logger.info("Creating incident");
        // Map request to entity and log some non-sensitive details at debug level
        IncidentEntity incident = mapper.toEntity(request);
        IncidentEntity saved = incidentRepository.save(incident);
        logger.info("Incident created with id={} and title={}", saved.getId(), saved.getTitle());
        return mapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<IncidentResponseVo> getAllIncidents() {
        logger.info("Fetching all incidents");
        List<IncidentResponseVo> results = incidentRepository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
        logger.info("Fetched {} incident(s)", results.size());
        return results;
    }

    @Transactional(readOnly = true)
    public IncidentResponseVo getIncidentById(Long id) {
        logger.info("Fetching incident with id={}", id);
        IncidentEntity incident = incidentRepository.findById(id)
                .orElseThrow(() -> new IncidentNotFoundException(id));
        return mapper.toResponse(incident);
    }

    @Transactional
    public IncidentResponseVo closeIncident(Long id) {
        logger.info("Closing incident with id={}", id);
        IncidentEntity incident = incidentRepository.findById(id)
                .orElseThrow(() -> new IncidentNotFoundException(id));

        if (incident.getIncidentStatusEntity() == IncidentStatusEntity.CLOSED) {
            logger.warn("Attempt to close an already closed incident id={}", id);
            throw new IncidentAlreadyClosedException(id);
        }

        incident.setIncidentStatusEntity(IncidentStatusEntity.CLOSED);
        IncidentEntity updated = incidentRepository.save(incident);
        logger.info("Incident closed");
        return mapper.toResponse(updated);
    }

    @Transactional
    public IncidentResponseVo updateIncident(Long id, IncidentPatchRequestVo request) {
        logger.info("Updating incident with id={}", id);
        IncidentEntity incident = incidentRepository.findById(id)
                .orElseThrow(() -> new IncidentNotFoundException(id));

        if (incident.getIncidentStatusEntity() == IncidentStatusEntity.CLOSED) {
            logger.warn("Attempt to update an already closed incident id={}", id);
            throw new IncidentAlreadyClosedException(id);
        }

        mapper.updateEntityFromDto(request, incident);
        IncidentEntity updated = incidentRepository.save(incident);
        logger.info("Incident updated");
        return mapper.toResponse(updated);
    }
}
