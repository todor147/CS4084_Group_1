package com.example.cs4084_group_01.util;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

/**
 * Utility class to manage theme settings across the application
 */
public class ThemeManager {
    private static final String PREFS_NAME = "fit_tracker_prefs";
    private static final String THEME_PREF = "ThemePreference";
    
    // Theme constants
    public static final int THEME_LIGHT = 0;
    public static final int THEME_DARK = 1;
    public static final int THEME_SYSTEM = 2;

    /**
     * Applies the saved theme preference
     * @param context Application context
     */
    public static void applyTheme(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int themeMode = sharedPreferences.getInt(THEME_PREF, THEME_SYSTEM);
        int currentNightMode = AppCompatDelegate.getDefaultNightMode();
        int targetNightMode;
        
        switch (themeMode) {
            case THEME_LIGHT:
                targetNightMode = AppCompatDelegate.MODE_NIGHT_NO;
                break;
            case THEME_DARK:
                targetNightMode = AppCompatDelegate.MODE_NIGHT_YES;
                break;
            case THEME_SYSTEM:
            default:
                targetNightMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
                break;
        }
        
        // Only change the night mode if it's different from the current one
        if (currentNightMode != targetNightMode) {
            AppCompatDelegate.setDefaultNightMode(targetNightMode);
        }
    }
    
    /**
     * Sets a theme preference
     * @param context Application context
     * @param themeMode The theme mode to set (THEME_LIGHT, THEME_DARK, THEME_SYSTEM)
     */
    public static void setTheme(Context context, int themeMode) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(THEME_PREF, themeMode);
        editor.apply();
        
        // Apply the theme immediately
        applyTheme(context);
    }
    
    /**
     * Gets the current theme preference
     * @param context Application context
     * @return The current theme mode
     */
    public static int getCurrentTheme(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(THEME_PREF, THEME_SYSTEM);
    }
} 