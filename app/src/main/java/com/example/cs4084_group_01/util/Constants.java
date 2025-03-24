package com.example.cs4084_group_01.util;

/**
 * Constants used throughout the application.
 */
public class Constants {
    // Broadcast actions
    public static final String ACTION_STEP_COUNTER_UPDATE = "com.example.cs4084_group_01.STEP_COUNTER_UPDATE";
    
    // Broadcast extras
    public static final String EXTRA_STEP_COUNT = "step_count";
    
    // Notification
    public static final String STEP_COUNTER_CHANNEL_ID = "step_counter_channel";
    public static final int STEP_COUNTER_NOTIFICATION_ID = 1001;
    
    // Service wake lock tags
    public static final String STEP_COUNTER_WAKE_LOCK_TAG = "StepCounter:WakeLock";
    
    // Private constructor to prevent instantiation
    private Constants() {
        // Do not instantiate
    }
} 