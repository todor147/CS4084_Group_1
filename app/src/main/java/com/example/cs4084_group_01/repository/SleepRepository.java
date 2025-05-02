package com.example.cs4084_group_01.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.cs4084_group_01.model.SleepEntry;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class SleepRepository {
    private static final String TAG = "SleepRepository";
    private static final String PREFS_NAME = "sleep_prefs";
    private static final String SLEEP_ENTRIES_KEY = "sleep_entries";

    private final SharedPreferences sharedPreferences;
    private final Gson gson;
    private final MutableLiveData<List<SleepEntry>> allSleepEntries;

    public SleepRepository(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
        allSleepEntries = new MutableLiveData<>(loadSleepEntries());
    }

    public LiveData<List<SleepEntry>> getAllSleepEntries() {
        return allSleepEntries;
    }

    public void addSleepEntry(SleepEntry entry) {
        List<SleepEntry> entries = loadSleepEntries();
        entries.add(entry);
        saveSleepEntries(entries);
        allSleepEntries.setValue(entries);
    }

    public void updateSleepEntry(SleepEntry updatedEntry) {
        List<SleepEntry> entries = loadSleepEntries();
        
        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i).getId().equals(updatedEntry.getId())) {
                entries.set(i, updatedEntry);
                saveSleepEntries(entries);
                allSleepEntries.setValue(entries);
                return;
            }
        }
        
        Log.e(TAG, "Failed to update sleep entry: no matching ID found");
    }

    public void deleteSleepEntry(String entryId) {
        List<SleepEntry> entries = loadSleepEntries();
        
        List<SleepEntry> updatedEntries = entries.stream()
                .filter(entry -> !entry.getId().equals(entryId))
                .collect(Collectors.toList());
        
        if (updatedEntries.size() < entries.size()) {
            saveSleepEntries(updatedEntries);
            allSleepEntries.setValue(updatedEntries);
        } else {
            Log.e(TAG, "Failed to delete sleep entry: no matching ID found");
        }
    }

    public List<SleepEntry> getEntriesForDate(Date date) {
        List<SleepEntry> entries = loadSleepEntries();
        
        return entries.stream()
                .filter(entry -> isSameDay(entry.getStartTime(), date))
                .collect(Collectors.toList());
    }

    public List<SleepEntry> getEntriesForDateRange(Date startDate, Date endDate) {
        List<SleepEntry> entries = loadSleepEntries();
        
        return entries.stream()
                .filter(entry -> entry.getStartTime() != null && 
                        !entry.getStartTime().before(startDate) && 
                        !entry.getStartTime().after(endDate))
                .collect(Collectors.toList());
    }

    private List<SleepEntry> loadSleepEntries() {
        String json = sharedPreferences.getString(SLEEP_ENTRIES_KEY, null);
        
        if (json == null) {
            return new ArrayList<>();
        }
        
        try {
            Type type = new TypeToken<List<SleepEntry>>() {}.getType();
            return gson.fromJson(json, type);
        } catch (Exception e) {
            Log.e(TAG, "Error loading sleep entries", e);
            return new ArrayList<>();
        }
    }

    private void saveSleepEntries(List<SleepEntry> entries) {
        try {
            String json = gson.toJson(entries);
            sharedPreferences.edit().putString(SLEEP_ENTRIES_KEY, json).apply();
        } catch (Exception e) {
            Log.e(TAG, "Error saving sleep entries", e);
        }
    }
    
    private boolean isSameDay(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }
} 