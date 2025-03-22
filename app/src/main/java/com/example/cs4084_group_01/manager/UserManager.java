package com.example.cs4084_group_01.manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.cs4084_group_01.model.UserProfile;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class UserManager {
    private static final String PREF_NAME = "UserManagerPrefs";
    private static final String KEY_USERS = "users";
    private static final String KEY_CURRENT_USER = "currentUser";
    private static final String KEY_USER_PROFILES = "userProfiles";
    private static UserManager instance;
    private final SharedPreferences preferences;
    private final Gson gson;
    private final Map<String, UserProfile> userProfiles;

    private UserManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
        userProfiles = new HashMap<>();
        loadUserProfiles();
    }

    public static synchronized UserManager getInstance(Context context) {
        if (instance == null) {
            instance = new UserManager(context.getApplicationContext());
        }
        return instance;
    }

    private void loadUserProfiles() {
        String profilesJson = preferences.getString(KEY_USER_PROFILES, "{}");
        Type type = new TypeToken<Map<String, UserProfile>>(){}.getType();
        Map<String, UserProfile> loadedProfiles = gson.fromJson(profilesJson, type);
        if (loadedProfiles != null) {
            userProfiles.putAll(loadedProfiles);
        }
    }

    private void saveUserProfiles() {
        String profilesJson = gson.toJson(userProfiles);
        preferences.edit().putString(KEY_USER_PROFILES, profilesJson).apply();
    }

    public boolean registerUser(String username, String password, String email) {
        if (username == null || password == null || email == null ||
                username.isEmpty() || password.isEmpty() || email.isEmpty()) {
            return false;
        }

        String usersJson = preferences.getString(KEY_USERS, "{}");
        Type type = new TypeToken<Map<String, UserCredentials>>(){}.getType();
        Map<String, UserCredentials> users = gson.fromJson(usersJson, type);

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
        Type type = new TypeToken<Map<String, UserCredentials>>(){}.getType();
        Map<String, UserCredentials> users = gson.fromJson(usersJson, type);

        UserCredentials credentials = users.get(username);
        if (credentials != null && credentials.password.equals(password)) {
            preferences.edit()
                    .putString(KEY_CURRENT_USER, username)
                    .apply();
            return true;
        }
        return false;
    }

    public void logout() {
        preferences.edit()
                .remove(KEY_CURRENT_USER)
                .apply();
    }

    public String getCurrentUser() {
        return preferences.getString(KEY_CURRENT_USER, null);
    }

    public void saveUserProfile(String username, UserProfile profile) {
        userProfiles.put(username, profile);
        saveUserProfiles();
    }

    public UserProfile getUserProfile(String username) {
        return userProfiles.get(username);
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