package com.example.cs4084_group_01.repository;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.cs4084_group_01.model.WorkoutEntry;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class WorkoutRepository {
    private static final String PREF_NAME = "workout_prefs";
    private static final String KEY_WORKOUTS = "workout_entries";
    
    private final SharedPreferences preferences;
    private final Gson gson;
    
    public WorkoutRepository(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }
    
    public void saveWorkout(WorkoutEntry workout) {
        List<WorkoutEntry> workouts = getWorkouts();
        workouts.add(workout);
        saveWorkouts(workouts);
    }
    
    public List<WorkoutEntry> getWorkouts() {
        String json = preferences.getString(KEY_WORKOUTS, null);
        if (json == null) {
            return new ArrayList<>();
        }
        
        Type type = new TypeToken<List<WorkoutEntry>>() {}.getType();
        List<WorkoutEntry> workouts = gson.fromJson(json, type);
        return workouts != null ? workouts : new ArrayList<>();
    }
    
    private void saveWorkouts(List<WorkoutEntry> workouts) {
        String json = gson.toJson(workouts);
        preferences.edit().putString(KEY_WORKOUTS, json).apply();
    }
    
    public void clearWorkouts() {
        preferences.edit().remove(KEY_WORKOUTS).apply();
    }
} 