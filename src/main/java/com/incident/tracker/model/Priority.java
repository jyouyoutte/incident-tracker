package com.incident.tracker.model;

/**
 * Possible lifecycle states for an incident
 */
public enum Priority {
    P1("CRITICAL"),
    P2("HIGH"),
    P3("MODERATE"),
    P4("LOW");

    public final String label;

    Priority(String label) {
        this.label = label;
    }
}
