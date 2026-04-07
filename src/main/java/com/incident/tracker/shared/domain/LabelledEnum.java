package com.incident.tracker.shared.domain;

public interface LabelledEnum {
    String getLabel();
    String name(); // Already present by default in each Enum
}
