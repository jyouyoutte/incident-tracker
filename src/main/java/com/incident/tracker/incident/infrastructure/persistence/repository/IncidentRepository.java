package com.incident.tracker.incident.infrastructure.persistence.repository;

import com.incident.tracker.incident.infrastructure.persistence.entity.Incident;
import com.incident.tracker.incident.infrastructure.persistence.entity.IncidentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IncidentRepository extends JpaRepository<Incident, Long> {
    List<Incident> findByStatus(IncidentStatus status);
}
