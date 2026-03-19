package com.incident.tracker.service;

import com.incident.tracker.application.service.DatabaseResetService;
import com.incident.tracker.infrastruture.persistence.IncidentRepository;
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
    private IncidentRepository incidentRepository;

    @InjectMocks
    private DatabaseResetService databaseResetService;

    @Test
    @DisplayName("Doit appeler deleteAll sur le repository")
    void shouldCallDeleteAllOnRepository() {
        databaseResetService.resetDatabase();

        Mockito.verify(incidentRepository, Mockito.times(1)).deleteAll();
    }

    @Test
    @DisplayName("Doit remonter l'exception si deleteAll échoue")
    void shouldPropagateExceptionWhenDeleteAllFails() {
        Mockito.doThrow(new RuntimeException("db error")).when(incidentRepository).deleteAll();

        Assertions.assertThatThrownBy(() -> databaseResetService.resetDatabase())
                .isExactlyInstanceOf(RuntimeException.class)
                .hasMessageContaining("db error");

        Mockito.verify(incidentRepository, Mockito.times(1)).deleteAll();
    }
}

