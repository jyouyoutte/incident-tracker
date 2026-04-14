package com.incident.tracker.incident.infrastructure;

import com.incident.tracker.incident.infrastructure.persistence.entity.IncidentEntity;
import com.incident.tracker.incident.infrastructure.persistence.entity.IncidentStatusEntity;
import com.incident.tracker.incident.infrastructure.persistence.repository.IncidentJpaRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
@ActiveProfiles("test")
@DataJpaTest // Only loads JPA components and configures an H2 database
public class IncidentRepositoryTest {

    @Autowired
    private IncidentJpaRepository incidentRepository;

    @BeforeEach
    void setUp() {
        incidentRepository.deleteAll(); // Ensure a clean state before each test
    }


    @Test
    void shouldFindByStatus(){
        // Given
        IncidentEntity openIncident = new IncidentEntity();
        openIncident.setTitle("Connection Failed");
        openIncident.setIncidentStatusEntity(IncidentStatusEntity.OPEN);

        incidentRepository.saveAll(List.of(openIncident));

        //When
        List<IncidentEntity> resultsByStatus = incidentRepository.findByIncidentStatusEntity(IncidentStatusEntity.OPEN);

        //Then
        Assertions.assertThat(resultsByStatus)
                .hasSize(1)
                .extracting(IncidentEntity::getIncidentStatusEntity)
                .containsExactly(IncidentStatusEntity.OPEN);
    }

    @Test
    void shouldFindAll(){
        // Given
        IncidentEntity openIncident1 = new IncidentEntity();
        openIncident1.setTitle("query test1");
        openIncident1.setIncidentStatusEntity(IncidentStatusEntity.OPEN);

        IncidentEntity openIncident2 = new IncidentEntity();
        openIncident2.setTitle("query test2");
        openIncident2.setIncidentStatusEntity(IncidentStatusEntity.OPEN);

        incidentRepository.saveAll(List.of(openIncident1, openIncident2));

        //When
        List<IncidentEntity> results= incidentRepository.findAll();

        //Then
        Assertions.assertThat(results)
                .hasSize(2);
    }
}
