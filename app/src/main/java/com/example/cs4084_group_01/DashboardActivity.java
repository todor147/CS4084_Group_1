package com.example.cs4084_group_01;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.example.cs4084_group_01.manager.UserManager;
import com.example.cs4084_group_01.model.User;

public class DashboardActivity extends BaseActivity {
    private TextView welcomeText;
    // Quick actions
    private MaterialCardView healthDashboardCard;
    private MaterialCardView workoutCard;
    private MaterialCardView bmiCard;
    
    // Feature cards
    private MaterialCardView healthDashboardFeatureCard;
    private MaterialCardView stepCounterCard;
    private MaterialCardView waterIntakeCard;
    private MaterialCardView nutritionCard;
    private MaterialCardView sleepCard;
    private MaterialCardView moodTrackerCard;
    private MaterialCardView workoutLoggerCard;
    private MaterialCardView meditationCard;
    private MaterialCardView progressCard;
    private MaterialCardView userProfileCard;
    private MaterialCardView campusGymCard;
    private MaterialCardView dataExportCard;
    private MaterialCardView settingsCard;
    
    private TextView bmiValueText;
    private TextView bmiCategoryText;
    private TextView bmiDetailsText;
    private FloatingActionButton editProfileFab;
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize views
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        welcomeText = findViewById(R.id.welcomeText);
        
        // Quick actions
        healthDashboardCard = findViewById(R.id.healthDashboardCard);
        workoutCard = findViewById(R.id.workoutCard);
        bmiCard = findViewById(R.id.bmiCard);
        
        // Features
        healthDashboardFeatureCard = findViewById(R.id.healthDashboardFeatureCard);
        stepCounterCard = findViewById(R.id.stepCounterCard);
        waterIntakeCard = findViewById(R.id.waterIntakeCard);
        nutritionCard = findViewById(R.id.nutritionCard);
        sleepCard = findViewById(R.id.sleepCard);
        moodTrackerCard = findViewById(R.id.moodTrackerCard);
        workoutLoggerCard = findViewById(R.id.workoutLoggerCard);
        meditationCard = findViewById(R.id.meditationCard);
        progressCard = findViewById(R.id.progressCard);
        userProfileCard = findViewById(R.id.userProfileCard);
        campusGymCard = findViewById(R.id.campusGymCard);
        dataExportCard = findViewById(R.id.dataExportCard);
        settingsCard = findViewById(R.id.settingsCard);
        
        bmiValueText = findViewById(R.id.bmiValueText);
        bmiCategoryText = findViewById(R.id.bmiCategoryText);
        bmiDetailsText = findViewById(R.id.bmiDetailsText);
        editProfileFab = findViewById(R.id.editProfileFab);

        // Set up toolbar
        setSupportActionBar(toolbar);

        // Initialize UserManager
        userManager = UserManager.getInstance(this);

        // Enable demo mode if this is first run
        enableDemoModeIfFirstRun();

        // Update welcome message
        updateWelcomeMessage();
        
        // Update BMI display
        updateBMIDisplay();

        // Set up card click listeners
        setupCardClickListeners();
        
