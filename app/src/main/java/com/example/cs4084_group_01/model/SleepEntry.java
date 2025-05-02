package com.example.cs4084_group_01.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class SleepEntry {
    private String id;
    private Date startTime;
    private Date endTime;
    private int quality;
    private String notes;

    // Default constructor for Gson
    public SleepEntry() {
    }

    public SleepEntry(Date startTime, Date endTime, int quality, String notes) {
        this.id = UUID.randomUUID().toString();
        this.startTime = startTime;
        this.endTime = endTime;
        this.quality = quality;
        this.notes = notes;
    }
    
    // Constructor with ID for updating existing entries
    public SleepEntry(String id, Date startTime, Date endTime, int quality, String notes) {
        this.id = id != null ? id : UUID.randomUUID().toString();
        this.startTime = startTime;
        this.endTime = endTime;
        this.quality = quality;
        this.notes = notes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public int getQuality() {
        return quality;
    }

    public void setQuality(int quality) {
        this.quality = quality;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public int getDurationMinutes() {
        if (startTime == null || endTime == null) {
            return 0;
        }
        
        long durationMillis = endTime.getTime() - startTime.getTime();
        return (int) TimeUnit.MILLISECONDS.toMinutes(durationMillis);
    }
    
    public String getFormattedStartTime() {
        return formatTime(startTime);
    }
    
    public String getFormattedEndTime() {
        return formatTime(endTime);
    }
    
    public String getFormattedDuration() {
        int minutes = getDurationMinutes();
        int hours = minutes / 60;
        int remainingMinutes = minutes % 60;
        
        return String.format(Locale.getDefault(), "%d hr %d min", hours, remainingMinutes);
    }
    
    public String getFormattedDate() {
        if (startTime == null) {
            return "";
        }
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());
        return dateFormat.format(startTime);
    }
    
    private String formatTime(Date date) {
        if (date == null) {
            return "";
        }
        
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
        return timeFormat.format(date);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SleepEntry that = (SleepEntry) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
} 