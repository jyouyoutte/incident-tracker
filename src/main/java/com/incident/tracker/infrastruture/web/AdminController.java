package com.incident.tracker.infrastruture.web;

import com.incident.tracker.application.service.DatabaseResetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//@Profile("dev")
@RestController
@RequestMapping("/api/admin")
public class AdminController {
    // ...existing code...
    // Remplacement: use explicit class reference for logger
    private final Logger logger = LoggerFactory.getLogger(AdminController.class);
    private final DatabaseResetService databaseResetService;

    public AdminController(DatabaseResetService databaseResetService) {
        this.databaseResetService = databaseResetService;
    }

    @DeleteMapping("/reset")
    public ResponseEntity<String> resetDatabase() {
        logger.info("HTTP DELETE /api/admin/reset - Resetting database");
        databaseResetService.resetDatabase();
        return ResponseEntity.ok("Database reset");
    }
}
