package com.incident.tracker.domain.model;

public interface LabelledEnum {
    String getLabel();
    String name(); // Already present by default in each Enum
}
