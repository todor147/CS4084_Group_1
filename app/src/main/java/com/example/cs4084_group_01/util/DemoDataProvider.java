package com.example.cs4084_group_01.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Utility class to provide demo/sample data without relying on repositories
 * This allows activities to display realistic data in UI without actually persisting anything
 */
public class DemoDataProvider {
    private static final Random random = new Random();
    
    /**
     * Checks if demo mode is enabled in the app
     */
    public static boolean isDemoModeEnabled(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean("is_demo_mode", false);
    }
    
    /**
     * Returns a random value between min and max
     */
    public static int getRandomInt(int min, int max) {
        return min + random.nextInt(max - min + 1);
    }
    
    /**
     * Returns a random float between min and max
     */
    public static float getRandomFloat(float min, float max) {
        return min + random.nextFloat() * (max - min);
    }
    
    /**
     * Returns a date that is a random number of days before today
     */
    public static Date getRandomPastDate(int maxDaysAgo) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -getRandomInt(1, maxDaysAgo));
        return calendar.getTime();
    }
    
    /**
     * Returns a list of recent step counts (steps per day)
     * For step counter activity
     */
    public static List<Integer> getSampleStepData(int days) {
        List<Integer> stepData = new ArrayList<>();
        
        for (int i = 0; i < days; i++) {
            // Generate between 3000 and 12000 steps per day
            stepData.add(getRandomInt(3000, 12000));
        }
        
        return stepData;
    }
    
    /**
     * Returns sample data for today's step count
     */
    public static int getTodayStepCount() {
        // Since "today" isn't complete, generate a lower step count
        return getRandomInt(2000, 7000);
    }
    
    /**
     * Returns a typical step count goal
     */
    public static int getStepGoal() {
        return 10000; // Standard step goal
    }
    
    /**
     * Returns a list of water intake amounts (in ml) for recent days
     */
    public static List<Float> getSampleWaterData(int days) {
        List<Float> waterData = new ArrayList<>();
        
        for (int i = 0; i < days; i++) {
            // Generate between 1000ml and 2500ml per day
            waterData.add(getRandomFloat(1000f, 2500f));
        }
        
        return waterData;
    }
    
    /**
     * Returns sample data for today's water intake
     */
    public static float getTodayWaterIntake() {
        // Since "today" isn't complete, generate a lower water intake
        return getRandomFloat(500f, 1500f);
    }
    
    /**
     * Returns a typical water intake goal in ml
     */
    public static float getWaterGoal() {
        return 2000f; // 2L standard goal
    }
    
    /**
     * Returns sample sleep duration in hours for the past few days
     */
    public static List<Float> getSampleSleepDurations(int days) {
        List<Float> sleepData = new ArrayList<>();
        
        for (int i = 0; i < days; i++) {
            // Generate between 5 and 9 hours of sleep
            sleepData.add(getRandomFloat(5.0f, 9.0f));
        }
        
        return sleepData;
    }
    
    /**
     * Returns sample sleep quality (1-5 scale) for the past few days
     */
    public static List<Integer> getSampleSleepQualities(int days) {
        List<Integer> qualityData = new ArrayList<>();
        
        for (int i = 0; i < days; i++) {
            // Generate between 1 and 5 quality rating
            qualityData.add(getRandomInt(1, 5));
        }
        
        return qualityData;
    }
    
    /**
     * Returns sample workout types
     */
    public static List<String> getSampleWorkoutTypes() {
        return Arrays.asList(
            "Running", "Cycling", "Swimming", "Weight Training", 
            "Yoga", "HIIT", "Pilates", "Basketball", "Tennis", "Walking"
        );
    }
    
    /**
     * Returns sample workout intensities
     */
    public static List<String> getSampleWorkoutIntensities() {
        return Arrays.asList(
            "Light", "Moderate", "Intense", "Very Intense"
        );
    }
    
    /**
     * Returns sample workout durations (in minutes)
     */
    public static List<Integer> getSampleWorkoutDurations(int count) {
        List<Integer> durations = new ArrayList<>();
        
        for (int i = 0; i < count; i++) {
            // Generate between 15 and 90 minute workouts
            durations.add(getRandomInt(15, 90));
        }
        
        return durations;
    }
    
    /**
     * Returns sample workout dates
     */
    public static List<Date> getSampleWorkoutDates(int count) {
        List<Date> dates = new ArrayList<>();
        
        for (int i = 0; i < count; i++) {
            // Generate dates within the past 30 days
            dates.add(getRandomPastDate(30));
        }
        
        return dates;
    }
    
    /**
     * Returns sample meal descriptions for breakfast
     */
    public static List<String> getSampleBreakfastOptions() {
        return Arrays.asList(
            "Oatmeal with berries", 
            "Scrambled eggs with toast", 
            "Greek yogurt with granola", 
            "Protein smoothie",
            "Avocado toast with eggs", 
            "Fruit and nut bowl"
        );
    }
    
    /**
     * Returns sample meal descriptions for lunch
     */
    public static List<String> getSampleLunchOptions() {
        return Arrays.asList(
            "Chicken salad sandwich", 
            "Vegetable soup with bread", 
            "Quinoa bowl with vegetables", 
            "Tuna wrap",
            "Pasta with tomato sauce", 
            "Buddha bowl with tofu"
        );
    }
    
    /**
     * Returns sample meal descriptions for dinner
     */
    public static List<String> getSampleDinnerOptions() {
        return Arrays.asList(
            "Grilled salmon with vegetables", 
            "Stir fry with rice", 
            "Chicken curry", 
            "Vegetable lasagna",
            "Steak with potatoes", 
            "Fish tacos with avocado"
        );
    }
    
    /**
     * Returns sample snack descriptions
     */
    public static List<String> getSampleSnackOptions() {
        return Arrays.asList(
            "Apple with peanut butter", 
            "Protein bar", 
            "Handful of nuts", 
            "Greek yogurt",
            "Carrot sticks with hummus", 
            "Banana"
        );
    }
    
    /**
     * Returns sample mood notes based on mood level (1-5, where 1 is very sad, 5 is very happy)
     */
    public static String getSampleMoodNote(int moodLevel) {
        switch (moodLevel) {
            case 1: // Very Sad
                return "Having a really tough day. Need to focus on self-care.";
            case 2: // Sad
                return "Feeling a bit down today. Weather might be affecting my mood.";
            case 3: // Neutral
                return "Average day, nothing special to report.";
            case 4: // Happy
                return "Had a good day overall. Feeling positive.";
            case 5: // Very Happy
                return "Feeling fantastic today! Everything is going well.";
            default:
                return "";
        }
    }
    
    /**
     * Returns sample mood levels (1-5) for the past few days
     */
    public static List<Integer> getSampleMoodLevels(int days) {
        List<Integer> moodData = new ArrayList<>();
        
        for (int i = 0; i < days; i++) {
            // Generate between 1 and 5 mood level
            moodData.add(getRandomInt(1, 5));
        }
        
        return moodData;
    }
    
    /**
     * Returns sample gym facilities
     */
    public static List<String> getSampleGymFacilities() {
        return Arrays.asList(
            "Gym", "Pool", "Basketball Court", "Tennis Court", 
            "Climbing Wall", "Squash Courts", "Running Track"
        );
    }
    
    /**
     * Returns sample gym classes
     */
    public static List<String> getSampleGymClasses() {
        return Arrays.asList(
            "Yoga (Monday, 18:00)",
            "Pilates (Tuesday, 17:30)",
            "HIIT (Wednesday, 18:30)",
            "Spin Class (Thursday, 07:00)",
            "Body Pump (Friday, 17:00)"
        );
    }
    
    /**
     * Returns a sample of current gym occupancy
     */
    public static int getSampleGymOccupancy() {
        return getRandomInt(30, 100);
    }
    
    /**
     * Returns a sample of maximum gym capacity
     */
    public static int getSampleGymCapacity() {
        return 120;
    }
} 