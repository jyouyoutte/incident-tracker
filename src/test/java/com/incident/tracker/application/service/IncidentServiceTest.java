package com.incident.tracker.application.service;

import com.incident.tracker.incident.application.dto.IncidentPatchRequestDto;
import com.incident.tracker.incident.application.dto.IncidentRequestDto;
import com.incident.tracker.incident.application.dto.IncidentResponseDto;
import com.incident.tracker.incident.application.IncidentService;
import com.incident.tracker.incident.domain.exception.IncidentAlreadyClosedException;
import com.incident.tracker.incident.domain.exception.IncidentNotFoundException;
import com.incident.tracker.domain.port.IncidentRepositoryPort;
import com.incident.tracker.mapper.IncidentMapper;
import com.incident.tracker.incident.infrastructure.persistence.entity.Incident;
import com.incident.tracker.incident.infrastructure.persistence.entity.IncidentStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class IncidentServiceTest {

    @Mock
    private IncidentRepositoryPort incidentRepository;

    @Mock
    private IncidentMapper mapper;

    @InjectMocks
    private IncidentService incidentService;

    @Nested
    @DisplayName("Method createIncident")
    class CreateIncident {

        @Test
        @DisplayName("Should map, save and return the response DTO")
        void shouldCreateAndReturnDto() {
            // Given
            var request = new IncidentRequestDto();
            request.setTitle("Titre");
            request.setDescription("Desc");
            request.setPriority("HIGH");
            request.setStatus("OPEN");
            var incident = new Incident();
            var savedIncident = new Incident();
            var response = new IncidentResponseDto(1L, "Titre", "Desc", "HIGH", "OPEN", LocalDateTime.now(), null);

            Mockito.when(mapper.toEntity(request)).thenReturn(incident);
            Mockito.when(incidentRepository.save(incident)).thenReturn(savedIncident);
            Mockito.when(mapper.toResponse(savedIncident)).thenReturn(response);

            // When
            var result = incidentService.createIncident(request);

            // Then
            Assertions.assertThat(result).isEqualTo(response);
            Mockito.verify(incidentRepository, Mockito.times(1)).save(any(Incident.class));
        }
    }

    @Nested
    @DisplayName("Method getAllIncidents")
    class GetAllIncidents {

        @Test
        @DisplayName("Should return a list of DTOs when incidents exist")
        void shouldReturnListOfDtos() {
            // Given
            var incident = new Incident();
            var response = new IncidentResponseDto(1L, "T", "D", "H", "OPEN", null, null);

            Mockito.when(incidentRepository.findAll()).thenReturn(List.of(incident));
            Mockito.when(mapper.toResponse(incident)).thenReturn(response);

            // When
            var results = incidentService.getAllIncidents();

            // Then
            Assertions.assertThat(results).hasSize(1).containsExactly(response);
        }

        @Test
        @DisplayName("Should return an empty list if no incident in database")
        void shouldReturnEmptyList() {
            Mockito.when(incidentRepository.findAll()).thenReturn(Collections.emptyList());

            var results = incidentService.getAllIncidents();

            Assertions.assertThat(results).isEmpty();
        }
    }

    @Nested
    @DisplayName("Method getIncidentById")
    class GetIncidentById {

        @Test
        @DisplayName("Should return a response DTO when incident exists")
        void shouldReturnDtoWhenFound() {
            // Given
            Long id = 1L;
            var incident = new Incident();
            var response = new IncidentResponseDto(id, "T", "D", "H", "OPEN", null, null);

            Mockito.when(incidentRepository.findById(id)).thenReturn(Optional.of(incident));
            Mockito.when(mapper.toResponse(incident)).thenReturn(response);

            // When
            var result = incidentService.getIncidentById(id);

            // Then
            Assertions.assertThat(result).isEqualTo(response);
        }

        @Test
        @DisplayName("Should throw IncidentNotFoundException when not present")
        void shouldThrowWhenNotFound() {
            Long id = 2L;
            Mockito.when(incidentRepository.findById(id)).thenReturn(Optional.empty());

            Assertions.assertThatThrownBy(() -> incidentService.getIncidentById(id))
                    .isExactlyInstanceOf(IncidentNotFoundException.class)
                    .hasMessageContaining("Incident not found with id: " + id);
        }
    }

    @Nested
    @DisplayName("Method closeIncident")
    class CloseIncident {

        @Test
        @DisplayName("Success: Change status to CLOSED and save")
        void shouldCloseIncidentSuccessfully() {
            // Given
            Long id = 1L;
            var incident = new Incident();
            incident.setStatus(IncidentStatus.OPEN);

            var updatedIncident = new Incident();
            updatedIncident.setStatus(IncidentStatus.CLOSED);

            var response = new IncidentResponseDto(id, "T", "D", "H", "CLOSED", null, null);

            Mockito.when(incidentRepository.findById(id)).thenReturn(Optional.of(incident));
            Mockito.when(incidentRepository.save(incident)).thenReturn(updatedIncident);
            Mockito.when(mapper.toResponse(updatedIncident)).thenReturn(response);

            // When
            var result = incidentService.closeIncident(id);

            // Then
            Assertions.assertThat(result.status()).isEqualTo("CLOSED");
            Assertions.assertThat(incident.getStatus()).isEqualTo(IncidentStatus.CLOSED);
            Mockito.verify(incidentRepository).save(incident);
        }

        @Test
        @DisplayName("Error: Throw IncidentNotFoundException if the ID does not exist")
        void shouldThrowExceptionWhenNotFound() {
            Mockito.when(incidentRepository.findById(1L)).thenReturn(Optional.empty());

            Assertions.assertThatThrownBy(() -> incidentService.closeIncident(1L))
                    .isExactlyInstanceOf(IncidentNotFoundException.class)
                    .hasMessageContaining("Incident not found with id: 1");

            Mockito.verify(incidentRepository, Mockito.never()).save(any());
        }

        @Test
        @DisplayName("Error: Throw IncidentAlreadyClosedException if the incident is already CLOSED")
        void shouldThrowExceptionWhenAlreadyClosed() {
            // Given
            var incident = new Incident();
            incident.setStatus(IncidentStatus.CLOSED);
            Mockito.when(incidentRepository.findById(1L)).thenReturn(Optional.of(incident));

            // When & Then
            Assertions.assertThatThrownBy(() -> incidentService.closeIncident(1L))
                    .isExactlyInstanceOf(IncidentAlreadyClosedException.class)
                    .hasMessageContaining("Incident with id " + 1 + " is already closed");

            Mockito.verify(incidentRepository, Mockito.never()).save(any());
        }
    }

    @Nested
    @DisplayName("Method updateIncident")
    class UpdateIncident {
        @Test
        @DisplayName("Update an incident successfully, must use mapper to update fields, save and return an updated incident" )
        void shouldUpdateIncidentSuccessfully() {
            // Given
            Long id = 1L;
            var request = new IncidentPatchRequestDto();
            request.setTitle("New Title");
            request.setDescription("New Desc");
            request.setPriority("MODERATE");
            request.setStatus("OPEN");
            var incident = new Incident();
            incident.setStatus(IncidentStatus.OPEN);

            var updatedIncident = new Incident();
            updatedIncident.setStatus(IncidentStatus.IN_PROGRESS);

            var response = new IncidentResponseDto( id,"New Title", "New Desc", "MODERATE", "IN_PROGRESS", null, LocalDateTime.now());

            Mockito.when(incidentRepository.findById(id)).thenReturn(Optional.of(incident));
            Mockito.when(incidentRepository.save(incident)).thenReturn(updatedIncident);
            Mockito.when(mapper.toResponse(updatedIncident)).thenReturn(response);

            // When
            var result = incidentService.updateIncident(id, request);

            // Then
            Assertions.assertThat(result).isEqualTo(response);
            Mockito.verify(incidentRepository).save(incident);
        }
        @Test
        @DisplayName("Update an incident that does not exist, must throw an IncidentNotFoundException")
        void should_throw_an_incident_not_found_exception_when_update_a_non_existing_incident() {
            // Given
            Long id = 2L;
            var request = new IncidentPatchRequestDto();
            request.setTitle("New Title");
            request.setDescription("New Desc");
            request.setPriority("MODERATE");
            request.setStatus("OPEN");
            var incident = new Incident();
            incident.setStatus(IncidentStatus.OPEN);

            // When
            Mockito.when(incidentRepository.findById(id)).thenReturn(Optional.empty());

            Assertions.assertThatThrownBy(() -> incidentService.updateIncident(id, request))
                    .isExactlyInstanceOf(IncidentNotFoundException.class)
                    .hasMessageContaining("Incident not found with id: 2");

            // Then
            Mockito.verify(incidentRepository, Mockito.only()).findById(id);
            Mockito.verify(incidentRepository, Mockito.never()).save(any());
        }



        @Test
        @DisplayName("Update an incident that is already, must throw an IncidentAlreadyClosedException")
        void should_throw_an_already_closed_exception_when_update_a_closed_incident() {
            // Given
            Long id = 1L;
            var request = new IncidentPatchRequestDto();
            request.setTitle("New Title");
            request.setDescription("New Desc");
            request.setPriority("MODERATE");
            request.setStatus("CLOSED");
            var incident = new Incident();
            incident.setStatus(IncidentStatus.CLOSED);

            // When
            Mockito.when(incidentRepository.findById(id)).thenReturn(Optional.of(incident));

            Assertions.assertThatThrownBy(() -> incidentService.updateIncident(id, request))
                    .isExactlyInstanceOf(IncidentAlreadyClosedException.class)
                    .hasMessageContaining("Incident with id " + id + " is already closed");

            // Then
            Mockito.verify(incidentRepository, Mockito.only()).findById(id);
            Mockito.verify(incidentRepository, Mockito.never()).save(any());
        }
    }
}

