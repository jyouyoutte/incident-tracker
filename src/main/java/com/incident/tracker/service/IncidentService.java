package com.incident.tracker.service;

import com.incident.tracker.domain.IncidentPatchRequestDto;
import com.incident.tracker.domain.IncidentRequestDto;
import com.incident.tracker.domain.IncidentResponseDto;
import com.incident.tracker.exception.IncidentAlreadyClosedException;
import com.incident.tracker.exception.IncidentNotFoundException;
import com.incident.tracker.mapper.IncidentMapper;
import com.incident.tracker.model.Incident;
import com.incident.tracker.model.IncidentStatus;
import com.incident.tracker.model.Priority;
import com.incident.tracker.repository.IncidentRepository;
import com.incident.tracker.utils.EnumFinderUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class IncidentService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final IncidentRepository incidentRepository;
    private final IncidentMapper mapper;

    public IncidentService(IncidentRepository incidentRepository, IncidentMapper mapper) {
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
