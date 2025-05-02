package com.example.cs4084_group_01.model;

import java.util.HashMap;
import java.util.Map;

public class SleepStatistics {
    private final double averageDuration;
    private final double averageQuality;
    private final double totalSleepTime;
    private final int entriesCount;

    public SleepStatistics(double averageDuration, double averageQuality, double totalSleepTime, int entriesCount) {
        this.averageDuration = averageDuration;
        this.averageQuality = averageQuality;
        this.totalSleepTime = totalSleepTime;
        this.entriesCount = entriesCount;
    }

    public double getAverageDuration() {
        return averageDuration;
    }

    public double getAverageQuality() {
        return averageQuality;
    }

    public double getTotalSleepTime() {
        return totalSleepTime;
    }

    public int getEntriesCount() {
        return entriesCount;
    }
    
    public Map<String, Double> toMap() {
        Map<String, Double> map = new HashMap<>();
        map.put("averageDuration", averageDuration);
        map.put("averageQuality", averageQuality);
        map.put("totalSleepTime", totalSleepTime);
        map.put("entriesCount", (double) entriesCount);
        return map;
    }
} 