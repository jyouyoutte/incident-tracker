package com.incident.tracker.shared.domain;

import java.util.Arrays;

public class EnumFinderUtils {
    /**
     * Finds an enum instance by its name or label.
     */
    public static <T extends Enum<T> & LabelledEnum> T parseByValue(Class<T> enumClass, String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return Arrays.stream(enumClass.getEnumConstants())
                .filter(e -> e.name().equalsIgnoreCase(value) || e.getLabel().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Unknown value '%s' for enum %s", value, enumClass.getSimpleName())
                ));
    }

    public static <T extends Enum<T>> T parseByName(Class<T> enumClass, String name){
        if(name == null || name.isBlank()){
           return null;
        }

        return Arrays.stream(enumClass.getEnumConstants())
                .filter(e -> e.name().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Unknown name '%s' for enum %s", name, enumClass.getSimpleName())
                ));
    }
}
