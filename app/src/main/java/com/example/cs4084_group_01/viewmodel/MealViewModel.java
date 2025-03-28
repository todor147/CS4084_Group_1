package com.example.cs4084_group_01.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.cs4084_group_01.model.FoodGroup;
import com.example.cs4084_group_01.model.MealEntry;
import com.example.cs4084_group_01.model.MealType;
import com.example.cs4084_group_01.repository.MealRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * ViewModel for meal logging feature.
 */
public class MealViewModel extends AndroidViewModel {
    private final MealRepository mealRepository;
    private final MutableLiveData<Map<String, List<MealEntry>>> mealEntriesLiveData;
    private final MutableLiveData<List<MealEntry>> currentDateEntriesLiveData;
    private final MutableLiveData<String> currentDateLiveData;
    private final MutableLiveData<Map<FoodGroup, Integer>> foodGroupsBreakdownLiveData;
    private final MutableLiveData<Map<MealType, Integer>> mealTypeBreakdownLiveData;

    public MealViewModel(Application application) {
        super(application);
        mealRepository = MealRepository.getInstance(application);
        mealEntriesLiveData = new MutableLiveData<>();
        currentDateEntriesLiveData = new MutableLiveData<>();
        currentDateLiveData = new MutableLiveData<>();
        foodGroupsBreakdownLiveData = new MutableLiveData<>();
        mealTypeBreakdownLiveData = new MutableLiveData<>();
        
        // Set today as the default date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String today = sdf.format(new Date());
        currentDateLiveData.setValue(today);
        
        // Load all meal entries
        loadMealEntries();
    }
    
    private void loadMealEntries() {
        Map<String, List<MealEntry>> allEntries = mealRepository.getAllMealEntries();
        mealEntriesLiveData.setValue(allEntries);
        updateCurrentDateEntries();
    }
    
    private void updateCurrentDateEntries() {
        String currentDate = currentDateLiveData.getValue();
        if (currentDate != null) {
            Map<String, List<MealEntry>> allEntries = mealEntriesLiveData.getValue();
            
            if (allEntries != null && allEntries.containsKey(currentDate)) {
                List<MealEntry> entries = allEntries.get(currentDate);
                currentDateEntriesLiveData.setValue(entries);
                
                // Update breakdowns
                updateBreakdowns(entries);
            } else {
                currentDateEntriesLiveData.setValue(new ArrayList<>());
                
                // Set empty breakdowns
                foodGroupsBreakdownLiveData.setValue(new HashMap<>());
                mealTypeBreakdownLiveData.setValue(new HashMap<>());
            }
        }
    }
    
    private void updateBreakdowns(List<MealEntry> entries) {
        // Update food groups breakdown
        Map<FoodGroup, Integer> foodGroupsBreakdown = new HashMap<>();
        
        // Update meal type breakdown
        Map<MealType, Integer> mealTypeBreakdown = new HashMap<>();
        
        for (MealEntry entry : entries) {
            // Update food groups count
            for (FoodGroup foodGroup : entry.getFoodGroups()) {
                foodGroupsBreakdown.put(foodGroup, foodGroupsBreakdown.getOrDefault(foodGroup, 0) + 1);
            }
            
            // Update meal type count
            mealTypeBreakdown.put(entry.getMealType(), mealTypeBreakdown.getOrDefault(entry.getMealType(), 0) + 1);
        }
        
        foodGroupsBreakdownLiveData.setValue(foodGroupsBreakdown);
        mealTypeBreakdownLiveData.setValue(mealTypeBreakdown);
    }
    
    public LiveData<Map<String, List<MealEntry>>> getAllMealEntries() {
        return mealEntriesLiveData;
    }
    
    public LiveData<List<MealEntry>> getCurrentDateEntries() {
        return currentDateEntriesLiveData;
    }
    
    public LiveData<String> getCurrentDate() {
        return currentDateLiveData;
    }
    
    public LiveData<Map<FoodGroup, Integer>> getFoodGroupsBreakdown() {
        return foodGroupsBreakdownLiveData;
    }
    
    public LiveData<Map<MealType, Integer>> getMealTypeBreakdown() {
        return mealTypeBreakdownLiveData;
    }
    
    public void setCurrentDate(String dateKey) {
        currentDateLiveData.setValue(dateKey);
        updateCurrentDateEntries();
    }
    
    public void setCurrentDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String dateKey = sdf.format(date);
        setCurrentDate(dateKey);
    }
    
    public void setCurrentDate(int year, int month, int day) {
        String dateKey = String.format(Locale.US, "%d-%02d-%02d", year, month + 1, day);
        setCurrentDate(dateKey);
    }
    
    public void saveMealEntry(MealEntry entry) {
        mealRepository.saveMealEntry(entry);
        loadMealEntries();
    }
    
    public void saveMealEntry(MealType mealType, String description, List<FoodGroup> foodGroups, String notes) {
        MealEntry entry = new MealEntry(mealType, new Date(), description, foodGroups, notes, 0);
        saveMealEntry(entry);
    }
    
    public void deleteMealEntry(MealEntry entry) {
        mealRepository.deleteMealEntry(entry);
        loadMealEntries();
    }
    
    public void clearAllEntries() {
        mealRepository.clearAllEntries();
        mealEntriesLiveData.setValue(new HashMap<>());
        currentDateEntriesLiveData.setValue(new ArrayList<>());
        foodGroupsBreakdownLiveData.setValue(new HashMap<>());
        mealTypeBreakdownLiveData.setValue(new HashMap<>());
    }
} 