package com.example.cs4084_group_01.repository;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.cs4084_group_01.model.MeditationSession;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MeditationRepository {
    private static final String PREF_NAME = "meditation_prefs";
    private static final String KEY_SESSIONS = "meditation_sessions";
    
    private final SharedPreferences preferences;
    private final Gson gson;
    
    public MeditationRepository(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }
    
    public void saveSession(MeditationSession session) {
        List<MeditationSession> sessions = getSessions();
        sessions.add(session);
        saveSessions(sessions);
    }
    
    public List<MeditationSession> getSessions() {
        String json = preferences.getString(KEY_SESSIONS, null);
        if (json == null) {
            return new ArrayList<>();
        }
        
        Type type = new TypeToken<List<MeditationSession>>() {}.getType();
        List<MeditationSession> sessions = gson.fromJson(json, type);
        return sessions != null ? sessions : new ArrayList<>();
    }
    
    private void saveSessions(List<MeditationSession> sessions) {
        String json = gson.toJson(sessions);
        preferences.edit().putString(KEY_SESSIONS, json).apply();
    }
    
    public void clearSessions() {
        preferences.edit().remove(KEY_SESSIONS).apply();
    }
} 