package com.example.cs4084_group_01;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import com.example.cs4084_group_01.adapter.StepHistoryAdapter;
import com.example.cs4084_group_01.model.StepData;
import com.example.cs4084_group_01.repository.StepDataRepository;
import com.example.cs4084_group_01.util.Constants;
import com.example.cs4084_group_01.util.DemoDataProvider;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class StepCounterActivity extends BaseActivity {
    private static final int PERMISSION_REQUEST_CODE = 1001;
    public static final String PREFS_NAME = "StepCounterPrefs";
    public static final String KEY_STEP_GOAL = "step_goal";

    private TextView stepCountText;
    private TextView goalText;
    private CircularProgressIndicator progressIndicator;
    private MaterialButton setGoalButton;
    private RecyclerView historyRecyclerView;
    private StepHistoryAdapter historyAdapter;
    private int currentSteps = 0;
    private int stepGoal = 10000; // Default goal
    private SharedPreferences prefs;
    private StepDataRepository repository;
    private boolean isDemoMode = false;
    
    private final BroadcastReceiver stepUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Constants.ACTION_STEP_COUNTER_UPDATE.equals(intent.getAction())) {
                currentSteps = intent.getIntExtra(Constants.EXTRA_STEP_COUNT, 0);
                updateUI();
                updateHistoryView();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_counter);

        // Set up toolbar with back button
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Check if in demo mode
        isDemoMode = DemoDataProvider.isDemoModeEnabled(this);

        // Initialize repositories and preferences
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        stepGoal = prefs.getInt(KEY_STEP_GOAL, 10000);
        repository = StepDataRepository.getInstance(this);

        initializeViews();
        setupClickListeners();
        
        if (isDemoMode) {
            // In demo mode, use sample data
            setupDemoData();
        } else {
            // In normal mode, use repository data
            setupHistoryView();
            checkAndRequestPermissions();
        }
        
        updateUI();
    }

    private void initializeViews() {
        stepCountText = findViewById(R.id.stepCountText);
        goalText = findViewById(R.id.goalText);
        progressIndicator = findViewById(R.id.progressIndicator);
        setGoalButton = findViewById(R.id.setGoalButton);
        historyRecyclerView = findViewById(R.id.historyRecyclerView);
    }

    private void setupClickListeners() {
        setGoalButton.setOnClickListener(v -> showSetGoalDialog());
    }
    
    private void setupHistoryView() {
        // Initialize with current data instead of an empty list
        List<StepData> history = repository.getStepHistory();
        
        // Sort by date (newest first)
        Collections.sort(history, (a, b) -> b.getDate().compareTo(a.getDate()));
        
        historyAdapter = new StepHistoryAdapter(history);
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        historyRecyclerView.setAdapter(historyAdapter);
    }
    
    private void updateHistoryView() {
        List<StepData> history = repository.getStepHistory();
        
        // Sort by date (newest first)
        Collections.sort(history, (a, b) -> b.getDate().compareTo(a.getDate()));
        
        historyAdapter.updateData(history);
    }

    private void showSetGoalDialog() {
        TextInputLayout textInputLayout = new TextInputLayout(this);
        textInputLayout.setHint("Daily Step Goal");

        TextInputEditText input = new TextInputEditText(this);
        input.setText(String.valueOf(stepGoal));
        textInputLayout.addView(input);

        new MaterialAlertDialogBuilder(this)
                .setTitle("Set Daily Step Goal")
                .setView(textInputLayout)
                .setPositiveButton("Save", (dialog, which) -> {
                    String goalStr = input.getText().toString();
                    try {
                        int newGoal = Integer.parseInt(goalStr);
                        if (newGoal > 0) {
                            stepGoal = newGoal;
                            prefs.edit().putInt(KEY_STEP_GOAL, stepGoal).apply();
                            updateUI();
                            
                            // Update today's step data with new goal
                            StepData todayData = repository.getTodayStepData(stepGoal);
                            todayData.setGoal(stepGoal);
                            repository.saveStepData(todayData);
                            updateHistoryView();
                            
                            Toast.makeText(this, "Goal updated successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Please enter a positive number", Toast.LENGTH_SHORT).show();
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void checkAndRequestPermissions() {
        List<String> permissionsNeeded = new ArrayList<>();

        // Check for ACTIVITY_RECOGNITION - required for all Android versions for step counter
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
                != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.ACTIVITY_RECOGNITION);
        }

        // Check for POST_NOTIFICATIONS - required for Android 13+ for foreground service notifications
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.POST_NOTIFICATIONS);
        }

        // Check for FOREGROUND_SERVICE_HEALTH - required for Android 14+ for health-type foreground services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE_HEALTH)
                        != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.FOREGROUND_SERVICE_HEALTH);
        }

        if (!permissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    permissionsNeeded.toArray(new String[0]),
                    PERMISSION_REQUEST_CODE);
        } else {
            startStepCounterService();
        }
    }

    private void startStepCounterService() {
        Intent serviceIntent = new Intent(this, StepCounterService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }

            if (allGranted) {
                startStepCounterService();
            } else {
                Toast.makeText(this, "Permissions are required for step tracking", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void updateUI() {
        if (isDemoMode) {
            // In demo mode, use the demo data
            stepCountText.setText(String.valueOf(currentSteps));
            goalText.setText("Goal: " + stepGoal + " steps");
            
            // Update progress indicator
            int progress = (int) (((float) currentSteps / stepGoal) * 100);
            progressIndicator.setProgress(Math.min(progress, 100));
        } else {
            // Get steps from repository
            currentSteps = repository.getCurrentSteps();
            
            // Update UI elements
            stepCountText.setText(String.valueOf(currentSteps));
            goalText.setText("Goal: " + stepGoal + " steps");
            
            // Update progress indicator
            int progress = (int) (((float) currentSteps / stepGoal) * 100);
            progressIndicator.setProgress(Math.min(progress, 100));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        
        if (!isDemoMode) {
            // Only register for step updates in normal mode
            IntentFilter filter = new IntentFilter(Constants.ACTION_STEP_COUNTER_UPDATE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                registerReceiver(stepUpdateReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
            } else {
                registerReceiver(stepUpdateReceiver, filter);
            }
            
            // Update UI and history from repository
            updateUI();
            updateHistoryView();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        
        if (!isDemoMode) {
            // Only unregister receiver in normal mode
            try {
                unregisterReceiver(stepUpdateReceiver);
            } catch (IllegalArgumentException e) {
                // Receiver not registered - ignore
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.common_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.action_health_summary) {
            Intent intent = new Intent(this, HealthDashboardActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Populates the UI with demo/sample data
     */
    private void setupDemoData() {
        // Set current steps to a random value
        currentSteps = DemoDataProvider.getTodayStepCount();
        
        // Create sample history data
        List<StepData> sampleHistory = new ArrayList<>();
        List<Integer> sampleSteps = DemoDataProvider.getSampleStepData(14);
        
        // Generate history for the past 14 days
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -14); // Start 14 days ago
        
        for (int i = 0; i < sampleSteps.size(); i++) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            Date date = calendar.getTime();
            
            StepData stepData = new StepData(date, sampleSteps.get(i), stepGoal);
            sampleHistory.add(stepData);
        }
        
        // Add today's data
        StepData todayData = new StepData(new Date(), currentSteps, stepGoal);
        sampleHistory.add(todayData);
        
        // Sort by date (newest first)
        Collections.sort(sampleHistory, (a, b) -> b.getDate().compareTo(a.getDate()));
        
        // Set up adapter with sample data
        historyAdapter = new StepHistoryAdapter(sampleHistory);
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        historyRecyclerView.setAdapter(historyAdapter);
        
        // Show a toast indicating demo mode
        Toast.makeText(this, "Demo mode: Sample step data is shown", Toast.LENGTH_SHORT).show();
    }
} 