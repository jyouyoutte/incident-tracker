package com.incident.tracker.infrastruture.persistence;

import com.incident.tracker.domain.model.Incident;
import com.incident.tracker.domain.model.IncidentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncidentRepository extends JpaRepository<Incident, Long> {
    List<Incident> findByStatus(IncidentStatus status);
}
