package com.example.cs4084_group_01.model;

import java.util.Date;
import java.util.UUID;

/**
 * Represents a health goal that the user wants to achieve.
 */
public class Goal {
    private String id;
    private String title;
    private GoalType type;
    private double targetValue;
    private double currentValue;
    private Date createdDate;
    private Date targetDate;
    private Date completedDate;
    private boolean completed;
    private String notes;

    public Goal() {
        // Required for GSON
        this.id = UUID.randomUUID().toString();
        this.createdDate = new Date();
        this.completed = false;
    }

    public Goal(String title, GoalType type, double targetValue, Date targetDate) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.type = type;
        this.targetValue = targetValue;
        this.currentValue = 0;
        this.createdDate = new Date();
        this.targetDate = targetDate;
        this.completed = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public GoalType getType() {
        return type;
    }

    public void setType(GoalType type) {
        this.type = type;
    }

    public double getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(double targetValue) {
        this.targetValue = targetValue;
    }

    public double getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(double currentValue) {
        this.currentValue = currentValue;
        
        // Check if goal is completed
        if (currentValue >= targetValue && !completed) {
            this.completed = true;
            this.completedDate = new Date();
        }
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(Date targetDate) {
        this.targetDate = targetDate;
    }

    public Date getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(Date completedDate) {
        this.completedDate = completedDate;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
        if (completed && completedDate == null) {
            this.completedDate = new Date();
        }
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * Calculate progress percentage towards goal completion.
     * @return Progress as a percentage (0-100)
     */
    public int getProgressPercentage() {
        if (targetValue <= 0) return 0;
        double progress = (currentValue / targetValue) * 100;
        return (int) Math.min(progress, 100);
    }

    /**
     * Get formatted string for current progress.
     * @return String representation of progress (e.g., "5/10 steps")
     */
    public String getProgressString() {
        return String.format("%.1f/%.1f %s", currentValue, targetValue, type.getUnit());
    }

    /**
     * Determine if the goal is overdue (past target date but not completed).
     * @return true if goal is overdue
     */
    public boolean isOverdue() {
        if (completed) return false;
        if (targetDate == null) return false;
        return new Date().after(targetDate);
    }

    /**
     * Increments the current value by the specified amount.
     * @param amount Amount to add to current value
     */
    public void incrementProgress(double amount) {
        setCurrentValue(currentValue + amount);
    }
} 