package com.example.cs4084_group_01.model;

public enum MoodType {
    HAPPY("Happy"),
    CALM("Calm"),
    NEUTRAL("Neutral"),
    SAD("Sad"),
    STRESSED("Stressed");

    private final String displayName;

    MoodType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }

    public static MoodType fromDisplayName(String displayName) {
        for (MoodType moodType : values()) {
            if (moodType.displayName.equals(displayName)) {
                return moodType;
            }
        }
        return NEUTRAL; // Default fallback
    }
} 