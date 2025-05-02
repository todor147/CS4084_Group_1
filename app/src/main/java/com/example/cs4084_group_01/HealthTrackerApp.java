package com.example.cs4084_group_01;

import android.app.Application;

import com.example.cs4084_group_01.util.ThemeManager;

/**
 * Main Application class for the Health Tracker app
 */
public class HealthTrackerApp extends Application {
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        // Initialize theme based on saved preference
        ThemeManager.applyTheme(this);
    }
} 