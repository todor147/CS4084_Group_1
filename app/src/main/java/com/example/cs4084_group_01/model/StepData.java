package com.example.cs4084_group_01.model;

import java.util.Date;

public class StepData {
    private Date date;
    private int steps;
    private int goal;

    public StepData() {
        // Required for GSON
    }

    public StepData(Date date, int steps, int goal) {
        this.date = date;
        this.steps = steps;
        this.goal = goal;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public int getGoal() {
        return goal;
    }

    public void setGoal(int goal) {
        this.goal = goal;
    }
} 