package com.incident.tracker.incident.infrastructure.web.controller;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Profile("dev")
@RestController
@RequestMapping("/api/admin")
@Tag(name = "Administration", description = "Administration endpoints (dev profile)")
public interface AdminIncidentController {

    @DeleteMapping("/reset")
    @Operation(summary = "Reset the database", description = "Deletes and recreates initial data. Accessible only when the 'dev' profile is active.")
    ResponseEntity<String> resetDatabase();
}
