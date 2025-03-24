package com.example.cs4084_group_01.model;

import java.io.Serializable;
import java.util.Date;

public class MoodEntry implements Serializable {
    private MoodType moodType;
    private Date timestamp;
    private String notes;

    public MoodEntry(MoodType moodType, Date timestamp, String notes) {
        this.moodType = moodType;
        this.timestamp = timestamp;
        this.notes = notes;
    }

    // Default constructor for Gson
    public MoodEntry() {
        this.moodType = MoodType.NEUTRAL;
        this.timestamp = new Date();
        this.notes = "";
    }

    public MoodType getMoodType() {
        return moodType;
    }

    public void setMoodType(MoodType moodType) {
        this.moodType = moodType;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
} 