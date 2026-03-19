package com.incident.tracker.application.service;

import com.incident.tracker.application.dto.incident.IncidentPatchRequestDto;
import com.incident.tracker.application.dto.incident.IncidentRequestDto;
import com.incident.tracker.application.dto.incident.IncidentResponseDto;
import com.incident.tracker.domain.exception.IncidentAlreadyClosedException;
import com.incident.tracker.domain.exception.IncidentNotFoundException;
import com.incident.tracker.domain.port.IncidentRepositoryPort;
import com.incident.tracker.mapper.IncidentMapper;
import com.incident.tracker.domain.model.Incident;
import com.incident.tracker.domain.model.IncidentStatus;
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
    public IncidentResponseDto createIncident(IncidentRequestDto request) {
        logger.info("Creating incident");
        // Map request to entity and log some non-sensitive details at debug level
        Incident incident = mapper.toEntity(request);
        Incident saved = incidentRepository.save(incident);
        logger.info("Incident created with id={} and title={}", saved.getId(), saved.getTitle());
        return mapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<IncidentResponseDto> getAllIncidents() {
        logger.info("Fetching all incidents");
        List<IncidentResponseDto> results = incidentRepository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
        logger.info("Fetched {} incident(s)", results.size());
        return results;
    }

    @Transactional(readOnly = true)
    public IncidentResponseDto getIncidentById(Long id) {
        logger.info("Fetching incident with id={}", id);
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new IncidentNotFoundException(id));
        return mapper.toResponse(incident);
    }

    @Transactional
    public IncidentResponseDto closeIncident(Long id) {
        logger.info("Closing incident with id={}", id);
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new IncidentNotFoundException(id));

        if (incident.getStatus() == IncidentStatus.CLOSED) {
            logger.warn("Attempt to close an already closed incident id={}", id);
            throw new IncidentAlreadyClosedException(id);
        }

        incident.setStatus(IncidentStatus.CLOSED);
        Incident updated = incidentRepository.save(incident);
        logger.info("Incident closed");
        return mapper.toResponse(updated);
    }

    @Transactional
    public IncidentResponseDto updateIncident(Long id, IncidentPatchRequestDto request) {
        logger.info("Updating incident with id={}", id);
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new IncidentNotFoundException(id));

        if (incident.getStatus() == IncidentStatus.CLOSED) {
            logger.warn("Attempt to update an already closed incident id={}", id);
            throw new IncidentAlreadyClosedException(id);
        }

        mapper.updateEntityFromDto(request, incident);
        Incident updated = incidentRepository.save(incident);
        logger.info("Incident updated");
        return mapper.toResponse(updated);
    }
}
