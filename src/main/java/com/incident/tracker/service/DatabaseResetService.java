package com.incident.tracker.service;

import com.incident.tracker.repository.IncidentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DatabaseResetService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final IncidentRepository incidentRepository;

    public DatabaseResetService(IncidentRepository incidentRepository) {
        this.incidentRepository = incidentRepository;
    }

    public void resetDatabase() {
        logger.info("delete all incidents");
        incidentRepository.deleteAll();
    }
}
