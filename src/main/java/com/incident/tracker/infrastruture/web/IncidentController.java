package com.incident.tracker.infrastruture.web;

import com.incident.tracker.application.dto.incident.IncidentPatchRequestDto;
import com.incident.tracker.application.dto.incident.IncidentRequestDto;
import com.incident.tracker.application.dto.incident.IncidentResponseDto;
import com.incident.tracker.application.service.IncidentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/incidents")
@Tag(name = "Incidents", description = "API for incident lifecycle management")
public class IncidentController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final IncidentService service;

    public IncidentController(IncidentService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Create a new incident", description = "Creates a new incident from the request payload")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Incident created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error on request payload")
    })
    public IncidentResponseDto create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Incident payload. Allowed values: priority = [LOW, MODERATE, HIGH]; status = [OPEN, IN_PROGRESS, CLOSED]",
                required = true,
                content = @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema = @io.swagger.v3.oas.annotations.media.Schema(
                        implementation = IncidentRequestDto.class,
                        example = "{ \"title\": \"1ere anomalie\", \"description\": \"test swagger\", \"priority\": \"MODERATE\", \"status\": \"OPEN\" }"
                    )
                )
            )
            @Valid @RequestBody IncidentRequestDto request) {
        logger.info("HTTP POST /api/incidents - Creating new incident with title={}", request.getTitle());
        return service.createIncident(request);
    }

    @GetMapping
    @Operation(summary = "Get all incidents", description = "Returns the list of all incidents")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Incidents fetched successfully")
    })
    public List<IncidentResponseDto> getAll() {
        logger.info("HTTP GET /api/incidents - Fetching all incidents");
        return service.getAllIncidents();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get incident by ID", description = "Returns a single incident by its identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Incident found"),
            @ApiResponse(responseCode = "404", description = "Incident not found")
    })
    public IncidentResponseDto getById(
            @Parameter(description = "Incident ID", example = "1")
            @PathVariable Long id) {
        logger.info("HTTP GET /api/incidents/{}", id);
        return service.getIncidentById(id);
    }

    @PostMapping("/{id}/close")
    @Operation(summary = "Close an incident", description = "Closes an incident by its identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Incident closed successfully"),
            @ApiResponse(responseCode = "404", description = "Incident not found")
    })
    public IncidentResponseDto close(
            @Parameter(description = "Incident ID", example = "1")
            @PathVariable Long id) {
        logger.info("HTTP POST /api/incidents/{}/close", id);
        return service.closeIncident(id);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update an incident", description = "Partially updates an incident by its identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Incident updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error on request payload"),
            @ApiResponse(responseCode = "404", description = "Incident not found")
    })
    public IncidentResponseDto update(
            @Parameter(description = "Incident ID", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody IncidentPatchRequestDto request) {
        logger.info("HTTP PATCH /api/incidents/{} - Updating incident with title={}", id, request.getTitle());
        return service.updateIncident(id, request);
    }
}
