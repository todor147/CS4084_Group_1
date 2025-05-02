package com.example.cs4084_group_01.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.cs4084_group_01.model.Goal;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Repository for storing and retrieving goals using SharedPreferences and Gson.
 */
public class GoalRepository {
    private static final String TAG = "GoalRepository";
    private static final String PREFS_NAME = "goal_prefs";
    private static final String KEY_GOALS = "goals";

    private static GoalRepository instance;
    private final SharedPreferences preferences;
    private final Gson gson;
    private final MutableLiveData<List<Goal>> allGoals;
    private final MutableLiveData<List<Goal>> activeGoals;
    private final MutableLiveData<List<Goal>> completedGoals;

    private GoalRepository(Context context) {
        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
        List<Goal> goals = loadGoals();
        allGoals = new MutableLiveData<>(goals);
        
        // Filter goals by status
        activeGoals = new MutableLiveData<>(filterActiveGoals(goals));
        completedGoals = new MutableLiveData<>(filterCompletedGoals(goals));
    }

    public static synchronized GoalRepository getInstance(Context context) {
        if (instance == null) {
            instance = new GoalRepository(context.getApplicationContext());
        }
        return instance;
    }

    public LiveData<List<Goal>> getAllGoals() {
        return allGoals;
    }

    public LiveData<List<Goal>> getActiveGoals() {
        return activeGoals;
    }

    public LiveData<List<Goal>> getCompletedGoals() {
        return completedGoals;
    }

    public void addGoal(Goal goal) {
        List<Goal> goals = loadGoals();
        goals.add(goal);
        saveGoals(goals);
        refreshLiveData();
    }

    public void updateGoal(Goal goal) {
        List<Goal> goals = loadGoals();
        
        // Find and update the goal with matching ID
        for (int i = 0; i < goals.size(); i++) {
            if (goals.get(i).getId().equals(goal.getId())) {
                goals.set(i, goal);
                break;
            }
        }
        
        saveGoals(goals);
        refreshLiveData();
    }

    public void deleteGoal(String goalId) {
        List<Goal> goals = loadGoals();
        goals.removeIf(goal -> goal.getId().equals(goalId));
        saveGoals(goals);
        refreshLiveData();
    }

    public Goal getGoalById(String goalId) {
        List<Goal> goals = loadGoals();
        for (Goal goal : goals) {
            if (goal.getId().equals(goalId)) {
                return goal;
            }
        }
        return null;
    }

    private List<Goal> loadGoals() {
        try {
            String json = preferences.getString(KEY_GOALS, null);
            if (json == null) {
                return new ArrayList<>();
            }
            
            Type type = new TypeToken<List<Goal>>(){}.getType();
            List<Goal> goals = gson.fromJson(json, type);
            return goals != null ? goals : new ArrayList<>();
        } catch (Exception e) {
            Log.e(TAG, "Error loading goals: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private void saveGoals(List<Goal> goals) {
        try {
            String json = gson.toJson(goals);
            preferences.edit().putString(KEY_GOALS, json).apply();
        } catch (Exception e) {
            Log.e(TAG, "Error saving goals: " + e.getMessage());
        }
    }

    private void refreshLiveData() {
        List<Goal> goals = loadGoals();
        allGoals.postValue(goals);
        activeGoals.postValue(filterActiveGoals(goals));
        completedGoals.postValue(filterCompletedGoals(goals));
    }

    private List<Goal> filterActiveGoals(List<Goal> goals) {
        return goals.stream()
                .filter(goal -> !goal.isCompleted())
                .collect(Collectors.toList());
    }

    private List<Goal> filterCompletedGoals(List<Goal> goals) {
        return goals.stream()
                .filter(Goal::isCompleted)
                .collect(Collectors.toList());
    }
} 