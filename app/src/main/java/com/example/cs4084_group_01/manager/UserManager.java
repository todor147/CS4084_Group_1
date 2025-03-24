package com.example.cs4084_group_01.manager;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.cs4084_group_01.model.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class UserManager {
    private static final String PREF_NAME = "UserPrefs";
    private static final String KEY_CURRENT_USER = "currentUser";
    private static final String KEY_USER_CREDENTIALS = "user_credentials";
    private static UserManager instance;
    private SharedPreferences prefs;
    private Gson gson;

    private UserManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public static UserManager getInstance(Context context) {
        if (instance == null) {
            instance = new UserManager(context.getApplicationContext());
        }
        return instance;
    }

    public void saveUser(User user) {
        String userJson = gson.toJson(user);
        prefs.edit().putString(KEY_CURRENT_USER, userJson).apply();
    }

    public User getCurrentUser() {
        String userJson = prefs.getString(KEY_CURRENT_USER, null);
        if (userJson != null) {
            return gson.fromJson(userJson, User.class);
        }
        return null;
    }

    public void logoutUser() {
        prefs.edit().remove(KEY_CURRENT_USER).apply();
    }

    public boolean isUserLoggedIn() {
        return getCurrentUser() != null;
    }

    public void saveUserProfile(User user) {
        String userJson = gson.toJson(user);
        prefs.edit().putString(KEY_CURRENT_USER, userJson).apply();
    }

    public User getUserProfile() {
        String userJson = prefs.getString(KEY_CURRENT_USER, null);
        if (userJson != null) {
            return gson.fromJson(userJson, User.class);
        }
        return null;
    }

    public void saveUserCredentials(UserCredentials credentials) {
        String credentialsJson = gson.toJson(credentials);
        prefs.edit().putString(KEY_USER_CREDENTIALS, credentialsJson).apply();
    }

    public UserCredentials getUserCredentials() {
        String credentialsJson = prefs.getString(KEY_USER_CREDENTIALS, null);
        if (credentialsJson != null) {
            return gson.fromJson(credentialsJson, UserCredentials.class);
        }
        return null;
    }

    public boolean loginUser(String email, String password) {
        UserCredentials credentials = getUserCredentials();
        if (credentials != null && credentials.email.equals(email) && credentials.password.equals(password)) {
            // Load user profile
            String userJson = prefs.getString(KEY_CURRENT_USER, null);
            if (userJson != null) {
                saveUser(gson.fromJson(userJson, User.class));
                return true;
            }
        }
        return false;
    }

    public void registerUser(String email, String password, String username, RegisterCallback callback) {
        User newUser = new User(username, email, username);
        // In a real app, you would hash the password and store it securely
        saveUser(newUser);
        boolean success = true;
        if (callback != null) {
            callback.onComplete(success);
        }
    }

    public boolean updateUser(User user) {
        saveUser(user);
        return true;
    }

    public static class UserCredentials {
        private String email;
        private String password;

        public UserCredentials(String email, String password) {
            this.email = email;
            this.password = password;
        }

        public String getEmail() {
            return email;
        }

        public String getPassword() {
            return password;
        }
    }

    public interface RegisterCallback {
        void onComplete(boolean success);
    }
} 