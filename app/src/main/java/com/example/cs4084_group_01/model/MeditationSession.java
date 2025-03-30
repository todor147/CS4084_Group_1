package com.example.cs4084_group_01.model;

import java.util.Date;

public class MeditationSession {
    private int durationMinutes;
    private Date date;
    private boolean completed;

    public MeditationSession(int durationMinutes) {
        this.durationMinutes = durationMinutes;
        this.date = new Date();
        this.completed = false;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
} 