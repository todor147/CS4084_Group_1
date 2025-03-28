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
    private static final String PREFS_NAME = "workout_prefs";
    private static final String WORKOUTS_KEY = "workouts";
    private final SharedPreferences prefs;
    private final Gson gson;

    public WorkoutRepository(Context context) {
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
    }

    public void saveWorkout(WorkoutEntry workout) {
        List<WorkoutEntry> workouts = getWorkouts();
        workouts.add(workout);
        String json = gson.toJson(workouts);
        prefs.edit().putString(WORKOUTS_KEY, json).apply();
    }

    public List<WorkoutEntry> getWorkouts() {
        String json = prefs.getString(WORKOUTS_KEY, null);
        if (json == null) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<List<WorkoutEntry>>(){}.getType();
        return gson.fromJson(json, type);
    }

    public void clearWorkouts() {
        prefs.edit().remove(WORKOUTS_KEY).apply();
    }
} 