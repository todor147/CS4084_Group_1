package com.example.cs4084_group_01.manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.cs4084_group_01.UserProfile;
import com.google.gson.Gson;

public class UserManager {
    private static final String PREF_NAME = "UserPrefs";
    private static final String KEY_USERS = "users";
    private static final String KEY_CURRENT_USER = "current_user";
    private static UserManager instance;
    private final SharedPreferences preferences;
    private final Gson gson;

    private UserManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public static synchronized UserManager getInstance(Context context) {
        if (instance == null) {
            instance = new UserManager(context.getApplicationContext());
        }
        return instance;
    }

    public boolean registerUser(String username, String password, String email) {
        if (username == null || password == null || email == null ||
                username.isEmpty() || password.isEmpty() || email.isEmpty()) {
            return false;
        }

        String usersJson = preferences.getString(KEY_USERS, "{}");
        java.util.Map<String, UserCredentials> users = gson.fromJson(usersJson,
                new com.google.gson.reflect.TypeToken<java.util.Map<String, UserCredentials>>() {
                }.getType());

        if (users.containsKey(username)) {
            return false;
        }

        users.put(username, new UserCredentials(password, email));
        preferences.edit()
                .putString(KEY_USERS, gson.toJson(users))
                .apply();

        // Create an empty profile for the new user
        saveUserProfile(username, new UserProfile());

        return true;
    }

    public boolean loginUser(String username, String password) {
        if (username == null || password == null ||
                username.isEmpty() || password.isEmpty()) {
            return false;
        }

        String usersJson = preferences.getString(KEY_USERS, "{}");
        java.util.Map<String, UserCredentials> users = gson.fromJson(usersJson,
                new com.google.gson.reflect.TypeToken<java.util.Map<String, UserCredentials>>() {
                }.getType());

        UserCredentials credentials = users.get(username);
        if (credentials != null && credentials.password.equals(password)) {
            preferences.edit()
                    .putString(KEY_CURRENT_USER, username)
                    .apply();
            return true;
        }
        return false;
    }

    public void logoutUser() {
        preferences.edit()
                .remove(KEY_CURRENT_USER)
                .apply();
    }

    public String getCurrentUser() {
        return preferences.getString(KEY_CURRENT_USER, null);
    }

    public void saveUserProfile(String username, UserProfile profile) {
        if (username == null || profile == null) return;
        preferences.edit()
                .putString("profile_" + username, gson.toJson(profile))
                .apply();
    }

    public UserProfile getUserProfile(String username) {
        if (username == null) return null;
        String profileJson = preferences.getString("profile_" + username, null);
        return profileJson != null ? gson.fromJson(profileJson, UserProfile.class) : null;
    }

    private static class UserCredentials {
        String password;
        String email;

        UserCredentials(String password, String email) {
            this.password = password;
            this.email = email;
        }
    }
} 