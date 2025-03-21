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
        history.removeIf(entry -> isSameDay(entry.getDate(), waterIntake.getDate()));
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

    private boolean isSameDay(Date date1, Date date2) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        return fmt.format(date1).equals(fmt.format(date2));
    }
} 