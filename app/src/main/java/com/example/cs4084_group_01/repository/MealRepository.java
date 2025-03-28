package com.example.cs4084_group_01.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.cs4084_group_01.model.FoodGroup;
import com.example.cs4084_group_01.model.MealEntry;
import com.example.cs4084_group_01.model.MealType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Repository for storing and retrieving meal entries using SharedPreferences and Gson.
 */
public class MealRepository {
    private static final String TAG = "MealRepository";
    private static final String MEAL_PREFS = "meal_prefs";
    private static final String KEY_MEAL_DATA = "meal_entries";
    
    private static MealRepository instance;
    private final SharedPreferences prefs;
    private final Gson gson;
    private final SimpleDateFormat dateFormat;
    
    private MealRepository(Context context) {
        prefs = context.getSharedPreferences(MEAL_PREFS, Context.MODE_PRIVATE);
        
        // Custom adapter for FoodGroup enum
        TypeAdapter<FoodGroup> foodGroupAdapter = new TypeAdapter<FoodGroup>() {
            @Override
            public void write(JsonWriter out, FoodGroup value) throws IOException {
                if (value == null) {
                    out.nullValue();
                } else {
                    out.value(value.name());
                }
            }

            @Override
            public FoodGroup read(JsonReader in) throws IOException {
                if (in.peek() == null) {
                    in.nextNull();
                    return null;
                }
                return FoodGroup.valueOf(in.nextString());
            }
        };
        
        // Custom adapter for MealType enum
        TypeAdapter<MealType> mealTypeAdapter = new TypeAdapter<MealType>() {
            @Override
            public void write(JsonWriter out, MealType value) throws IOException {
                if (value == null) {
                    out.nullValue();
                } else {
                    out.value(value.name());
                }
            }

            @Override
            public MealType read(JsonReader in) throws IOException {
                if (in.peek() == null) {
                    in.nextNull();
                    return null;
                }
                return MealType.valueOf(in.nextString());
            }
        };
        
        gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .registerTypeAdapter(FoodGroup.class, foodGroupAdapter)
                .registerTypeAdapter(MealType.class, mealTypeAdapter)
                .create();
        
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    }
    
    public static synchronized MealRepository getInstance(Context context) {
        if (instance == null) {
            instance = new MealRepository(context.getApplicationContext());
        }
        return instance;
    }
    
    /**
     * Get all meal entries organized by date.
     */
    public Map<String, List<MealEntry>> getAllMealEntries() {
        String mealDataJson = prefs.getString(KEY_MEAL_DATA, "{}");
        Type type = new TypeToken<HashMap<String, List<MealEntry>>>(){}.getType();
        Map<String, List<MealEntry>> mealEntryMap;
        try {
            mealEntryMap = gson.fromJson(mealDataJson, type);
        } catch (Exception e) {
            Log.e(TAG, "Error parsing meal entries: " + e.getMessage());
            mealEntryMap = new HashMap<>();
        }
        return mealEntryMap != null ? mealEntryMap : new HashMap<>();
    }
    
    /**
     * Get meal entries for a specific date using a date string.
     * @param dateKey Date string in format "yyyy-MM-dd"
     */
    public List<MealEntry> getMealsForDate(String dateKey) {
        Map<String, List<MealEntry>> mealEntryMap = getAllMealEntries();
        return mealEntryMap.getOrDefault(dateKey, new ArrayList<>());
    }
    
    /**
     * Get meal entries for a specific date.
     */
    public List<MealEntry> getMealEntriesForDate(Date date) {
        String dateKey = dateFormat.format(date);
        Map<String, List<MealEntry>> mealEntryMap = getAllMealEntries();
        return mealEntryMap.getOrDefault(dateKey, new ArrayList<>());
    }
    
    /**
     * Get meal entries for today.
     */
    public List<MealEntry> getTodaysMealEntries() {
        return getMealEntriesForDate(new Date());
    }
    
    /**
     * Save a meal entry.
     */
    public void saveMealEntry(MealEntry entry) {
        String dateKey = dateFormat.format(entry.getTimestamp());
        Map<String, List<MealEntry>> mealEntryMap = getAllMealEntries();
        List<MealEntry> entries = mealEntryMap.getOrDefault(dateKey, new ArrayList<>());
        entries.add(entry);
        mealEntryMap.put(dateKey, entries);
        
        String mealDataJson = gson.toJson(mealEntryMap);
        prefs.edit().putString(KEY_MEAL_DATA, mealDataJson).apply();
    }
    
    /**
     * Save multiple meal entries.
     */
    public void saveMealEntries(Map<String, List<MealEntry>> mealEntryMap) {
        String mealDataJson = gson.toJson(mealEntryMap);
        prefs.edit().putString(KEY_MEAL_DATA, mealDataJson).apply();
    }
    
    /**
     * Delete a meal entry.
     */
    public void deleteMealEntry(MealEntry entry) {
        String dateKey = dateFormat.format(entry.getTimestamp());
        Map<String, List<MealEntry>> mealEntryMap = getAllMealEntries();
        List<MealEntry> entries = mealEntryMap.getOrDefault(dateKey, new ArrayList<>());
        
        // Find and remove the entry (timestamp-based comparison)
        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i).getTimestamp().equals(entry.getTimestamp())) {
                entries.remove(i);
                break;
            }
        }
        
        if (entries.isEmpty()) {
            mealEntryMap.remove(dateKey);
        } else {
            mealEntryMap.put(dateKey, entries);
        }
        
        String mealDataJson = gson.toJson(mealEntryMap);
        prefs.edit().putString(KEY_MEAL_DATA, mealDataJson).apply();
    }
    
    /**
     * Clear all entries.
     */
    public void clearAllEntries() {
        prefs.edit().remove(KEY_MEAL_DATA).apply();
    }
    
    /**
     * Get food group breakdown for a specific date.
     * Returns a map with the count of each food group.
     */
    public Map<FoodGroup, Integer> getFoodGroupsBreakdown(Date date) {
        List<MealEntry> entries = getMealEntriesForDate(date);
        Map<FoodGroup, Integer> breakdown = new HashMap<>();
        
        for (MealEntry entry : entries) {
            for (FoodGroup foodGroup : entry.getFoodGroups()) {
                breakdown.put(foodGroup, breakdown.getOrDefault(foodGroup, 0) + 1);
            }
        }
        
        return breakdown;
    }
    
    /**
     * Get meal type breakdown for a specific date.
     * Returns a map with the count of each meal type.
     */
    public Map<MealType, Integer> getMealTypeBreakdown(Date date) {
        List<MealEntry> entries = getMealEntriesForDate(date);
        Map<MealType, Integer> breakdown = new HashMap<>();
        
        for (MealEntry entry : entries) {
            breakdown.put(entry.getMealType(), breakdown.getOrDefault(entry.getMealType(), 0) + 1);
        }
        
        return breakdown;
    }
} 