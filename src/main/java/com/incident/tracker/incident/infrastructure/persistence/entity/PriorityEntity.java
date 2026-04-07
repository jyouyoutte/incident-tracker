package com.incident.tracker.incident.infrastructure.persistence.entity;

import com.incident.tracker.shared.domain.LabelledEnum;

/**
 * Possible lifecycle states for an incident
 */
public enum PriorityEntity implements LabelledEnum {
    P1("CRITICAL"),
    P2("HIGH"),
    P3("MODERATE"),
    P4("LOW");

    public final String label;

    PriorityEntity(String label) {
        this.label = label;
    }


    @Override
    public String getLabel() {
        return label;
    }
}
