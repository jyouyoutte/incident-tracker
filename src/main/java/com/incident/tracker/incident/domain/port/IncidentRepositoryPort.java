package com.incident.tracker.incident.domain.port;

import com.incident.tracker.incident.domain.model.Incident;
import com.incident.tracker.incident.domain.model.IncidentStatus;

import java.util.List;
import java.util.Optional;

public interface IncidentRepositoryPort {
    Incident save(Incident incident);
    Optional<Incident> findById(Long id);
    List<Incident> findAll();
    List<Incident> findByIncidentStatus(IncidentStatus status);
    void deleteAll();
}
