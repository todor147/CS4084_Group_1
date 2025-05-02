package com.example.cs4084_group_01.model;

/**
 * Represents different types of health goals that can be tracked.
 */
public enum GoalType {
    STEPS("Steps", "steps"),
    WATER("Water", "ml"),
    CALORIES("Calories", "kcal"),
    WORKOUT("Workout", "minutes"),
    MEDITATION("Meditation", "minutes"),
    SLEEP("Sleep", "hours"),
    WEIGHT("Weight", "kg"),
    CUSTOM("Custom", "");

    private final String displayName;
    private final String unit;

    GoalType(String displayName, String unit) {
        this.displayName = displayName;
        this.unit = unit;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getUnit() {
        return unit;
    }
} 