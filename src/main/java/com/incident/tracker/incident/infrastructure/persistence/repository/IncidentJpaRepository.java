package com.incident.tracker.incident.infrastructure.persistence.repository;

import com.incident.tracker.incident.infrastructure.persistence.entity.IncidentEntity;
import com.incident.tracker.incident.infrastructure.persistence.entity.IncidentStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IncidentJpaRepository extends JpaRepository<IncidentEntity, Long> {
    List<IncidentEntity> findByIncidentStatusEntity(IncidentStatusEntity status);
}
