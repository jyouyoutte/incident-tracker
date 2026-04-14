package com.incident.tracker.incident.application;

import com.incident.tracker.incident.application.dto.IncidentDto;
import com.incident.tracker.incident.application.mapper.IncidentMapper;
import com.incident.tracker.incident.application.service.IncidentService;
import com.incident.tracker.incident.domain.exception.IncidentAlreadyClosedException;
import com.incident.tracker.incident.domain.exception.IncidentNotFoundException;
import com.incident.tracker.incident.domain.model.Incident;
import com.incident.tracker.incident.domain.model.IncidentStatus;
import com.incident.tracker.incident.domain.model.Priority;
import com.incident.tracker.incident.domain.port.IncidentRepositoryPort;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class IncidentServiceTest {

    @Mock
    private IncidentRepositoryPort incidentRepositoryPort;

    @Spy
    private IncidentMapper incidentMapper;

    @InjectMocks
    private IncidentService incidentService;

    @Nested
    @DisplayName("Method createIncident")
    class CreateIncident {

        @Test
        @DisplayName("Should map, save and return the response DTO")
        void shouldCreateAndReturnDto() {
            // Given
            LocalDateTime now = LocalDateTime.now();
            var incidentDto = IncidentDto.builder()
                    .title("Titre")
                    .description("Desc")
                    .priority("HIGH")
                    .incidentStatus("OPEN").build();

            var savedIncident = incidentMapper.toDomain(incidentDto);
            savedIncident.setId(1L);
            savedIncident.setCreatedAt(now);

            Mockito.when(incidentRepositoryPort.save(any(Incident.class))).thenReturn(savedIncident);

            // When
            var result = incidentService.createIncident(incidentDto);

            // Then
            Assertions.assertThat(result.getId()).isEqualTo(1L);
            Assertions.assertThat(result.getTitle()).isEqualTo(incidentDto.getTitle());
            Assertions.assertThat(result.getDescription()).isEqualTo(incidentDto.getDescription());
            Assertions.assertThat(result.getPriority()).isEqualTo(incidentDto.getPriority());
            Assertions.assertThat(result.getIncidentStatus()).isEqualTo(incidentDto.getIncidentStatus());

            Mockito.verify(incidentRepositoryPort, Mockito.times(1)).save(any(Incident.class));
        }
    }

    @Nested
    @DisplayName("Method getAllIncidents")
    class GetAllIncidents {

        @Test
        @DisplayName("Should return a list of DTOs when incidents exist")
        void shouldReturnListOfIncidents() {
            // Given
            var incident = new Incident("T", "D", Priority.P2);
            incident.setId(1L);

            Mockito.when(incidentRepositoryPort.findAll()).thenReturn(List.of(incident));

            // When
            var results = incidentService.getAllIncidents();

            // Then
            Assertions.assertThat(results).isNotNull().isNotEmpty().hasSize(1);
            Assertions.assertThat(results.getFirst().getId()).isEqualTo(incident.getId());
            Assertions.assertThat(results.getFirst().getDescription()).isEqualTo(incident.getDescription());
            Assertions.assertThat(results.getFirst().getPriority()).isEqualTo(incident.getPriority().getLabel());
        }

        @Test
        @DisplayName("Should return an empty list if no incident in database")
        void shouldReturnEmptyList() {
            Mockito.when(incidentRepositoryPort.findAll()).thenReturn(Collections.emptyList());

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
            var incident = new Incident("T", "D", Priority.P2);
            incident.setId(id);

            Mockito.when(incidentRepositoryPort.findById(id)).thenReturn(Optional.of(incident));

            // When
            var result = incidentService.getIncidentById(id);

            // Then
            Assertions.assertThat(result.getId()).isEqualTo(incident.getId());
            Assertions.assertThat(result.getTitle()).isEqualTo(incident.getTitle());
            Assertions.assertThat(result.getDescription()).isEqualTo(incident.getDescription());

        }

        @Test
        @DisplayName("Should throw IncidentNotFoundException when not present")
        void shouldThrowWhenNotFound() {
            Long id = 2L;
            Mockito.when(incidentRepositoryPort.findById(id)).thenReturn(Optional.empty());

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
            var incident = new Incident("T", "D", Priority.P2);
            incident.setId(id);
            incident.setIncidentStatus(IncidentStatus.OPEN);

            var updatedIncident = new Incident("T", "D", Priority.P2);
            updatedIncident.setIncidentStatus(IncidentStatus.CLOSED);
            updatedIncident.setId(id);


            Mockito.when(incidentRepositoryPort.findById(id)).thenReturn(Optional.of(incident));
            Mockito.when(incidentRepositoryPort.save(incident)).thenReturn(updatedIncident);

            // When
            var result = incidentService.closeIncident(id);

            // Then
            Assertions.assertThat(result.getIncidentStatus()).isEqualTo("CLOSED");
            Assertions.assertThat(incident.getIncidentStatus()).isEqualTo(IncidentStatus.CLOSED);
            Assertions.assertThat(result.getId()).isEqualTo(id);
            Mockito.verify(incidentRepositoryPort).save(incident);
        }

        @Test
        @DisplayName("Error: Throw IncidentNotFoundException if the ID does not exist")
        void shouldThrowExceptionWhenNotFound() {
            Mockito.when(incidentRepositoryPort.findById(1L)).thenReturn(Optional.empty());

            Assertions.assertThatThrownBy(() -> incidentService.closeIncident(1L))
                    .isExactlyInstanceOf(IncidentNotFoundException.class)
                    .hasMessageContaining("Incident not found with id: 1");

            Mockito.verify(incidentRepositoryPort, Mockito.never()).save(any());
        }

        @Test
        @DisplayName("Error: Throw IncidentAlreadyClosedException if the incident is already CLOSED")
        void shouldThrowExceptionWhenAlreadyClosed() {
            // Given
            var incident = new Incident("T", "D", Priority.P2);
            incident.setId(1L);
            incident.setIncidentStatus(IncidentStatus.CLOSED);
            Mockito.when(incidentRepositoryPort.findById(1L)).thenReturn(Optional.of(incident));

            // When & Then
            Assertions.assertThatThrownBy(() -> incidentService.closeIncident(1L))
                    .isExactlyInstanceOf(IncidentAlreadyClosedException.class)
                    .hasMessageContaining("Incident with id " + 1 + " is already closed");

            Mockito.verify(incidentRepositoryPort, Mockito.never()).save(any());
        }
    }

    @Nested
    @DisplayName("Method updateIncident")
    class UpdateIncident {
        @Test
        @DisplayName("Update an incident successfully, must use mapper to update fields, save and return an updated incident" )
        void shouldUpdateIncidentSuccessfully() {
            // Given
            LocalDateTime now = LocalDateTime.now();
            Long id = 1L;
            var incidentDto = IncidentDto.builder()
            .title("New Title")
                    .description("New Desc")
                    .priority("MODERATE")
            .incidentStatus("OPEN").build();
            var incident = new Incident("New Title", "New Desc", Priority.P3);
            incident.setIncidentStatus(IncidentStatus.OPEN);

            var updatedIncident = new Incident("New Title", "New Desc", Priority.P3);
            updatedIncident.setId(id);
            updatedIncident.setIncidentStatus(IncidentStatus.IN_PROGRESS);
            updatedIncident.setUpdatedAt(now);


            Mockito.when(incidentRepositoryPort.findById(id)).thenReturn(Optional.of(incident));
            Mockito.when(incidentRepositoryPort.save(incident)).thenReturn(updatedIncident);

            // When
            var result = incidentService.updateIncident(id, incidentDto);

            // Then
            Assertions.assertThat(result).isNotNull();
            Assertions.assertThat(result.getId()).isEqualTo(id);
            Assertions.assertThat(result).isNotNull();
            Assertions.assertThat(result).isNotNull();

            Mockito.verify(incidentRepositoryPort).save(incident);
        }
        @Test
        @DisplayName("Update an incident that does not exist, must throw an IncidentNotFoundException")
        void should_throw_an_incident_not_found_exception_when_update_a_non_existing_incident() {
            // Given
            Long id = 2L;
            var incidentPatch = IncidentDto.builder()
            .title("New Title")
                    .description("New Desc")
                    .priority("MODERATE")
                    .incidentStatus("OPEN").build();

            var incident = new Incident("New Title", "New Desc", Priority.P3);
            incident.setIncidentStatus(IncidentStatus.OPEN);

            // When
            Mockito.when(incidentRepositoryPort.findById(id)).thenReturn(Optional.empty());

            Assertions.assertThatThrownBy(() -> incidentService.updateIncident(id, incidentPatch))
                    .isExactlyInstanceOf(IncidentNotFoundException.class)
                    .hasMessageContaining("Incident not found with id: 2");

            // Then
            Mockito.verify(incidentRepositoryPort, Mockito.only()).findById(id);
            Mockito.verify(incidentRepositoryPort, Mockito.never()).save(any());
        }



        @Test
        @DisplayName("Update an incident that is already, must throw an IncidentAlreadyClosedException")
        void should_throw_an_already_closed_exception_when_update_a_closed_incident() {
            // Given
            Long id = 1L;
            var request = IncidentDto.builder()
            .title("New Title")
            .description("New Desc").priority("MODERATE")
            .incidentStatus("CLOSED").build();

            var incident = new Incident(request.getTitle(), request.getDescription(), Priority.P3);
            incident.setId(id);
            incident.setIncidentStatus(IncidentStatus.CLOSED);

            // When
            Mockito.when(incidentRepositoryPort.findById(id)).thenReturn(Optional.of(incident));

            Assertions.assertThatThrownBy(() -> incidentService.updateIncident(id, request))
                    .isExactlyInstanceOf(IncidentAlreadyClosedException.class)
                    .hasMessageContaining("Incident with id " + id + " is already closed");

            // Then
            Mockito.verify(incidentRepositoryPort, Mockito.only()).findById(id);
            Mockito.verify(incidentRepositoryPort, Mockito.never()).save(any());
        }
    }
}

