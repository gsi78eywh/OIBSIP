package com.oibsip.unitconverter.model;

/**
 * Represents the measurement categories supported by the Unit Converter application.
 */
public enum Category {
    LENGTH("Length", "Distance & dimensions"),
    WEIGHT("Weight / Mass", "Mass & weight measurements"),
    TEMPERATURE("Temperature", "Thermal measurements"),
    VOLUME("Volume / Capacity", "Liquid & dry capacity"),
    SPEED("Speed / Velocity", "Rate of motion"),
    TIME("Time", "Duration & time intervals");

    private final String displayName;
    private final String description;

    Category(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
