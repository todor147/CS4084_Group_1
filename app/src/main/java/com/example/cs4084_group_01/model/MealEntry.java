package com.example.cs4084_group_01.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Represents a meal entry with food groups and notes.
 */
public class MealEntry implements Serializable {
    private MealType mealType;
    private Date timestamp;
    private String description;
    private List<FoodGroup> foodGroups;
    private String notes;
    private int calories;

    public MealEntry(MealType mealType, Date timestamp, String description, List<FoodGroup> foodGroups, String notes, int calories) {
        this.mealType = mealType;
        this.timestamp = timestamp;
        this.description = description;
        this.foodGroups = foodGroups;
        this.notes = notes;
        this.calories = calories;
    }

    // Default constructor for Gson
    public MealEntry() {
        this.mealType = MealType.BREAKFAST;
        this.timestamp = new Date();
        this.description = "";
        this.foodGroups = new ArrayList<>();
        this.notes = "";
        this.calories = 0;
    }

    public MealType getMealType() {
        return mealType;
    }

    public void setMealType(MealType mealType) {
        this.mealType = mealType;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<FoodGroup> getFoodGroups() {
        return foodGroups;
    }

    public void setFoodGroups(List<FoodGroup> foodGroups) {
        this.foodGroups = foodGroups;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public int getCalories() {
        return calories;
    }
    
    public void setCalories(int calories) {
        this.calories = calories;
    }
    
    /**
     * Returns a comma-separated string of food groups.
     */
    public String getFoodGroupsString() {
        if (foodGroups == null || foodGroups.isEmpty()) {
            return "No food groups";
        }
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < foodGroups.size(); i++) {
            sb.append(foodGroups.get(i).getDisplayName());
            if (i < foodGroups.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }
} 