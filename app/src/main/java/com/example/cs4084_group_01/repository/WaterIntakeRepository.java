package com.example.cs4084_group_01.repository;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.example.cs4084_group_01.model.WaterIntake;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class WaterIntakeRepository {
    private static final String PREFS_NAME = "WaterIntakePrefs";
    private static final String KEY_WATER_INTAKE = "water_intake";
    private static final String KEY_WATER_HISTORY = "water_history";
    private final SharedPreferences preferences;
    private final Gson gson;

    public WaterIntakeRepository(Context context) {
        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public void saveWaterIntake(WaterIntake waterIntake) {
        String json = gson.toJson(waterIntake);
        preferences.edit().putString(KEY_WATER_INTAKE, json).apply();
        
        // Also save to history
        List<WaterIntake> history = getWaterIntakeHistory();
        // Remove old entry for same date if exists
        history.removeIf(entry -> isSameHour(entry.getDate(), waterIntake.getDate()));
        history.add(waterIntake);
        saveWaterIntakeHistory(history);
    }

    public WaterIntake getWaterIntake() {
        String json = preferences.getString(KEY_WATER_INTAKE, null);
        if (json == null) {
            return new WaterIntake();
        }
        WaterIntake savedIntake = gson.fromJson(json, WaterIntake.class);
        // If saved intake is from a different day, create new one
        if (!isSameDay(savedIntake.getDate(), new Date())) {
            return new WaterIntake();
        }
        return savedIntake;
    }

    public List<WaterIntake> getWaterIntakeHistory() {
        String json = preferences.getString(KEY_WATER_HISTORY, null);
        if (json == null) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<List<WaterIntake>>(){}.getType();
        return gson.fromJson(json, type);
    }

    private void saveWaterIntakeHistory(List<WaterIntake> history) {
        String json = gson.toJson(history);
        preferences.edit().putString(KEY_WATER_HISTORY, json).apply();
    }
    
    /**
     * Updates a specific water intake entry in the history
     */
    public void updateWaterIntakeEntry(WaterIntake intake) {
        List<WaterIntake> history = getWaterIntakeHistory();
        boolean found = false;
        
        // Find and update the matching entry
        for (int i = 0; i < history.size(); i++) {
            WaterIntake entry = history.get(i);
            if (isSameEntry(entry, intake)) {
                history.set(i, intake);
                found = true;
                break;
            }
        }
        
        // If the entry was updated, save the history
        if (found) {
            saveWaterIntakeHistory(history);
            
            // If the entry is the current day's latest entry, update the current intake
            WaterIntake currentIntake = getWaterIntake();
            if (isSameHour(currentIntake.getDate(), intake.getDate())) {
                saveWaterIntake(intake);
            }
        }
    }
    
    /**
     * Removes a specific water intake entry from the history
     */
    public void removeWaterIntakeEntry(WaterIntake intake) {
        List<WaterIntake> history = getWaterIntakeHistory();
        boolean removed = history.removeIf(entry -> isSameEntry(entry, intake));
        
        // If the entry was removed, save the history
        if (removed) {
            saveWaterIntakeHistory(history);
            
            // If the entry is the current day's latest entry, update the current intake
            WaterIntake currentIntake = getWaterIntake();
            if (isSameHour(currentIntake.getDate(), intake.getDate())) {
                // Create a new current intake
                WaterIntake newCurrentIntake = new WaterIntake();
                
                // Find the most recent entry for today
                Date today = new Date();
                for (WaterIntake entry : history) {
                    if (isSameDay(entry.getDate(), today)) {
                        if (entry.getDate().after(newCurrentIntake.getDate())) {
                            newCurrentIntake = entry;
                        }
                    }
                }
                
                saveWaterIntake(newCurrentIntake);
            }
        }
    }

    private boolean isSameDay(Date date1, Date date2) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        return fmt.format(date1).equals(fmt.format(date2));
    }
    
    /**
     * Checks if two dates have the same hour (useful for replacing recent entries)
     */
    private boolean isSameHour(Date date1, Date date2) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHH", Locale.getDefault());
        return fmt.format(date1).equals(fmt.format(date2));
    }
    
    /**
     * Checks if two water intake entries are the same
     */
    private boolean isSameEntry(WaterIntake entry1, WaterIntake entry2) {
        // Compare dates up to the minute level
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmm", Locale.getDefault());
        return fmt.format(entry1.getDate()).equals(fmt.format(entry2.getDate()));
    }
} 