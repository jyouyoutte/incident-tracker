package com.incident.tracker.service;

import com.incident.tracker.domain.IncidentRequestDto;
import com.incident.tracker.domain.IncidentResponseDto;
import com.incident.tracker.exception.IncidentAlreadyClosedException;
import com.incident.tracker.exception.IncidentNotFoundException;
import com.incident.tracker.mapper.IncidentMapper;
import com.incident.tracker.model.Incident;
import com.incident.tracker.model.IncidentStatus;
import com.incident.tracker.repository.IncidentRepository;
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
        logger.info("Create incident");
        Incident incident = mapper.toEntity(request);
        Incident saved = incidentRepository.save(incident);
        logger.info("Incident created");
        return mapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<IncidentResponseDto> getAllIncidents() {
        return incidentRepository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional
    public IncidentResponseDto closeIncident(Long id) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new IncidentNotFoundException(id));

        if (incident.getStatus() == IncidentStatus.CLOSED) {
            throw new IncidentAlreadyClosedException(id);
        }

        incident.setStatus(IncidentStatus.CLOSED);
        Incident updated = incidentRepository.save(incident);
        logger.info("Incident closed");
        return mapper.toResponse(updated);
    }

}
