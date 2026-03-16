package com.incident.tracker.repository;

import com.incident.tracker.model.Incident;
import com.incident.tracker.model.IncidentStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

@DataJpaTest // Only loads JPA components and configures an H2 database
public class IncidentRepositoryTest {

    @Autowired
    private IncidentRepository incidentRepository;

    @BeforeEach
    void setUp() {
        incidentRepository.deleteAll(); // Ensure a clean state before each test
    }


    @Test
    void shouldFindByStatus(){
        // Given
        Incident openIncident = new Incident();
        openIncident.setTitle("Connection Failed");
        openIncident.setStatus(IncidentStatus.OPEN);

        incidentRepository.saveAll(List.of(openIncident));

        //When
        List<Incident> resultsByStatus = incidentRepository.findByStatus(IncidentStatus.OPEN);

        //Then
        Assertions.assertThat(resultsByStatus)
                .hasSize(1)
                .extracting(Incident::getStatus)
                .containsExactly(IncidentStatus.OPEN);
    }

    @Test
    void shouldFindAll(){
        // Given
        Incident openIncident1 = new Incident();
        openIncident1.setTitle("query test1");
        openIncident1.setStatus(IncidentStatus.OPEN);

        Incident openIncident2 = new Incident();
        openIncident2.setTitle("query test2");
        openIncident2.setStatus(IncidentStatus.OPEN);

        incidentRepository.saveAll(List.of(openIncident1, openIncident2));

        //When
        List<Incident> results= incidentRepository.findAll();

        //Then
        Assertions.assertThat(results)
                .hasSize(2);
    }
}
