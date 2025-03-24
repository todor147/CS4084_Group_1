package com.example.cs4084_group_01.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.cs4084_group_01.model.WaterIntake;
import com.example.cs4084_group_01.repository.WaterIntakeRepository;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class WaterViewModel extends AndroidViewModel {
    private final WaterIntakeRepository repository;
    private final MutableLiveData<WaterIntake> currentWaterIntake;
    private final MutableLiveData<List<WaterIntake>> waterHistory;

    public WaterViewModel(Application application) {
        super(application);
        repository = new WaterIntakeRepository(application);
        currentWaterIntake = new MutableLiveData<>();
        waterHistory = new MutableLiveData<>();
        
        // Load initial data
        loadWaterIntake();
        loadWaterHistory();
    }

    public LiveData<WaterIntake> getCurrentWaterIntake() {
        return currentWaterIntake;
    }
    
    public LiveData<List<WaterIntake>> getWaterHistory() {
        return waterHistory;
    }
    
    public void loadWaterIntake() {
        WaterIntake intake = repository.getWaterIntake();
        currentWaterIntake.setValue(intake);
    }
    
    public void loadWaterHistory() {
        List<WaterIntake> history = repository.getWaterIntakeHistory();
        waterHistory.setValue(history);
    }
    
    public void updateWaterIntake(float amount) {
        WaterIntake current = getCurrentIntakeValue();
        
        // Create a new intake if reducing to below zero
        float newAmount = current.getCurrentIntake() + amount;
        if (newAmount < 0) {
            newAmount = 0;
        }
        
        current.setCurrentIntake(newAmount);
        saveWaterIntake(current);
    }
    
    public void setDailyGoal(float goal) {
        WaterIntake current = getCurrentIntakeValue();
        current.setDailyGoal(goal);
        saveWaterIntake(current);
    }
    
    public void saveWaterIntake(WaterIntake intake) {
        repository.saveWaterIntake(intake);
        loadWaterIntake();
        loadWaterHistory();
    }
    
    /**
     * Updates a specific water intake entry
     */
    public void updateWaterIntakeEntry(WaterIntake intake) {
        // Only allow editing today's entries
        if (isToday(intake.getDate())) {
            repository.updateWaterIntakeEntry(intake);
            loadWaterIntake(); // Reload current water intake
            loadWaterHistory(); // Reload history list
        }
    }
    
    /**
     * Removes a water intake entry from history
     */
    public void removeWaterIntakeEntry(WaterIntake intake) {
        // Only allow removing today's entries
        if (isToday(intake.getDate())) {
            repository.removeWaterIntakeEntry(intake);
            loadWaterIntake(); // Reload current water intake
            loadWaterHistory(); // Reload history list
        }
    }
    
    /**
     * Checks if a date is today
     */
    private boolean isToday(Date date) {
        Calendar today = Calendar.getInstance();
        Calendar entryDate = Calendar.getInstance();
        entryDate.setTime(date);
        
        return today.get(Calendar.YEAR) == entryDate.get(Calendar.YEAR) &&
               today.get(Calendar.DAY_OF_YEAR) == entryDate.get(Calendar.DAY_OF_YEAR);
    }
    
    private WaterIntake getCurrentIntakeValue() {
        WaterIntake current = currentWaterIntake.getValue();
        if (current == null) {
            current = new WaterIntake();
            currentWaterIntake.setValue(current);
        }
        return current;
    }
    
    public List<WaterIntake> getTodayHistory() {
        List<WaterIntake> allHistory = waterHistory.getValue();
        if (allHistory == null) {
            return new ArrayList<>();
        }
        
        List<WaterIntake> todayHistory = new ArrayList<>();
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        
        for (WaterIntake intake : allHistory) {
            Calendar intakeDate = Calendar.getInstance();
            intakeDate.setTime(intake.getDate());
            intakeDate.set(Calendar.HOUR_OF_DAY, 0);
            intakeDate.set(Calendar.MINUTE, 0);
            intakeDate.set(Calendar.SECOND, 0);
            intakeDate.set(Calendar.MILLISECOND, 0);
            
            if (today.getTimeInMillis() == intakeDate.getTimeInMillis()) {
                todayHistory.add(intake);
            }
        }
        
        return todayHistory;
    }
} 