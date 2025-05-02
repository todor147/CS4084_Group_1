package com.example.cs4084_group_01.viewmodel;

import android.app.Application;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.cs4084_group_01.model.MealEntry;
import com.example.cs4084_group_01.model.MeditationSession;
import com.example.cs4084_group_01.model.MoodEntry;
import com.example.cs4084_group_01.model.StepData;
import com.example.cs4084_group_01.model.WaterIntake;
import com.example.cs4084_group_01.model.WorkoutEntry;
import com.example.cs4084_group_01.repository.MealRepository;
import com.example.cs4084_group_01.repository.MeditationRepository;
import com.example.cs4084_group_01.repository.MoodRepository;
import com.example.cs4084_group_01.repository.StepDataRepository;
import com.example.cs4084_group_01.repository.WaterIntakeRepository;
import com.example.cs4084_group_01.repository.WorkoutRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HealthDashboardViewModel extends AndroidViewModel {
    private final StepDataRepository stepDataRepository;
    private final WaterIntakeRepository waterIntakeRepository;
    private final MoodRepository moodRepository;
    private final MealRepository mealRepository;
    private final WorkoutRepository workoutRepository;
    private final MeditationRepository meditationRepository;
    
    private final MutableLiveData<StepData> stepData = new MutableLiveData<>();
    private final MutableLiveData<WaterIntake> waterIntake = new MutableLiveData<>();
    private final MutableLiveData<MoodEntry> moodEntry = new MutableLiveData<>();
    private final MutableLiveData<List<MealEntry>> todayMeals = new MutableLiveData<>();
    private final MutableLiveData<Integer> totalCalories = new MutableLiveData<>();
    private final MutableLiveData<List<WorkoutEntry>> todayWorkouts = new MutableLiveData<>();
    private final MutableLiveData<Integer> totalWorkoutMinutes = new MutableLiveData<>();
    private final MutableLiveData<List<MeditationSession>> todayMeditations = new MutableLiveData<>();
    private final MutableLiveData<Long> totalMeditationMinutes = new MutableLiveData<>();
    
    public HealthDashboardViewModel(@NonNull Application application) {
        super(application);
        stepDataRepository = StepDataRepository.getInstance(application);
        // WaterIntakeRepository doesn't have a getInstance method, initialize directly
        waterIntakeRepository = new WaterIntakeRepository(application);
        moodRepository = MoodRepository.getInstance(application);
        mealRepository = MealRepository.getInstance(application);
        workoutRepository = new WorkoutRepository(application);
        meditationRepository = new MeditationRepository(application);
    }
    
    public void loadAllHealthData() {
        loadStepData();
        loadWaterIntake();
        loadMoodData();
        loadMealData();
        loadWorkoutData();
        loadMeditationData();
    }
    
    private void loadMeditationData() {
        List<MeditationSession> allSessions = meditationRepository.getSessions();
        List<MeditationSession> sessionsToday = new ArrayList<>();
        long totalMinutes = 0;
        
        // Get today's start time
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        Date startOfDay = today.getTime();
        
        // Filter meditation sessions for today and calculate total minutes
        for (MeditationSession session : allSessions) {
            if (session.getDate().after(startOfDay) || session.getDate().equals(startOfDay)) {
                sessionsToday.add(session);
                totalMinutes += session.getDurationMinutes();
            }
        }
        
        todayMeditations.setValue(sessionsToday);
        totalMeditationMinutes.setValue(totalMinutes);
    }
    
    private void loadWorkoutData() {
        List<WorkoutEntry> allWorkouts = workoutRepository.getWorkouts();
        List<WorkoutEntry> workoutsToday = new ArrayList<>();
        int totalMinutes = 0;
        
        // Get today's start time
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        Date startOfDay = today.getTime();
        
        // Filter workouts for today and calculate total minutes
        for (WorkoutEntry workout : allWorkouts) {
            if (workout.getDate().after(startOfDay) || workout.getDate().equals(startOfDay)) {
                workoutsToday.add(workout);
                totalMinutes += workout.getDurationMinutes();
            }
        }
        
        todayWorkouts.setValue(workoutsToday);
        totalWorkoutMinutes.setValue(totalMinutes);
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
    
    private void loadMealData() {
        // Format today's date as key for meal repository
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
        
        // Get today's meals using the dedicated method
        List<MealEntry> meals = mealRepository.getMealsForDate(today);
        todayMeals.setValue(meals);
        
        // Calculate total calories
        int calories = 0;
        if (meals != null) {
            for (MealEntry meal : meals) {
                calories += meal.getCalories();
            }
        }
        totalCalories.setValue(calories);
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
        // Note: We don't reset meal data as it's also historical
        // Note: We don't reset workout data as it's also historical
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
    
    public LiveData<List<MealEntry>> getTodayMeals() {
        return todayMeals;
    }
    
    public LiveData<Integer> getTotalCalories() {
        return totalCalories;
    }
    
    public LiveData<List<WorkoutEntry>> getTodayWorkouts() {
        return todayWorkouts;
    }
    
    public LiveData<Integer> getTotalWorkoutMinutes() {
        return totalWorkoutMinutes;
    }
    
    public LiveData<List<MeditationSession>> getTodayMeditations() {
        return todayMeditations;
    }
    
    public LiveData<Long> getTotalMeditationMinutes() {
        return totalMeditationMinutes;
    }
} 