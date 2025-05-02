package com.example.cs4084_group_01.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.cs4084_group_01.model.Goal;
import com.example.cs4084_group_01.model.GoalType;
import com.example.cs4084_group_01.repository.GoalRepository;

import java.util.Date;
import java.util.List;

/**
 * ViewModel to manage health goal data for the UI.
 */
public class GoalViewModel extends AndroidViewModel {
    private final GoalRepository repository;
    private final LiveData<List<Goal>> allGoals;
    private final LiveData<List<Goal>> activeGoals;
    private final LiveData<List<Goal>> completedGoals;
    private final MutableLiveData<Goal> selectedGoal = new MutableLiveData<>();
    private final MediatorLiveData<Integer> activeGoalCount = new MediatorLiveData<>();
    private final MediatorLiveData<Integer> completedGoalCount = new MediatorLiveData<>();

    public GoalViewModel(@NonNull Application application) {
        super(application);
        repository = GoalRepository.getInstance(application);
        allGoals = repository.getAllGoals();
        activeGoals = repository.getActiveGoals();
        completedGoals = repository.getCompletedGoals();
        
        // Set up goal count mediators
        activeGoalCount.addSource(activeGoals, goals -> {
            activeGoalCount.setValue(goals != null ? goals.size() : 0);
        });
        
        completedGoalCount.addSource(completedGoals, goals -> {
            completedGoalCount.setValue(goals != null ? goals.size() : 0);
        });
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

    public LiveData<Goal> getSelectedGoal() {
        return selectedGoal;
    }

    public void setSelectedGoal(Goal goal) {
        selectedGoal.setValue(goal);
    }

    public LiveData<Integer> getActiveGoalCount() {
        return activeGoalCount;
    }

    public LiveData<Integer> getCompletedGoalCount() {
        return completedGoalCount;
    }

    public void addGoal(String title, GoalType type, double targetValue, Date targetDate) {
        Goal goal = new Goal(title, type, targetValue, targetDate);
        repository.addGoal(goal);
    }

    public void updateGoal(Goal goal) {
        repository.updateGoal(goal);
    }

    public void deleteGoal(String goalId) {
        repository.deleteGoal(goalId);
    }

    public void updateGoalProgress(String goalId, double newValue) {
        Goal goal = repository.getGoalById(goalId);
        if (goal != null) {
            goal.setCurrentValue(newValue);
            repository.updateGoal(goal);
        }
    }

    public void incrementGoalProgress(String goalId, double amount) {
        Goal goal = repository.getGoalById(goalId);
        if (goal != null) {
            goal.incrementProgress(amount);
            repository.updateGoal(goal);
        }
    }

    public void markGoalCompleted(String goalId) {
        Goal goal = repository.getGoalById(goalId);
        if (goal != null) {
            goal.setCompleted(true);
            repository.updateGoal(goal);
        }
    }
} 