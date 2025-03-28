package com.example.cs4084_group_01.model;

import java.util.Date;

public class WorkoutEntry {
    private String workoutType;
    private int durationMinutes;
    private String intensity;
    private Date date;

    public WorkoutEntry(String workoutType, int durationMinutes, String intensity) {
        this.workoutType = workoutType;
        this.durationMinutes = durationMinutes;
        this.intensity = intensity;
        this.date = new Date();
    }

    // Getters and setters
    public String getWorkoutType() {
        return workoutType;
    }

    public void setWorkoutType(String workoutType) {
        this.workoutType = workoutType;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getIntensity() {
        return intensity;
    }

    public void setIntensity(String intensity) {
        this.intensity = intensity;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
} 