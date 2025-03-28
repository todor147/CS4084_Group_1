package com.example.cs4084_group_01.model;

/**
 * Represents basic food groups for nutrition awareness.
 */
public enum FoodGroup {
    FRUITS("Fruits"),
    VEGETABLES("Vegetables"),
    GRAINS("Grains"),
    PROTEIN("Protein"),
    DAIRY("Dairy"),
    FATS("Fats and Oils"),
    SWEETS("Sweets and Snacks");

    private final String displayName;

    FoodGroup(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }

    public static FoodGroup fromDisplayName(String displayName) {
        for (FoodGroup foodGroup : values()) {
            if (foodGroup.displayName.equals(displayName)) {
                return foodGroup;
            }
        }
        return null;
    }
} 