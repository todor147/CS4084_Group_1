package com.example.cs4084_group_01.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.example.cs4084_group_01.model.MoodEntry;
import com.example.cs4084_group_01.model.MoodType;

public class MoodRepository {
    private static final String TAG = "MoodRepository";
    private static final String MOOD_PREFS = "mood_prefs";
    private static final String KEY_MOOD_DATA = "mood_entries";
    
    private static MoodRepository instance;
    private final SharedPreferences prefs;
    private final Gson gson;
    private final SimpleDateFormat dateFormat;
    
    private MoodRepository(Context context) {
        prefs = context.getSharedPreferences(MOOD_PREFS, Context.MODE_PRIVATE);
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    }
    
    public static synchronized MoodRepository getInstance(Context context) {
        if (instance == null) {
            instance = new MoodRepository(context.getApplicationContext());
        }
        return instance;
    }
    
    public Map<String, List<MoodEntry>> getAllMoodEntries() {
        String moodDataJson = prefs.getString(KEY_MOOD_DATA, "{}");
        Type type = new TypeToken<HashMap<String, List<MoodEntry>>>(){}.getType();
        Map<String, List<MoodEntry>> moodEntryMap;
        try {
            moodEntryMap = gson.fromJson(moodDataJson, type);
        } catch (Exception e) {
            Log.e(TAG, "Error parsing mood entries: " + e.getMessage());
            moodEntryMap = new HashMap<>();
        }
        return moodEntryMap != null ? moodEntryMap : new HashMap<>();
    }
    
    public List<MoodEntry> getMoodEntriesForDate(Date date) {
        String dateKey = dateFormat.format(date);
        Map<String, List<MoodEntry>> moodEntryMap = getAllMoodEntries();
        return moodEntryMap.getOrDefault(dateKey, new ArrayList<>());
    }
    
    public void saveMoodEntry(MoodEntry entry) {
        String dateKey = dateFormat.format(entry.getTimestamp());
        Map<String, List<MoodEntry>> moodEntryMap = getAllMoodEntries();
        List<MoodEntry> entries = moodEntryMap.getOrDefault(dateKey, new ArrayList<>());
        entries.add(entry);
        moodEntryMap.put(dateKey, entries);
        
        String moodDataJson = gson.toJson(moodEntryMap);
        prefs.edit().putString(KEY_MOOD_DATA, moodDataJson).apply();
    }
    
    public void saveMoodEntries(Map<String, List<MoodEntry>> moodEntryMap) {
        String moodDataJson = gson.toJson(moodEntryMap);
        prefs.edit().putString(KEY_MOOD_DATA, moodDataJson).apply();
    }
    
    public void clearAllEntries() {
        prefs.edit().remove(KEY_MOOD_DATA).apply();
    }
} 