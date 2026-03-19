package com.incident.tracker.domain.port;

import com.incident.tracker.domain.model.Incident;
import com.incident.tracker.domain.model.IncidentStatus;

import java.util.List;
import java.util.Optional;

public interface IncidentRepositoryPort {
    Incident save(Incident incident);
    Optional<Incident> findById(Long id);
    List<Incident> findAll();
    List<Incident> findByStatus(IncidentStatus status);
    void deleteAll();
}
