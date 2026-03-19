package com.incident.tracker.infrastructure.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.incident.tracker.application.dto.incident.IncidentPatchRequestDto;
import com.incident.tracker.application.dto.incident.IncidentRequestDto;
import com.incident.tracker.application.dto.incident.IncidentResponseDto;
import com.incident.tracker.domain.exception.IncidentAlreadyClosedException;
import com.incident.tracker.domain.exception.IncidentNotFoundException;
import com.incident.tracker.application.service.IncidentService;
import com.incident.tracker.infrastruture.web.IncidentController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(IncidentController.class)
class IncidentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IncidentService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/incidents - Success")
    void shouldCreateIncident() throws Exception {

        IncidentRequestDto request = new IncidentRequestDto();
        request.setTitle("Bug login");
        request.setDescription("Impossible login");
        request.setPriority("HIGH");
        request.setStatus("OPEN");

        IncidentResponseDto response =
                new IncidentResponseDto(1L, "Bug login", "Impossible login","HIGH","OPEN", LocalDateTime.now(), LocalDateTime.now());

        when(service.createIncident(any())).thenReturn(response);

        mockMvc.perform(post("/api/incidents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Bug login"))
                .andExpect(jsonPath("$.description").value("Impossible login"))
                .andExpect(jsonPath("$.priority").value("HIGH"))
                .andExpect(jsonPath("$.status").value("OPEN"));
    }

    @Test
    @DisplayName("POST /api/incidents - Error 400 in case of empty Title")
    void shouldReturn400WhenTitleIsEmpty() throws Exception {
        var invalidRequest = new IncidentRequestDto();
        invalidRequest.setTitle("");
        invalidRequest.setDescription("Impossible login");
        invalidRequest.setPriority("HIGH");
        invalidRequest.setStatus("OPEN");

        // When & Then
        mockMvc.perform(post("/api/incidents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/incidents - find all incidents")
    void shouldGetAllIncidents() throws Exception {
        // Given
        var response1 = new IncidentResponseDto(1L, "No connection", "D", "H", "OPEN", null, null);
        var response2 = new IncidentResponseDto(2L, "No incidents displayed", "D", "H", "OPEN", null, null);

        when(service.getAllIncidents()).thenReturn(List.of(response1, response2));

        // When & Then
        mockMvc.perform(get("/api/incidents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @DisplayName("GET /api/incidents/{id} - Success")
    void shouldGetIncidentById() throws Exception {
        Long id = 1L;
        var response = new IncidentResponseDto(id, "Bug login", "Impossible login", "HIGH", "OPEN", null, LocalDateTime.now());

        when(service.getIncidentById(id)).thenReturn(response);

        mockMvc.perform(get("/api/incidents/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.title").value("Bug login"));
    }

    @Test
    @DisplayName("GET /api/incidents/{id} - Not Found")
    void shouldReturn404WhenNotFound() throws Exception {
        Long id = 2L;
        when(service.getIncidentById(id)).thenThrow(new IncidentNotFoundException(id));

        mockMvc.perform(get("/api/incidents/2")).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/incidents/{id}/close - Success in case of closed incident")
    void shouldCloseIncident() throws Exception {
        // Given
        var response = new IncidentResponseDto(1L, "T", "D", "H", "CLOSED", null, null);
        when(service.closeIncident(1L)).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/incidents/1/close"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CLOSED"));
    }

    @Test
    @DisplayName("POST /api/incidents - Error 400 in case of already not found incident")
    void shouldReturn400WhenIncidentNotFound() throws Exception {
        when(service.closeIncident(1L)).thenThrow(new IncidentNotFoundException(1L));

        // When & Then
        mockMvc.perform(post("/api/incidents/1/close")).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/incidents - Error 409 in case of already closed incident")
    void shouldReturn409WhenIncidentAlreadyClosed() throws Exception {
        when(service.closeIncident(1L)).thenThrow(new IncidentAlreadyClosedException(1L));

        // When & Then
        mockMvc.perform(post("/api/incidents/1/close")).andExpect(status().isConflict());
    }
    @Test
    @DisplayName("PATCH /api/incidents/{id} - Success in case of updated incident")
    void shouldUpdateIncident() throws Exception {
        // Given
        Long id = 1L;
        var response = new IncidentResponseDto(id, "Bug login", "Impossible login", "HIGH", "IN_PROGRESS", null, LocalDateTime.now());
        IncidentPatchRequestDto request = new IncidentPatchRequestDto();
        request.setTitle("Bug login");
        request.setDescription("Impossible login");
        request.setPriority("HIGH");
        request.setStatus("IN_PROGRESS");

        when(service.updateIncident(eq(id), any(IncidentPatchRequestDto.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(patch("/api/incidents/1", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.title").value("Bug login"));
    }

}