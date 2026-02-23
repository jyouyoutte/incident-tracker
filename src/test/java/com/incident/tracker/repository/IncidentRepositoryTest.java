package com.incident.tracker.repository;

import com.incident.tracker.model.Incident;
import com.incident.tracker.model.IncidentStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

@DataJpaTest // Charge uniquement les composants JPA et configure une base H2
public class IncidentRepositoryTest {

    @Autowired
    private IncidentRepository incidentRepository;

    @Test
    void shouldFindByStatus(){
        // Given
        Incident openIncident = new Incident();
        openIncident.setTitle("Connection Failed");
        openIncident.setStatus(IncidentStatus.OPEN);

        Incident closedIncident = new Incident();
        closedIncident.setTitle("Duplicated lines");
        closedIncident.setStatus(IncidentStatus.CLOSED);

        incidentRepository.saveAll(List.of(openIncident, closedIncident));

        //When
        List<Incident> results = incidentRepository.findByStatus(IncidentStatus.OPEN);

        //Then
        Assertions.assertThat(results)
                .hasSize(1)
                .extracting(Incident::getStatus)
                .containsExactly(IncidentStatus.OPEN);
    }
}
