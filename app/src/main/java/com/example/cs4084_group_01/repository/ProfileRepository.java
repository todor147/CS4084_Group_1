package com.example.cs4084_group_01.repository;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.cs4084_group_01.UserProfile;
import com.google.gson.Gson;

public class ProfileRepository {
    private static final String PREF_NAME = "profile_preferences";
    private static final String KEY_PROFILE = "user_profile";

    private final SharedPreferences sharedPreferences;
    private final Gson gson;

    public ProfileRepository(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
    }

    public void saveProfile(UserProfile profile) {
        String json = gson.toJson(profile);
        sharedPreferences.edit().putString(KEY_PROFILE, json).apply();
    }

    public UserProfile getProfile() {
        String json = sharedPreferences.getString(KEY_PROFILE, null);
        if (json == null) {
            return null;
        }
        return gson.fromJson(json, UserProfile.class);
    }

    public void deleteProfile() {
        sharedPreferences.edit().remove(KEY_PROFILE).apply();
    }

    public boolean hasProfile() {
        return sharedPreferences.contains(KEY_PROFILE);
    }
} 