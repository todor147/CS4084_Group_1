package com.example.cs4084_group_01;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import java.util.ArrayList;
import java.util.List;

import com.example.cs4084_group_01.manager.UserManager;
import com.example.cs4084_group_01.model.User;
import com.example.cs4084_group_01.model.Feature;
import com.example.cs4084_group_01.adapter.FeaturesAdapter;

public class DashboardActivity extends AppCompatActivity {
    private TextView greetingText;
    private TextView userNameText;
    private RecyclerView featuresGrid;
    private UserManager userManager;
    private MaterialCardView quickActionWater;
    private MaterialCardView quickActionSteps;
    private MaterialCardView bmiDashboardCard;
    private TextView bmiValueDashboard;
    private TextView bmiCategoryDashboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize views
        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        greetingText = findViewById(R.id.greetingText);
        userNameText = findViewById(R.id.userNameText);
        featuresGrid = findViewById(R.id.featuresGrid);
        quickActionWater = findViewById(R.id.quickActionWater);
        quickActionSteps = findViewById(R.id.quickActionSteps);
        bmiDashboardCard = findViewById(R.id.bmiDashboardCard);
        bmiValueDashboard = findViewById(R.id.bmiValueDashboard);
        bmiCategoryDashboard = findViewById(R.id.bmiCategoryDashboard);

        // Set up toolbar
        setSupportActionBar(toolbar);

        // Initialize UserManager
        userManager = UserManager.getInstance(this);

        // Update welcome message
        updateWelcomeMessage();
        
        // Update BMI display
        updateBMIDisplay();

        // Set up features grid
        setupFeaturesGrid();

        // Set up quick actions
        setupQuickActions();
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
            greetingText.setText(greeting);
            
            // Use name if available, otherwise use email
            String displayName = name != null && !name.isEmpty() ? name : email.split("@")[0];
            userNameText.setText(displayName);
        } else {
            greetingText.setText("Welcome");
            userNameText.setText("Guest");
        }
    }

    private void setupFeaturesGrid() {
        List<Feature> features = new ArrayList<>();
        features.add(new Feature("Water Tracking", R.drawable.ic_water, WaterTrackingActivity.class));
        features.add(new Feature("Step Counter", R.drawable.ic_directions_walk, StepCounterActivity.class));
        features.add(new Feature("Mood Tracker", R.drawable.ic_mood, MoodTrackerActivity.class));
        features.add(new Feature("Profile", R.drawable.ic_person, ProfileActivity.class));

        FeaturesAdapter adapter = new FeaturesAdapter(features, this);
        featuresGrid.setLayoutManager(new GridLayoutManager(this, 2));
        featuresGrid.setAdapter(adapter);
    }

    private void setupQuickActions() {
        quickActionWater.setOnClickListener(v -> {
            Intent intent = new Intent(this, WaterTrackingActivity.class);
            intent.putExtra("quick_add", true);
            startActivity(intent);
        });

        quickActionSteps.setOnClickListener(v -> {
            Intent intent = new Intent(this, StepCounterActivity.class);
            intent.putExtra("quick_view", true);
            startActivity(intent);
        });

        // Setup mood selection button
        MaterialButton moodSelectionButton = findViewById(R.id.moodSelectionButton);
        moodSelectionButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, MoodSelectionActivity.class);
            startActivity(intent);
        });
    }

    private void updateBMIDisplay() {
        User currentUser = userManager.getCurrentUser();
        if (currentUser != null && currentUser.getHeight() > 0 && currentUser.getWeight() > 0) {
            float heightInMeters = currentUser.getHeight() / 100f;
            float bmi = currentUser.getWeight() / (heightInMeters * heightInMeters);
            
            bmiValueDashboard.setText(String.format("%.1f", bmi));
            bmiCategoryDashboard.setText(getBMICategory(bmi));
            bmiDashboardCard.setVisibility(View.VISIBLE);
        } else {
            bmiDashboardCard.setVisibility(View.GONE);
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
        if (item.getItemId() == R.id.action_logout) {
            userManager.logoutUser();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 