        // Setup edit profile button
        if (editProfileFab != null) {
            editProfileFab.setOnClickListener(v -> {
                Intent intent = new Intent(this, ProfileActivity.class);
                startActivity(intent);
            });
        }
    }

    private void updateWelcomeMessage() {
        User currentUser = userManager.getCurrentUser();
        if (currentUser != null) {
            String name = currentUser.getName();
            String email = currentUser.getEmail();
            
            // Set a personalized greeting based on time of day
            int hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY);
            String greeting;
            if (hour < 12) {
                greeting = "Good morning";
            } else if (hour < 17) {
                greeting = "Good afternoon";
            } else {
                greeting = "Good evening";
            }
            
            // Use name if available, otherwise use email
            String displayName = name != null && !name.isEmpty() ? name : email.split("@")[0];
            welcomeText.setText(greeting + ", " + displayName + "!");
        } else {
            welcomeText.setText("Welcome to FitTracker!");
        }
    }

    private void setupCardClickListeners() {
        // Quick Actions
        
        // Health Dashboard (Quick action)
        healthDashboardCard.setOnClickListener(v -> {
            Intent intent = new Intent(this, HealthDashboardActivity.class);
            startActivity(intent);
        });

        // Workout Tracking (Quick action)
        workoutCard.setOnClickListener(v -> {
            Intent intent = new Intent(this, WorkoutTrackingActivity.class);
            startActivity(intent);
        });

        // BMI Calculator (Quick action)
        bmiCard.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtra("open_bmi", true);
            startActivity(intent);
        });
        
        // Features
        
        // Health Dashboard Feature
        healthDashboardFeatureCard.setOnClickListener(v -> {
            Intent intent = new Intent(this, HealthDashboardActivity.class);
            startActivity(intent);
        });
        
        // Step Counter
        stepCounterCard.setOnClickListener(v -> {
            Intent intent = new Intent(this, StepCounterActivity.class);
            startActivity(intent);
        });
        
        // Water Intake Tracker
        waterIntakeCard.setOnClickListener(v -> {
            Intent intent = new Intent(this, WaterTrackingActivity.class);
            startActivity(intent);
        });

        // Meal Tracker
        nutritionCard.setOnClickListener(v -> {
            Intent intent = new Intent(this, MealLoggerActivity.class);
            startActivity(intent);
        });

        // Sleep Logger
        sleepCard.setOnClickListener(v -> {
            Intent intent = new Intent(this, SleepTrackingActivity.class);
            startActivity(intent);
        });
        
        // Mood Tracker
        moodTrackerCard.setOnClickListener(v -> {
            Intent intent = new Intent(this, MoodTrackerActivity.class);
            startActivity(intent);
        });
        
        // Workout Logger
        workoutLoggerCard.setOnClickListener(v -> {
            Intent intent = new Intent(this, WorkoutTrackingActivity.class);
            startActivity(intent);
        });

        // Meditation Timer
        meditationCard.setOnClickListener(v -> {
            Intent intent = new Intent(this, MeditationTimerActivity.class);
            startActivity(intent);
        });

        // Goal Tracker
        progressCard.setOnClickListener(v -> {
            Intent intent = new Intent(this, HealthProgressActivity.class);
            startActivity(intent);
        });
        
        // User Profile
        userProfileCard.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        });

        // Campus Gym Status
        campusGymCard.setOnClickListener(v -> {
            Intent intent = new Intent(this, CampusGymActivity.class);
            startActivity(intent);
        });
        
        // Data Export
        dataExportCard.setOnClickListener(v -> {
            Intent intent = new Intent(this, ExportDataActivity.class);
            startActivity(intent);
        });
        
        // Settings
        settingsCard.setOnClickListener(v -> {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        });
    }

    private void updateBMIDisplay() {
        User currentUser = userManager.getCurrentUser();
        if (currentUser != null && currentUser.getHeight() > 0 && currentUser.getWeight() > 0) {
            float heightInMeters = currentUser.getHeight() / 100f;
            float bmi = currentUser.getWeight() / (heightInMeters * heightInMeters);
            
            bmiValueText.setText(String.format("%.1f", bmi));
            String category = getBMICategory(bmi);
            bmiCategoryText.setText(category);
            bmiDetailsText.setText(getBMIDetailText(category));
        } else {
            bmiValueText.setText("0.0");
            bmiCategoryText.setText("Not calculated");
            bmiDetailsText.setText("Tap to calculate your BMI");
        }
    }
    
    private String getBMICategory(float bmi) {
        if (bmi < 18.5) {
            return "Underweight";
        } else if (bmi < 25) {
            return "Normal weight";
        } else if (bmi < 30) {
            return "Overweight";
        } else {
            return "Obese";
        }
    }
    
    private String getBMIDetailText(String category) {
        switch (category) {
            case "Underweight":
                return "Consider increasing caloric intake";
            case "Normal weight":
                return "Maintain your healthy lifestyle!";
            case "Overweight":
                return "Consider moderate diet changes";
            case "Obese":
                return "Consider consulting a health professional";
            default:
                return "Tap to calculate your BMI";
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Update BMI display when returning to the dashboard
        updateBMIDisplay();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_logout) {
            userManager.logoutUser();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        } else if (itemId == R.id.action_health_summary) {
            Intent intent = new Intent(this, HealthDashboardActivity.class);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.action_export) {
            Intent intent = new Intent(this, ExportDataActivity.class);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Sets a flag indicating that the app is in demo mode
     * Activities can check this flag to display sample data
     */
    private void enableDemoModeIfFirstRun() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isFirstRun = prefs.getBoolean("is_first_run", true);
        
        if (isFirstRun) {
            // Enable demo mode for all features
            prefs.edit()
                .putBoolean("is_demo_mode", true)
                .putBoolean("is_first_run", false)
                .apply();
                
            // Show a toast indicating demo mode
            Toast.makeText(this, "Demo mode enabled - sample data will be shown", Toast.LENGTH_LONG).show();
        }
    }
} 