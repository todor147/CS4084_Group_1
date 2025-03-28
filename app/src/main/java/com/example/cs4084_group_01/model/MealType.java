package com.example.cs4084_group_01.model;

/**
 * Represents different types of meals throughout the day.
 */
public enum MealType {
    BREAKFAST("Breakfast"),
    LUNCH("Lunch"),
    DINNER("Dinner"),
    SNACK("Snack");

    private final String displayName;

    MealType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }

    public static MealType fromDisplayName(String displayName) {
        for (MealType mealType : values()) {
            if (mealType.displayName.equals(displayName)) {
                return mealType;
            }
        }
        return null;
    }
} 