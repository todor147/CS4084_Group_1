package com.example.cs4084_group_01.model;

import java.util.Date;

public class UserProfile {
    // Basic Information
    private int age;
    private float height; // in cm
    private float weight; // in kg
    private String gender;
    private Date dateOfBirth;
    private String activityLevel;

    // Health Metrics
    private int systolicBP;
    private int diastolicBP;
    private int restingHeartRate;
    private float sleepHours;
    private Date lastUpdated;

    // Fitness Goals
    private float targetWeight;
    private int weeklyActivityMinutes;
    private String fitnessGoals;

    // Dietary Information
    private String dietaryRestrictions;
    private int dailyCalorieTarget;
    private float waterIntakeGoal; // in liters

    public UserProfile() {
        // Initialize with empty/default values
        this.age = 0;
        this.height = 0.0f;
        this.weight = 0.0f;
        this.gender = null;
        this.activityLevel = null;
        this.lastUpdated = new Date();
    }

    public UserProfile(int age, float height, float weight, String gender, String activityLevel) {
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.gender = gender;
        this.activityLevel = activityLevel;
    }

    // Getters and Setters
    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
        this.lastUpdated = new Date();
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getActivityLevel() {
        return activityLevel;
    }

    public void setActivityLevel(String activityLevel) {
        this.activityLevel = activityLevel;
    }

    public int getSystolicBP() {
        return systolicBP;
    }

    public void setSystolicBP(int systolicBP) {
        this.systolicBP = systolicBP;
    }

    public int getDiastolicBP() {
        return diastolicBP;
    }

    public void setDiastolicBP(int diastolicBP) {
        this.diastolicBP = diastolicBP;
    }

    public int getRestingHeartRate() {
        return restingHeartRate;
    }

    public void setRestingHeartRate(int restingHeartRate) {
        this.restingHeartRate = restingHeartRate;
    }

    public float getSleepHours() {
        return sleepHours;
    }

    public void setSleepHours(float sleepHours) {
        this.sleepHours = sleepHours;
    }

    public float getTargetWeight() {
        return targetWeight;
    }

    public void setTargetWeight(float targetWeight) {
        this.targetWeight = targetWeight;
    }

    public int getWeeklyActivityMinutes() {
        return weeklyActivityMinutes;
    }

    public void setWeeklyActivityMinutes(int weeklyActivityMinutes) {
        this.weeklyActivityMinutes = weeklyActivityMinutes;
    }

    public String getFitnessGoals() {
        return fitnessGoals;
    }

    public void setFitnessGoals(String fitnessGoals) {
        this.fitnessGoals = fitnessGoals;
    }

    public String getDietaryRestrictions() {
        return dietaryRestrictions;
    }

    public void setDietaryRestrictions(String dietaryRestrictions) {
        this.dietaryRestrictions = dietaryRestrictions;
    }

    public int getDailyCalorieTarget() {
        return dailyCalorieTarget;
    }

    public void setDailyCalorieTarget(int dailyCalorieTarget) {
        this.dailyCalorieTarget = dailyCalorieTarget;
    }

    public float getWaterIntakeGoal() {
        return waterIntakeGoal;
    }

    public void setWaterIntakeGoal(float waterIntakeGoal) {
        this.waterIntakeGoal = waterIntakeGoal;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    // Utility Methods
    public float calculateBMI() {
        if (height <= 0 || weight <= 0) {
            return 0;
        }
        // Convert height from cm to meters
        float heightInMeters = height / 100;
        // Calculate BMI using the formula: weight / (height^2)
        return weight / (heightInMeters * heightInMeters);
    }

    public String getBMICategory() {
        float bmi = calculateBMI();
        if (bmi <= 0) {
            return "Not calculated";
        } else if (bmi < 18.5) {
            return "Underweight";
        } else if (bmi < 25) {
            return "Normal";
        } else if (bmi < 30) {
            return "Overweight";
        } else {
            return "Obese";
        }
    }

    public String getBloodPressureCategory() {
        if (systolicBP < 120 && diastolicBP < 80) return "Normal";
        if (systolicBP < 130 && diastolicBP < 80) return "Elevated";
        if (systolicBP < 140 || diastolicBP < 90) return "Stage 1 Hypertension";
        return "Stage 2 Hypertension";
    }

    public float calculateDailyCalorieNeeds() {
        // Basic BMR calculation using Harris-Benedict equation
        float bmr;
        if (gender != null && gender.equalsIgnoreCase("female")) {
            bmr = 655.1f + (9.563f * weight) + (1.850f * height) - (4.676f * age);
        } else {
            bmr = 66.47f + (13.75f * weight) + (5.003f * height) - (6.755f * age);
        }

        // Activity level multiplier
        float multiplier = 1.2f; // Default to sedentary
        if (activityLevel != null) {
            switch (activityLevel.toLowerCase()) {
                case "light":
                    multiplier = 1.375f;
                    break;
                case "moderate":
                    multiplier = 1.55f;
                    break;
                case "very active":
                    multiplier = 1.725f;
                    break;
            }
        }

        return bmr * multiplier;
    }

    public String getWeightStatus() {
        float bmi = calculateBMI();
        float targetBMI = targetWeight / ((height / 100.0f) * (height / 100.0f));

        if (Math.abs(bmi - targetBMI) < 0.5) {
            return "At target weight";
        }
        return bmi > targetBMI ? "Weight loss needed" : "Weight gain needed";
    }

    public boolean isProfileComplete() {
        return age > 0 && height > 0 && weight > 0 && gender != null &&
                dateOfBirth != null && activityLevel != null && targetWeight > 0;
    }
} 