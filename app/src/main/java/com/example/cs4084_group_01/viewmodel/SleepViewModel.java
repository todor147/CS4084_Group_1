package com.example.cs4084_group_01.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.cs4084_group_01.model.SleepEntry;
import com.example.cs4084_group_01.model.SleepStatistics;
import com.example.cs4084_group_01.repository.SleepRepository;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

public class SleepViewModel extends AndroidViewModel {
    private final SleepRepository repository;
    private final LiveData<List<SleepEntry>> allSleepEntries;
    private final MutableLiveData<Date> selectedDate = new MutableLiveData<>(new Date());
    private final MediatorLiveData<List<SleepEntry>> sleepEntriesForSelectedDate = new MediatorLiveData<>();
    private final MutableLiveData<Map<String, Double>> weeklyStatistics = new MutableLiveData<>();

    public SleepViewModel(@NonNull Application application) {
        super(application);
        repository = new SleepRepository(application);
        allSleepEntries = repository.getAllSleepEntries();
        
        // Update entries when selected date or all entries change
        sleepEntriesForSelectedDate.addSource(selectedDate, date -> 
                updateEntriesForSelectedDate());
        sleepEntriesForSelectedDate.addSource(allSleepEntries, entries -> 
                updateEntriesForSelectedDate());
                
        // Calculate weekly statistics when all entries change
        allSleepEntries.observeForever(entries -> calculateWeeklyStatistics());
    }

    public LiveData<List<SleepEntry>> getAllSleepEntries() {
        return allSleepEntries;
    }

    public LiveData<List<SleepEntry>> getSleepEntriesForSelectedDate() {
        return sleepEntriesForSelectedDate;
    }

    public LiveData<Map<String, Double>> getWeeklyStatistics() {
        return weeklyStatistics;
    }

    public void setSelectedDate(Date date) {
        selectedDate.setValue(date);
    }

    public Date getSelectedDate() {
        return selectedDate.getValue();
    }

    public void addSleepEntry(SleepEntry entry) {
        repository.addSleepEntry(entry);
    }

    public void updateSleepEntry(SleepEntry entry) {
        repository.updateSleepEntry(entry);
    }

    public void deleteSleepEntry(String entryId) {
        repository.deleteSleepEntry(entryId);
    }

    private void updateEntriesForSelectedDate() {
        Date date = selectedDate.getValue();
        List<SleepEntry> entries = allSleepEntries.getValue();
        
        if (date != null && entries != null) {
            List<SleepEntry> filteredEntries = repository.getEntriesForDate(date);
            sleepEntriesForSelectedDate.setValue(filteredEntries);
        } else {
            sleepEntriesForSelectedDate.setValue(new ArrayList<>());
        }
    }

    private void calculateWeeklyStatistics() {
        List<SleepEntry> allEntries = allSleepEntries.getValue();
        if (allEntries == null || allEntries.isEmpty()) {
            Map<String, Double> emptyStats = new HashMap<>();
            emptyStats.put("averageDuration", 0.0);
            emptyStats.put("averageQuality", 0.0);
            emptyStats.put("totalSleepTime", 0.0);
            emptyStats.put("entriesCount", 0.0);
            weeklyStatistics.setValue(emptyStats);
            return;
        }
        
        // Get entries for the past week
        Calendar calendar = Calendar.getInstance();
        Date currentDate = new Date();
        calendar.setTime(currentDate);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        
        // Set to the first day of the week (Sunday in US, Monday in most other locales)
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        Date weekStart = calendar.getTime();
        
        // Add 6 days to get to the end of the week
        calendar.add(Calendar.DAY_OF_WEEK, 6);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        Date weekEnd = calendar.getTime();

        // Filter entries within the week
        List<SleepEntry> weeklyEntries = allEntries.stream()
                .filter(entry -> {
                    Date entryDate = entry.getStartTime();
                    return !entryDate.before(weekStart) && !entryDate.after(weekEnd);
                })
                .collect(Collectors.toList());

        // Calculate statistics
        int entriesCount = weeklyEntries.size();
        if (entriesCount == 0) {
            Map<String, Double> emptyStats = new HashMap<>();
            emptyStats.put("averageDuration", 0.0);
            emptyStats.put("averageQuality", 0.0);
            emptyStats.put("totalSleepTime", 0.0);
            emptyStats.put("entriesCount", 0.0);
            weeklyStatistics.setValue(emptyStats);
            return;
        }
        
        // Calculate total and average values
        double totalDuration = 0;
        double totalQuality = 0;
        
        for (SleepEntry entry : weeklyEntries) {
            totalDuration += entry.getDurationMinutes();
            totalQuality += entry.getQuality();
        }
        
        double avgDuration = totalDuration / entriesCount;
        double avgQuality = totalQuality / entriesCount;
        
        // Create and return statistics
        Map<String, Double> stats = new HashMap<>();
        stats.put("averageDuration", avgDuration);
        stats.put("averageQuality", avgQuality);
        stats.put("totalSleepTime", totalDuration);
        stats.put("entriesCount", (double) entriesCount);
        
        weeklyStatistics.setValue(stats);
    }
} 