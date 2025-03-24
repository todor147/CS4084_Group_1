package com.example.cs4084_group_01.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.cs4084_group_01.model.MoodType;
import com.example.cs4084_group_01.repository.MoodRepository;
import com.example.cs4084_group_01.model.MoodEntry;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MoodViewModel extends AndroidViewModel {
    private final MoodRepository moodRepository;
    private final MutableLiveData<Map<String, List<MoodEntry>>> moodEntriesLiveData;
    private final MutableLiveData<List<MoodEntry>> currentDateEntriesLiveData;
    private final MutableLiveData<String> currentDateLiveData;

    public MoodViewModel(Application application) {
        super(application);
        moodRepository = MoodRepository.getInstance(application);
        moodEntriesLiveData = new MutableLiveData<>();
        currentDateEntriesLiveData = new MutableLiveData<>();
        currentDateLiveData = new MutableLiveData<>();
        
        // Set today as the default date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String today = sdf.format(new Date());
        currentDateLiveData.setValue(today);
        
        // Load all mood entries
        loadMoodEntries();
    }
    
    private void loadMoodEntries() {
        Map<String, List<MoodEntry>> allEntries = moodRepository.getAllMoodEntries();
        moodEntriesLiveData.setValue(allEntries);
        updateCurrentDateEntries();
    }
    
    private void updateCurrentDateEntries() {
        String currentDate = currentDateLiveData.getValue();
        if (currentDate != null) {
            Map<String, List<MoodEntry>> allEntries = moodEntriesLiveData.getValue();
            if (allEntries != null && allEntries.containsKey(currentDate)) {
                currentDateEntriesLiveData.setValue(allEntries.get(currentDate));
            } else {
                currentDateEntriesLiveData.setValue(null);
            }
        }
    }
    
    public LiveData<Map<String, List<MoodEntry>>> getAllMoodEntries() {
        return moodEntriesLiveData;
    }
    
    public LiveData<List<MoodEntry>> getCurrentDateEntries() {
        return currentDateEntriesLiveData;
    }
    
    public LiveData<String> getCurrentDate() {
        return currentDateLiveData;
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
    
    public void saveMoodEntry(MoodType moodType, String notes) {
        String currentDate = currentDateLiveData.getValue();
        if (currentDate == null) {
            // If no date is set, use today
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            currentDate = sdf.format(new Date());
            currentDateLiveData.setValue(currentDate);
        }
        
        MoodEntry newEntry = new MoodEntry(moodType, new Date(), notes);
        moodRepository.saveMoodEntry(newEntry);
        
        // Reload mood entries to update LiveData
        loadMoodEntries();
    }
    
    public void saveMoodEntry(MoodEntry entry) {
        moodRepository.saveMoodEntry(entry);
        loadMoodEntries();
    }
    
    public void clearAllEntries() {
        moodRepository.clearAllEntries();
        moodEntriesLiveData.setValue(new HashMap<>());
        currentDateEntriesLiveData.setValue(null);
    }
} 