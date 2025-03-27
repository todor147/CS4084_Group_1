package com.example.cs4084_group_01.viewmodel;

import android.app.Application;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.cs4084_group_01.model.MoodEntry;
import com.example.cs4084_group_01.model.StepData;
import com.example.cs4084_group_01.model.WaterIntake;
import com.example.cs4084_group_01.repository.MoodRepository;
import com.example.cs4084_group_01.repository.StepDataRepository;
import com.example.cs4084_group_01.repository.WaterIntakeRepository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HealthDashboardViewModel extends AndroidViewModel {
    private final StepDataRepository stepDataRepository;
    private final WaterIntakeRepository waterIntakeRepository;
    private final MoodRepository moodRepository;
    
    private final MutableLiveData<StepData> stepData = new MutableLiveData<>();
    private final MutableLiveData<WaterIntake> waterIntake = new MutableLiveData<>();
    private final MutableLiveData<MoodEntry> moodEntry = new MutableLiveData<>();
    
    public HealthDashboardViewModel(@NonNull Application application) {
        super(application);
        stepDataRepository = StepDataRepository.getInstance(application);
        // WaterIntakeRepository doesn't have a getInstance method, initialize directly
        waterIntakeRepository = new WaterIntakeRepository(application);
        moodRepository = MoodRepository.getInstance(application);
    }
    
    public void loadAllHealthData() {
        loadStepData();
        loadWaterIntake();
        loadMoodData();
    }
    
    private void loadStepData() {
        // Get today's step data or create a new one with default goal
        StepData todayStepData = stepDataRepository.getTodayStepData(10000);
        stepData.setValue(todayStepData);
    }
    
    private void loadWaterIntake() {
        // Get today's water intake
        WaterIntake todayWaterIntake = waterIntakeRepository.getWaterIntake();
        waterIntake.setValue(todayWaterIntake);
    }
    
    private void loadMoodData() {
        // Get today's mood entries
        List<MoodEntry> todayEntries = moodRepository.getMoodEntriesForDate(new Date());
        
        // Use the most recent mood entry if available
        if (todayEntries != null && !todayEntries.isEmpty()) {
            // Find the latest mood entry
            MoodEntry latestEntry = todayEntries.get(0);
            Date latestTime = latestEntry.getTimestamp();
            
            for (MoodEntry entry : todayEntries) {
                if (entry.getTimestamp().after(latestTime)) {
                    latestEntry = entry;
                    latestTime = entry.getTimestamp();
                }
            }
            
            moodEntry.setValue(latestEntry);
        } else {
            moodEntry.setValue(null);
        }
    }
    
    public void resetDailyStats() {
        // Reset step data
        StepData resetStepData = new StepData(new Date(), 0, 10000);
        stepDataRepository.saveStepData(resetStepData);
        stepData.setValue(resetStepData);
        
        // Reset water intake - use default constructor and set intake to 0
        WaterIntake resetWaterIntake = new WaterIntake();
        resetWaterIntake.setCurrentIntake(0);
        waterIntakeRepository.saveWaterIntake(resetWaterIntake);
        waterIntake.setValue(resetWaterIntake);
        
        // Note: We don't reset mood data as it's a subjective measure and historical
    }
    
    // Getters for LiveData
    public LiveData<StepData> getStepData() {
        return stepData;
    }
    
    public LiveData<WaterIntake> getWaterIntake() {
        return waterIntake;
    }
    
    public LiveData<MoodEntry> getMoodEntry() {
        return moodEntry;
    }
} 