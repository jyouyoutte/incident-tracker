package com.incident.tracker.incident.infrastructure.persistence.entity;

public interface LabelledEnum {
    String getLabel();
    String name(); // Already present by default in each Enum
}
