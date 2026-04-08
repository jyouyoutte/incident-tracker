package com.incident.tracker.incident.infrastructure.web.controller;

import com.incident.tracker.incident.infrastructure.web.vo.IncidentPatchRequestVo;
import com.incident.tracker.incident.infrastructure.web.vo.IncidentRequestVo;
import com.incident.tracker.incident.infrastructure.web.vo.IncidentResponseVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/incidents")
@Tag(name = "Incidents", description = "API for incident lifecycle management")
public interface IncidentController {

    @PostMapping
    @Operation(summary = "Create a new incident", description = "Creates a new incident from the request payload")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Incident created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error on request payload")
    })

    IncidentResponseVo create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Incident payload. Allowed values: priority = [LOW, MODERATE, HIGH]; status = [OPEN, IN_PROGRESS, CLOSED]",
                required = true,
                content = @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema = @io.swagger.v3.oas.annotations.media.Schema(
                        implementation = IncidentRequestVo.class,
                        example = "{ \"title\": \"1ere anomalie\", \"description\": \"test swagger\", \"priority\": \"MODERATE\", \"status\": \"OPEN\" }"
                    )
                )
            )
            @Valid @RequestBody IncidentRequestVo request);

    @GetMapping
    @Operation(summary = "Get all incidents", description = "Returns the list of all incidents")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Incidents fetched successfully")
    })
    List<IncidentResponseVo> getAll();

    @GetMapping("/{id}")
    @Operation(summary = "Get incident by ID", description = "Returns a single incident by its identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Incident found"),
            @ApiResponse(responseCode = "404", description = "Incident not found")
    })
    IncidentResponseVo getById(
            @Parameter(description = "Incident ID", example = "1")
            @PathVariable Long id);

    @PostMapping("/{id}/close")
    @Operation(summary = "Close an incident", description = "Closes an incident by its identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Incident closed successfully"),
            @ApiResponse(responseCode = "404", description = "Incident not found")
    })

    IncidentResponseVo close(
            @Parameter(description = "Incident ID", example = "1")
            @PathVariable Long id);

    @PatchMapping("/{id}")
    @Operation(summary = "Update an incident", description = "Partially updates an incident by its identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Incident updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error on request payload"),
            @ApiResponse(responseCode = "404", description = "Incident not found")
    })

    IncidentResponseVo update(
            @Parameter(description = "Incident ID", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody IncidentPatchRequestVo request);
}
