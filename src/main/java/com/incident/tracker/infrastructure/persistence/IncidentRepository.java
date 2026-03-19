package com.incident.tracker.infrastructure.persistence;

import com.incident.tracker.domain.model.Incident;
import com.incident.tracker.domain.model.IncidentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IncidentRepository extends JpaRepository<Incident, Long> {
    List<Incident> findByStatus(IncidentStatus status);
}
