package com.incident.tracker.incident.domain.port;

import com.incident.tracker.incident.infrastructure.persistence.entity.IncidentEntity;
import com.incident.tracker.incident.infrastructure.persistence.entity.IncidentStatusEntity;

import java.util.List;
import java.util.Optional;

public interface IncidentRepositoryPort {
    IncidentEntity save(IncidentEntity incident);
    Optional<IncidentEntity> findById(Long id);
    List<IncidentEntity> findAll();
    List<IncidentEntity> findByIncidentStatusEntity(IncidentStatusEntity status);
    void deleteAll();
}
