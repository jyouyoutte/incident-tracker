package com.incident.tracker.incident.infrastructure.persistence.repository;

import com.incident.tracker.incident.infrastructure.persistence.entity.IncidentEntity;
import com.incident.tracker.incident.infrastructure.persistence.entity.IncidentStatusEntity;
import com.incident.tracker.incident.domain.port.IncidentRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class IncidentRepositoryJpa implements IncidentRepositoryPort {
    private final IncidentRepository incidentRepository;

    public IncidentRepositoryJpa(IncidentRepository incidentRepository) {
        this.incidentRepository = incidentRepository;
    }

    @Override
    public IncidentEntity save(IncidentEntity incident) {
        return incidentRepository.save(incident);
    }

    @Override
    public Optional<IncidentEntity> findById(Long id) {
        return incidentRepository.findById(id);
    }

    @Override
    public List<IncidentEntity> findAll() {
        return incidentRepository.findAll();
    }

    @Override
    public List<IncidentEntity> findByIncidentStatusEntity(IncidentStatusEntity status) {
        return incidentRepository.findByIncidentStatusEntity(status);
    }

    @Override
    public void deleteAll() {
         incidentRepository.deleteAll();
    }
}
