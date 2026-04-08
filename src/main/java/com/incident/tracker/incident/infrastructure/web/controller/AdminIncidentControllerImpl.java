package com.incident.tracker.incident.infrastructure.web.controller;

import com.incident.tracker.incident.application.service.DatabaseResetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class AdminIncidentControllerImpl implements AdminIncidentController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final DatabaseResetService databaseResetService;

    public AdminIncidentControllerImpl(DatabaseResetService databaseResetService) {
        this.databaseResetService = databaseResetService;
    }

    public ResponseEntity<String> resetDatabase() {
        logger.info("HTTP DELETE /api/admin/reset - Resetting database");
        databaseResetService.resetDatabase();
        return ResponseEntity.ok("Database reset");
    }
}
