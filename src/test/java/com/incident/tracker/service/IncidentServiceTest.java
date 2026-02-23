package com.incident.tracker.service;

import com.incident.tracker.domain.IncidentRequestDto;
import com.incident.tracker.domain.IncidentResponseDto;
import com.incident.tracker.exception.IncidentAlreadyClosedException;
import com.incident.tracker.exception.IncidentNotFoundException;
import com.incident.tracker.mapper.IncidentMapper;
import com.incident.tracker.model.Incident;
import com.incident.tracker.model.IncidentStatus;
import com.incident.tracker.repository.IncidentRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

import org.mockito.Mockito;

@ExtendWith(MockitoExtension.class)
class IncidentServiceTest {

    @Mock
    private IncidentRepository incidentRepository;

    @Mock
    private IncidentMapper mapper;

    @InjectMocks
    private IncidentService incidentService;

    @Nested
    @DisplayName("Méthode createIncident")
    class CreateIncident {

        @Test
        @DisplayName("Doit mapper, sauvegarder et retourner le DTO de réponse")
        void shouldCreateAndReturnDto() {
            // Given
            var request = new IncidentRequestDto("Titre", "Desc", "HIGH");
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
    @DisplayName("Méthode getAllIncidents")
    class GetAllIncidents {

        @Test
        @DisplayName("Doit retourner une liste de DTOs quand des incidents existent")
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
        @DisplayName("Doit retourner une liste vide si aucun incident en base")
        void shouldReturnEmptyList() {
            Mockito.when(incidentRepository.findAll()).thenReturn(Collections.emptyList());

            var results = incidentService.getAllIncidents();

            Assertions.assertThat(results).isEmpty();
        }
    }

    @Nested
    @DisplayName("Méthode closeIncident")
    class CloseIncident {

        @Test
        @DisplayName("Succès : Change le statut en CLOSED et sauvegarde")
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
        @DisplayName("Erreur : Lance une RuntimeException si l'ID n'existe pas")
        void shouldThrowExceptionWhenNotFound() {
            Mockito.when(incidentRepository.findById(1L)).thenReturn(Optional.empty());

            Assertions.assertThatThrownBy(() -> incidentService.closeIncident(1L))
                    .isExactlyInstanceOf(IncidentNotFoundException.class)
                    .hasMessageContaining("Incident not found with id: 1");

            Mockito.verify(incidentRepository, Mockito.never()).save(any());
        }

        @Test
        @DisplayName("Erreur : Lance une IllegalStateException si l'incident est déjà CLOSED")
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
}