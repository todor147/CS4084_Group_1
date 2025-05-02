package com.example.cs4084_group_01.model;

import java.util.Date;

public class MeditationSession {
    private Date date;
    private long durationMinutes;

    public MeditationSession(Date date, long durationMinutes) {
        this.date = date;
        this.durationMinutes = durationMinutes;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(long durationMinutes) {
        this.durationMinutes = durationMinutes;
    }
} 