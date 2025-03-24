package com.example.cs4084_group_01.repository;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.example.cs4084_group_01.model.StepData;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class StepDataRepository {
    private static final String PREFS_NAME = "StepDataPrefs";
    private static final String KEY_STEP_HISTORY = "step_history";
    private static final String KEY_CURRENT_STEPS = "current_steps";
    
    private static StepDataRepository instance;
    private final SharedPreferences prefs;
    private final Gson gson;
    
    private StepDataRepository(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }
    
    public static synchronized StepDataRepository getInstance(Context context) {
        if (instance == null) {
            instance = new StepDataRepository(context.getApplicationContext());
        }
        return instance;
    }
    
    public void saveCurrentSteps(int steps) {
        prefs.edit().putInt(KEY_CURRENT_STEPS, steps).apply();
    }
    
    public int getCurrentSteps() {
        return prefs.getInt(KEY_CURRENT_STEPS, 0);
    }
    
    public void saveStepData(StepData stepData) {
        List<StepData> history = getStepHistory();
        
        // Check if we already have an entry for today
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        
        boolean updated = false;
        for (int i = 0; i < history.size(); i++) {
            StepData data = history.get(i);
            Calendar dataDate = Calendar.getInstance();
            dataDate.setTime(data.getDate());
            dataDate.set(Calendar.HOUR_OF_DAY, 0);
            dataDate.set(Calendar.MINUTE, 0);
            dataDate.set(Calendar.SECOND, 0);
            dataDate.set(Calendar.MILLISECOND, 0);
            
            if (dataDate.getTimeInMillis() == today.getTimeInMillis()) {
                history.set(i, stepData);
                updated = true;
                break;
            }
        }
        
        if (!updated) {
            history.add(stepData);
        }
        
        saveStepHistory(history);
    }
    
    public List<StepData> getStepHistory() {
        String json = prefs.getString(KEY_STEP_HISTORY, null);
        if (json == null) {
            return new ArrayList<>();
        }
        
        Type type = new TypeToken<List<StepData>>(){}.getType();
        List<StepData> history = gson.fromJson(json, type);
        return history != null ? history : new ArrayList<>();
    }
    
    private void saveStepHistory(List<StepData> history) {
        String json = gson.toJson(history);
        prefs.edit().putString(KEY_STEP_HISTORY, json).apply();
    }
    
    public StepData getTodayStepData(int goal) {
        List<StepData> history = getStepHistory();
        Date today = new Date();
        
        Calendar todayCal = Calendar.getInstance();
        todayCal.setTime(today);
        todayCal.set(Calendar.HOUR_OF_DAY, 0);
        todayCal.set(Calendar.MINUTE, 0);
        todayCal.set(Calendar.SECOND, 0);
        todayCal.set(Calendar.MILLISECOND, 0);
        
        for (StepData data : history) {
            Calendar dataCal = Calendar.getInstance();
            dataCal.setTime(data.getDate());
            dataCal.set(Calendar.HOUR_OF_DAY, 0);
            dataCal.set(Calendar.MINUTE, 0);
            dataCal.set(Calendar.SECOND, 0);
            dataCal.set(Calendar.MILLISECOND, 0);
            
            if (dataCal.getTimeInMillis() == todayCal.getTimeInMillis()) {
                return data;
            }
        }
        
        // No data for today, create a new one
        return new StepData(todayCal.getTime(), getCurrentSteps(), goal);
    }
} 