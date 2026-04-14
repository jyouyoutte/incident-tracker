package com.incident.tracker.incident.application;

import com.incident.tracker.incident.domain.port.IncidentRepositoryPort;
import com.incident.tracker.incident.application.service.DatabaseResetService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DatabaseResetServiceTest {

    @Mock
    private IncidentRepositoryPort incidentRepository;

    @InjectMocks
    private DatabaseResetService databaseResetService;

    @Test
    @DisplayName("Should call deleteAll on the repository")
    void shouldCallDeleteAllOnRepository() {
        databaseResetService.resetDatabase();

        Mockito.verify(incidentRepository, Mockito.times(1)).deleteAll();
    }

    @Test
    @DisplayName("Should propagate the exception if deleteAll fails")
    void shouldPropagateExceptionWhenDeleteAllFails() {
        Mockito.doThrow(new RuntimeException("db error")).when(incidentRepository).deleteAll();

        Assertions.assertThatThrownBy(() -> databaseResetService.resetDatabase())
                .isExactlyInstanceOf(RuntimeException.class)
                .hasMessageContaining("db error");

        Mockito.verify(incidentRepository, Mockito.times(1)).deleteAll();
    }
}

