package com.incident.tracker.domain.utils;

import com.incident.tracker.incident.infrastructure.persistence.entity.IncidentStatusEntity;
import com.incident.tracker.incident.infrastructure.persistence.entity.PriorityEntity;
import com.incident.tracker.shared.domain.EnumFinderUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class EnumFinderUtilsTest {

    @ParameterizedTest
    @DisplayName("Verify that parseByName returns the correct IncidentStatus instance for each instance name")
    @EnumSource(IncidentStatusEntity.class)
    void parseByName(IncidentStatusEntity incidentStatus) {
        IncidentStatusEntity found = EnumFinderUtils.parseByName(IncidentStatusEntity.class, incidentStatus.name());
        Assertions.assertThat(found).isNotNull();
    }

    @ParameterizedTest
    @DisplayName("Verify that parseByValue returns the correct Priority instance for a given value")
    @EnumSource(PriorityEntity.class)
    void parseByValue(PriorityEntity priority) {
        PriorityEntity found = EnumFinderUtils.parseByValue(PriorityEntity.class, priority.getLabel());
        Assertions.assertThat(found).isNotNull();
    }

    @Test
    @DisplayName("Verify that parseByName throws IllegalArgumentException for an unknown instance name")
    void should_throw_IllegalArgumentException_when_unknown_enum_names() throws IllegalArgumentException {
        Assertions.assertThatThrownBy(() -> EnumFinderUtils.parseByName(IncidentStatusEntity.class, "UNKNOWN_STATUS"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unknown name 'UNKNOWN_STATUS' for enum IncidentStatus")
        ;
    }

    @Test
    @DisplayName("Verify that parseByValue throws IllegalArgumentException for an unknown instance value")
    void should_throw_IllegalArgumentException_when_unknown_enum_value() throws IllegalArgumentException{
        Assertions.assertThatThrownBy(() -> EnumFinderUtils.parseByValue(PriorityEntity.class, "UNKNOWN_LABEL"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unknown value 'UNKNOWN_LABEL' for enum Priority");    }
}