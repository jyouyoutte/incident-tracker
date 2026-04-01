package com.incident.tracker.incident.infrastructure.persistence.repository;

import com.incident.tracker.incident.infrastructure.persistence.entity.Incident;
import com.incident.tracker.incident.infrastructure.persistence.entity.IncidentStatus;
import com.incident.tracker.domain.port.IncidentRepositoryPort;
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
    public Incident save(Incident incident) {
        return incidentRepository.save(incident);
    }

    @Override
    public Optional<Incident> findById(Long id) {
        return incidentRepository.findById(id);
    }

    @Override
    public List<Incident> findAll() {
        return incidentRepository.findAll();
    }

    @Override
    public List<Incident> findByStatus(IncidentStatus status) {
        return incidentRepository.findByStatus(status);
    }

    @Override
    public void deleteAll() {
         incidentRepository.deleteAll();
    }
}
