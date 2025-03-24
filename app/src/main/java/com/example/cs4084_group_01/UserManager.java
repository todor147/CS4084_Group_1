package com.example.cs4084_group_01;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

public class UserManager {
    private static final String PREF_NAME = "UserPrefs";
    private static final String KEY_USER_PROFILE = "user_profile";
    private static final String KEY_USER_CREDENTIALS = "user_credentials";
    
    private final SharedPreferences prefs;
    private final Gson gson;
    private User currentUser;

    public UserManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
        loadCurrentUser();
    }

    private void loadCurrentUser() {
        String userJson = prefs.getString(KEY_USER_PROFILE, null);
        if (userJson != null) {
            Type type = new TypeToken<User>(){}.getType();
            currentUser = gson.fromJson(userJson, type);
        }
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void saveUserProfile(User user) {
        currentUser = user;
        String userJson = gson.toJson(user);
        prefs.edit().putString(KEY_USER_PROFILE, userJson).apply();
    }

    public void saveUserCredentials(String email, String password) {
        UserCredentials credentials = new UserCredentials(email, password);
        String credentialsJson = gson.toJson(credentials);
        prefs.edit().putString(KEY_USER_CREDENTIALS, credentialsJson).apply();
    }

    public UserCredentials getUserCredentials() {
        String credentialsJson = prefs.getString(KEY_USER_CREDENTIALS, null);
        if (credentialsJson != null) {
            Type type = new TypeToken<UserCredentials>(){}.getType();
            return gson.fromJson(credentialsJson, type);
        }
        return null;
    }

    public boolean loginUser(String email, String password) {
        UserCredentials credentials = getUserCredentials();
        if (credentials != null && credentials.getEmail().equals(email) && 
            credentials.getPassword().equals(password)) {
            return true;
        }
        return false;
    }

    public boolean registerUser(String email, String password, String name) {
        if (getUserCredentials() != null) {
            return false; // User already exists
        }
        saveUserCredentials(email, password);
        saveUserProfile(new User(email, name));
        return true;
    }

    public void logoutUser() {
        currentUser = null;
        prefs.edit().clear().apply();
    }

    public static class User {
        private String email;
        private String name;

        public User(String email, String name) {
            this.email = email;
            this.name = name;
        }

        public String getEmail() { return email; }
        public String getName() { return name; }
    }

    public static class UserCredentials {
        private String email;
        private String password;

        public UserCredentials(String email, String password) {
            this.email = email;
            this.password = password;
        }

        public String getEmail() { return email; }
        public String getPassword() { return password; }
    }
} 