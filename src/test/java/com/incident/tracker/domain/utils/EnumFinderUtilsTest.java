package com.incident.tracker.domain.utils;

import com.incident.tracker.domain.utils.EnumFinderUtils;
import com.incident.tracker.domain.model.IncidentStatus;
import com.incident.tracker.domain.model.Priority;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class EnumFinderUtilsTest {

    @ParameterizedTest
    @DisplayName("Verify that parseByName returns the correct IncidentStatus instance for each instance name")
    @EnumSource(IncidentStatus.class)
    void parseByName(IncidentStatus incidentStatus) {
        IncidentStatus found = EnumFinderUtils.parseByName(IncidentStatus.class, incidentStatus.name());
        Assertions.assertThat(found).isNotNull();
    }

    @ParameterizedTest
    @DisplayName("Verify that parseByValue returns the correct Priority instance for a given value")
    @EnumSource(Priority.class)
    void parseByValue(Priority priority) {
        Priority found = EnumFinderUtils.parseByValue(Priority.class, priority.getLabel());
        Assertions.assertThat(found).isNotNull();
    }

    @Test
    @DisplayName("Verify that parseByName throws IllegalArgumentException for an unknown instance name")
    void should_throw_IllegalArgumentException_when_unknown_enum_names() throws IllegalArgumentException {
        Assertions.assertThatThrownBy(() -> EnumFinderUtils.parseByName(IncidentStatus.class, "UNKNOWN_STATUS"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unknown name 'UNKNOWN_STATUS' for enum IncidentStatus")
        ;
    }

    @Test
    @DisplayName("Verify that parseByValue throws IllegalArgumentException for an unknown instance value")
    void should_throw_IllegalArgumentException_when_unknown_enum_value() throws IllegalArgumentException{
        Assertions.assertThatThrownBy(() -> EnumFinderUtils.parseByValue(Priority.class, "UNKNOWN_LABEL"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unknown value 'UNKNOWN_LABEL' for enum Priority");    }
}