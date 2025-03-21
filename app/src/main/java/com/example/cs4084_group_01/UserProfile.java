package com.example.cs4084_group_01;

public class UserProfile {
    // Basic Information
    private int age;
    private float height;
    private float weight;
    private String gender;
    private String activityLevel;

    public UserProfile() {
        // Initialize with default values
        this.age = 0;
        this.height = 0;
        this.weight = 0;
    }

    // Getters
    public int getAge() {
        return age;
    }

    // Setters
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
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getActivityLevel() {
        return activityLevel;
    }

    public void setActivityLevel(String activityLevel) {
        this.activityLevel = activityLevel;
    }
} 