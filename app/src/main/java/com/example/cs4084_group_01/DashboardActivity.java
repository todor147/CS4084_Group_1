package com.example.cs4084_group_01;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.cs4084_group_01.manager.UserManager;
import com.example.cs4084_group_01.model.UserProfile;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.button.MaterialButton;

public class DashboardActivity extends AppCompatActivity {
    private UserManager userManager;
    private TextView ageText, heightText, weightText, genderText, activityLevelText;
    private TextView bmiValueText, bmiCategoryText, bmiDescriptionText;
    private FloatingActionButton editProfileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize UserManager
        userManager = UserManager.getInstance(this);

        // Check if user is logged in
        if (userManager.getCurrentUser() == null) {
            startLoginActivity();
            return;
        }

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Dashboard");

        // Initialize views
        initializeViews();
        loadUserProfile();

        // Setup edit profile button
        editProfileButton = findViewById(R.id.editProfileButton);
        editProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        });

        // Setup mood selection button
        Button moodSelectionButton = findViewById(R.id.moodSelectionButton);
        moodSelectionButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, MoodSelection.class);
            startActivity(intent);
        });

        // Setup water tracking button
        MaterialButton waterTrackingButton = findViewById(R.id.waterTrackingButton);
        waterTrackingButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, WaterTrackingActivity.class);
            startActivity(intent);
        });
    }

    private void initializeViews() {
        // Profile Information
        ageText = findViewById(R.id.ageText);
        heightText = findViewById(R.id.heightText);
        weightText = findViewById(R.id.weightText);
        genderText = findViewById(R.id.genderText);
        activityLevelText = findViewById(R.id.activityLevelText);

        // BMI Information
        bmiValueText = findViewById(R.id.bmiValueText);
        bmiCategoryText = findViewById(R.id.bmiCategoryText);
        bmiDescriptionText = findViewById(R.id.bmiDescriptionText);
    }

    private void loadUserProfile() {
        String currentUser = userManager.getCurrentUser();
        if (currentUser != null) {
            UserProfile profile = userManager.getUserProfile(currentUser);
            if (profile != null) {
                updateProfileDisplay(profile);
            } else {
                // Profile not set up, redirect to ProfileActivity
                Toast.makeText(this, "Please complete your profile", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, ProfileActivity.class);
                startActivity(intent);
            }
        }
    }

    private void updateProfileDisplay(UserProfile profile) {
        // Update profile information
        ageText.setText(String.valueOf(profile.getAge()));
        heightText.setText(String.format("%.1f cm", profile.getHeight()));
        weightText.setText(String.format("%.1f kg", profile.getWeight()));
        genderText.setText(profile.getGender() != null ? profile.getGender() : "Not set");
        activityLevelText.setText(profile.getActivityLevel() != null ? profile.getActivityLevel() : "Not set");

        // Calculate and display BMI
        double bmi = profile.calculateBMI();
        String category = profile.getBMICategory();
        String description = getBMIDescription(category);

        bmiValueText.setText(String.format("%.1f", bmi));
        bmiCategoryText.setText(category);
        bmiDescriptionText.setText(description);
    }

    private String getBMIDescription(String category) {
        switch (category) {
            case "Underweight":
                return "You may need to gain some weight to reach a healthy BMI range.";
            case "Normal":
                return "Your BMI is within a healthy range. Keep up the good work!";
            case "Overweight":
                return "Consider making lifestyle changes to reach a healthier BMI range.";
            case "Obese":
                return "Consult with a healthcare provider about strategies to improve your health.";
            default:
                return "BMI information not available.";
        }
    }

    private void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            userManager.logout();
            startLoginActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserProfile(); // Reload profile data when returning to dashboard
    }
} 