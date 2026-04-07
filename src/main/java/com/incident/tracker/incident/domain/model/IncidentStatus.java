package com.incident.tracker.incident.domain.model;

import com.incident.tracker.shared.domain.LabelledEnum;

/** Possible lifecycle states for an incident*/
public enum IncidentStatus implements LabelledEnum {
    OPEN("OPEN"),
    IN_PROGRESS("IN PROGRESS"),
    RESOLVED("RESOLVED"),
    CLOSED("CLOSED");

    public final String label;

    IncidentStatus(String label) {
        this.label = label;
    }

    @Override
    public String getLabel() {
        return label;
    }
}
