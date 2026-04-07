package com.incident.tracker.incident.domain.model;

import com.incident.tracker.shared.domain.LabelledEnum;

/**
 * Possible priority for an incident
 */
public enum Priority implements LabelledEnum {
    P1("CRITICAL"),
    P2("HIGH"),
    P3("MODERATE"),
    P4("LOW");

    public final String label;

    Priority(String label) {
        this.label = label;
    }

    @Override
    public String getLabel() {
        return label;
    }
}
