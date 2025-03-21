package com.example.cs4084_group_01.model;

import java.util.Date;

public class WaterIntake {
    private Date date;
    private float currentIntake; // in milliliters
    private float dailyGoal; // in milliliters
    private static final float DEFAULT_GOAL = 2000f; // 2L default goal
    private static final float GLASS_SIZE = 250f; // 250ml per glass

    public WaterIntake() {
        this.date = new Date();
        this.currentIntake = 0f;
        this.dailyGoal = DEFAULT_GOAL;
    }

    public WaterIntake(Date date, float currentIntake, float dailyGoal) {
        this.date = date;
        this.currentIntake = currentIntake;
        this.dailyGoal = dailyGoal;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public float getCurrentIntake() {
        return currentIntake;
    }

    public void setCurrentIntake(float currentIntake) {
        this.currentIntake = currentIntake;
    }

    public float getDailyGoal() {
        return dailyGoal;
    }

    public void setDailyGoal(float dailyGoal) {
        this.dailyGoal = dailyGoal;
    }

    public void addGlass() {
        this.currentIntake += GLASS_SIZE;
    }

    public void removeGlass() {
        if (this.currentIntake >= GLASS_SIZE) {
            this.currentIntake -= GLASS_SIZE;
        }
    }

    public float getProgress() {
        return (currentIntake / dailyGoal) * 100;
    }

    public String getFormattedIntake() {
        return String.format("%.1f L", currentIntake / 1000);
    }

    public String getFormattedGoal() {
        return String.format("%.1f L", dailyGoal / 1000);
    }

    public int getGlassCount() {
        return Math.round(currentIntake / GLASS_SIZE);
    }

    public static float getGlassSize() {
        return GLASS_SIZE;
    }
} 