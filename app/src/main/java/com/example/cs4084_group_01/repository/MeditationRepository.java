package com.example.cs4084_group_01.repository;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.cs4084_group_01.model.MeditationSession;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MeditationRepository {
    private static final String PREFS_NAME = "meditation_prefs";
    private static final String SESSIONS_KEY = "sessions";
    private final SharedPreferences prefs;
    private final Gson gson;

    public MeditationRepository(Context context) {
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
    }

    public void saveSession(MeditationSession session) {
        List<MeditationSession> sessions = getSessions();
        sessions.add(session);
        String json = gson.toJson(sessions);
        prefs.edit().putString(SESSIONS_KEY, json).apply();
    }

    public List<MeditationSession> getSessions() {
        String json = prefs.getString(SESSIONS_KEY, null);
        if (json == null) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<List<MeditationSession>>(){}.getType();
        return gson.fromJson(json, type);
    }

    public void clearSessions() {
        prefs.edit().remove(SESSIONS_KEY).apply();
    }
} 