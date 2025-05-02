package com.example.cs4084_group_01;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cs4084_group_01.util.ThemeManager;

/**
 * Base activity class that handles theme and other app-wide settings.
 * All activities should extend this class to ensure consistent theme and behavior.
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // Apply theme before calling super.onCreate to ensure consistent UI
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
    }
} 