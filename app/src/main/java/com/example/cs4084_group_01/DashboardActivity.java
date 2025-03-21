package com.example.cs4084_group_01;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.cs4084_group_01.viewmodel.ProfileViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class DashboardActivity extends AppCompatActivity {
    private ProfileViewModel viewModel;
    private TextView ageText, heightText, weightText, genderText, activityLevelText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        // Initialize views
        ageText = findViewById(R.id.profileAge);
        heightText = findViewById(R.id.profileHeight);
        weightText = findViewById(R.id.profileWeight);
        genderText = findViewById(R.id.profileGender);
        activityLevelText = findViewById(R.id.profileActivityLevel);

        // Setup edit button
        FloatingActionButton editButton = findViewById(R.id.editProfileButton);
        editButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        });

        // Observe profile data
        viewModel.getProfileLiveData().observe(this, profile -> {
            if (profile != null) {
                ageText.setText("Age: " + profile.getAge());
                heightText.setText("Height: " + profile.getHeight() + " cm");
                weightText.setText("Weight: " + profile.getWeight() + " kg");
                genderText.setText("Gender: " + profile.getGender());
                activityLevelText.setText("Activity Level: " + profile.getActivityLevel());
            }
        });
    }
} 