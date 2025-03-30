package com.example.cs4084_group_01;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.cs4084_group_01.manager.UserManager;
import com.example.cs4084_group_01.model.UserProfile;
import com.example.cs4084_group_01.model.User;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.button.MaterialButton;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class DashboardActivity extends AppCompatActivity {
    private UserManager userManager;
    private TextView ageText, heightText, weightText, genderText, activityLevelText;
    private TextView bmiValueText, bmiCategoryText, bmiDescriptionText;
    private FloatingActionButton editProfileButton;
    private static final int NOTIFICATION_PERMISSION_CODE = 123;

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

        // Setup water tracking button
        MaterialButton waterTrackingButton = findViewById(R.id.waterTrackingButton);
        waterTrackingButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, WaterTrackingActivity.class);
            startActivity(intent);
        });

        // Setup mood selection button
        MaterialButton moodSelectionButton = findViewById(R.id.moodSelectionButton);
        moodSelectionButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, MoodTrackerActivity.class);
            intent.putExtra(MoodTrackerActivity.EXTRA_DATE, System.currentTimeMillis());
            startActivity(intent);
        });

        // Setup workout tracking button
        MaterialButton workoutTrackingButton = findViewById(R.id.workoutTrackingButton);
        workoutTrackingButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, WorkoutTrackingActivity.class);
            startActivity(intent);
        });

        // Setup meditation button
        MaterialButton meditationButton = findViewById(R.id.meditationButton);
        meditationButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, MeditationActivity.class);
            startActivity(intent);
        });

        // Initialize notification system
        MoodNotificationManager.createNotificationChannel(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_CODE);
            } else {
                MoodNotificationManager.scheduleDaily(this);
            }
        } else {
            MoodNotificationManager.scheduleDaily(this);
        }
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
        User currentUser = userManager.getCurrentUser();
        if (currentUser != null) {
            updateProfileDisplay(currentUser);
        } else {
            // Profile not set up, redirect to ProfileActivity
            Toast.makeText(this, "Please complete your profile", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        }
    }

    private void updateProfileDisplay(User user) {
        // Update profile information
        ageText.setText(user.getName()); // Since User class doesn't have age, we'll show name instead
        heightText.setText(String.format("%.1f cm", user.getHeight()));
        weightText.setText(String.format("%.1f kg", user.getWeight()));
        genderText.setText(user.getGender() != null ? user.getGender() : "Not set");
        activityLevelText.setText(user.getActivityLevel() != null ? user.getActivityLevel() : "Not set");

        // Calculate and display BMI
        float height = user.getHeight() / 100; // Convert cm to meters
        float weight = user.getWeight();
        double bmi = weight / (height * height);
        String category = getBMICategory(bmi);
        String description = getBMIDescription(category);

        bmiValueText.setText(String.format("%.1f", bmi));
        bmiCategoryText.setText(category);
        bmiDescriptionText.setText(description);
    }

    private String getBMICategory(double bmi) {
        if (bmi < 18.5) {
            return "Underweight";
        } else if (bmi < 25) {
            return "Normal";
        } else if (bmi < 30) {
            return "Overweight";
        } else {
            return "Obese";
        }
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
            userManager.logoutUser();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                MoodNotificationManager.scheduleDaily(this);
            }
        }
    }
}