package com.example.cs4084_group_01.model;

import android.app.Activity;

public class Feature {
    private String title;
    private int iconResId;
    private Class<? extends Activity> activityClass;

    public Feature(String title, int iconResId, Class<? extends Activity> activityClass) {
        this.title = title;
        this.iconResId = iconResId;
        this.activityClass = activityClass;
    }

    public String getTitle() {
        return title;
    }

    public int getIconResId() {
        return iconResId;
    }

    public Class<? extends Activity> getActivityClass() {
        return activityClass;
    }
} 