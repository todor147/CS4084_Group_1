package com.example.cs4084_group_01;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.cs4084_group_01.model.MoodEntry;
import com.example.cs4084_group_01.model.MoodType;
import com.example.cs4084_group_01.model.StepData;
import com.example.cs4084_group_01.model.WaterIntake;
import com.example.cs4084_group_01.repository.MoodRepository;
import com.example.cs4084_group_01.repository.StepDataRepository;
import com.example.cs4084_group_01.repository.WaterIntakeRepository;
import com.example.cs4084_group_01.util.DateUtils;
import com.example.cs4084_group_01.viewmodel.HealthDashboardViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HealthDashboardActivity extends AppCompatActivity {
    private HealthDashboardViewModel viewModel;
    
    // UI Components
    private TextView todayDateText;
    private TextView stepsCountText;
    private TextView stepsGoalText;
    private LinearProgressIndicator stepsProgressIndicator;
    private TextView waterIntakeText;
    private TextView waterGoalText;
    private LinearProgressIndicator waterProgressIndicator;
    private TextView moodTypeText;
    private TextView moodTimeText;
    private MaterialButton resetButton;
    
    private SimpleDateFormat dateFormat;
    private SimpleDateFormat timeFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_dashboard);
        
        // Set up toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        
        // Initialize date formatters
        dateFormat = new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault());
        timeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
        
        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(HealthDashboardViewModel.class);
        
        // Initialize views
        initializeViews();
        
        // Set up listeners
        setupListeners();
        
        // Set up observers
        setupObservers();
        
        // Update date display
        updateDateDisplay();
        
        // Load data
        viewModel.loadAllHealthData();
    }
    
    private void initializeViews() {
        todayDateText = findViewById(R.id.todayDateText);
        stepsCountText = findViewById(R.id.stepsCountText);
        stepsGoalText = findViewById(R.id.stepsGoalText);
        stepsProgressIndicator = findViewById(R.id.stepsProgressIndicator);
        waterIntakeText = findViewById(R.id.waterIntakeText);
        waterGoalText = findViewById(R.id.waterGoalText);
        waterProgressIndicator = findViewById(R.id.waterProgressIndicator);
        moodTypeText = findViewById(R.id.moodTypeText);
        moodTimeText = findViewById(R.id.moodTimeText);
        resetButton = findViewById(R.id.resetButton);
    }
    
    private void setupListeners() {
        resetButton.setOnClickListener(v -> {
            viewModel.resetDailyStats();
            Toast.makeText(this, "Daily stats have been reset", Toast.LENGTH_SHORT).show();
        });
    }
    
    private void setupObservers() {
        // Observe step data
        viewModel.getStepData().observe(this, this::updateStepData);
        
        // Observe water intake data
        viewModel.getWaterIntake().observe(this, this::updateWaterIntake);
        
        // Observe mood data
        viewModel.getMoodEntry().observe(this, this::updateMoodData);
    }
    
    private void updateDateDisplay() {
        Calendar calendar = Calendar.getInstance();
        todayDateText.setText(dateFormat.format(calendar.getTime()));
    }
    
    private void updateStepData(StepData stepData) {
        if (stepData != null) {
            int steps = stepData.getSteps();
            int goal = stepData.getGoal();
            int progress = (int) (((float) steps / goal) * 100);
            
            stepsCountText.setText(String.valueOf(steps));
            stepsGoalText.setText("Goal: " + goal + " steps");
            stepsProgressIndicator.setProgress(Math.min(progress, 100));
        } else {
            stepsCountText.setText("0");
            stepsGoalText.setText("Goal: 10,000 steps");
            stepsProgressIndicator.setProgress(0);
        }
    }
    
    private void updateWaterIntake(WaterIntake waterIntake) {
        if (waterIntake != null) {
            float intake = waterIntake.getCurrentIntake();
            int goal = 2000; // Default goal in ml
            int progress = (int) ((intake / goal) * 100);
            
            waterIntakeText.setText(String.format(Locale.getDefault(), "%.0f ml", intake));
            waterGoalText.setText("Goal: " + goal + " ml");
            waterProgressIndicator.setProgress(Math.min(progress, 100));
        } else {
            waterIntakeText.setText("0 ml");
            waterGoalText.setText("Goal: 2000 ml");
            waterProgressIndicator.setProgress(0);
        }
    }
    
    private void updateMoodData(MoodEntry moodEntry) {
        if (moodEntry != null) {
            MoodType moodType = moodEntry.getMoodType();
            Date timestamp = moodEntry.getTimestamp();
            
            moodTypeText.setText(moodType.getDisplayName());
            moodTimeText.setText("Last updated: " + timeFormat.format(timestamp));
        } else {
            moodTypeText.setText("No mood recorded");
            moodTimeText.setText("Last updated: -");
        }
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 