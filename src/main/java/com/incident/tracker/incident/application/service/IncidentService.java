package com.incident.tracker.incident.application.service;

import com.incident.tracker.incident.application.dto.IncidentDto;
import com.incident.tracker.incident.application.mapper.IncidentMapper;
import com.incident.tracker.incident.domain.exception.IncidentNotFoundException;
import com.incident.tracker.incident.domain.model.Incident;
import com.incident.tracker.incident.domain.port.IncidentRepositoryPort;
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
    public IncidentDto createIncident(IncidentDto dto) {
        logger.info("Creating incident");
        Incident incident = incidentMapper.toDomain(dto);
        incident.create();
        Incident saved = incidentRepositoryPort.save(incident);
        logger.info("Incident created with id={} and title={}", saved.getId(), saved.getTitle());
        return incidentMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<IncidentDto> getAllIncidents() {
        logger.info("Fetching all incidents");
        List<IncidentDto> results = incidentRepositoryPort.findAll()
                .stream()
                .map(incidentMapper::toDto)
                .toList();
        logger.info("Fetched {} incident(s)", results.size());
        return results;
    }

    @Transactional(readOnly = true)
    public IncidentDto getIncidentById(Long id) {
        logger.info("Fetching incident with id={}", id);
        Incident incident = incidentRepositoryPort.findById(id)
                .orElseThrow(() -> new IncidentNotFoundException(id));
        return incidentMapper.toDto(incident);
    }

    @Transactional
    public IncidentDto closeIncident(Long id) {
        logger.info("Closing incident with id={}", id);
        Incident incident = incidentRepositoryPort.findById(id)
                .orElseThrow(() -> new IncidentNotFoundException(id));
        incident.close();
        Incident updated = incidentRepositoryPort.save(incident);
        logger.info("Incident closed");
        return incidentMapper.toDto(updated);
    }

    @Transactional
    public IncidentDto updateIncident(Long id, IncidentDto incidentDto) {
        logger.info("Updating incident with id={}", id);
        Incident incident = incidentRepositoryPort.findById(id)
                .orElseThrow(() -> new IncidentNotFoundException(id));

        incident.update(
                incidentDto.getTitle(),
                incidentDto.getDescription(),
                incidentDto.getPriority(),
                incidentDto.getIncidentStatus()
        );

        Incident updated = incidentRepositoryPort.save(incident);

        logger.info("Incident updated at {}", updated.getUpdatedAt());
        return incidentMapper.toDto(updated);
    }
}
