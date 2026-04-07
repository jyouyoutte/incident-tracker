package com.incident.tracker.incident.application.service;

import com.incident.tracker.incident.domain.port.IncidentRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DatabaseResetService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final IncidentRepositoryPort incidentRepository;

    public DatabaseResetService(IncidentRepositoryPort incidentRepository) {
        this.incidentRepository = incidentRepository;
    }

    public void resetDatabase() {
        logger.info("delete all incidents");
        incidentRepository.deleteAll();
    }
}
