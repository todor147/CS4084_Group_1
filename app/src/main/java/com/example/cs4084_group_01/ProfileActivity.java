package com.example.cs4084_group_01;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.cs4084_group_01.viewmodel.ProfileViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class ProfileActivity extends AppCompatActivity {
    private ProfileViewModel viewModel;
    private TextInputEditText ageInput;
    private TextInputEditText heightInput;
    private TextInputEditText weightInput;
    private AutoCompleteTextView genderDropdown;
    private AutoCompleteTextView activityLevelDropdown;
    private MaterialButton saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        initializeViews();
        setupDropdowns();
        observeViewModel();
        setupSaveButton();
    }

    private void initializeViews() {
        // Basic Information
        ageInput = findViewById(R.id.ageInput);
        heightInput = findViewById(R.id.heightInput);
        weightInput = findViewById(R.id.weightInput);
        genderDropdown = findViewById(R.id.genderDropdown);
        activityLevelDropdown = findViewById(R.id.activityLevelDropdown);
        saveButton = findViewById(R.id.saveButton);
    }

    private void setupDropdowns() {
        // Setup gender dropdown
        String[] genders = new String[]{"Male", "Female", "Other"};
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, genders);
        genderDropdown.setAdapter(genderAdapter);

        // Setup activity level dropdown
        String[] activityLevels = new String[]{"Sedentary", "Light", "Moderate", "Very Active"};
        ArrayAdapter<String> activityAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, activityLevels);
        activityLevelDropdown.setAdapter(activityAdapter);
    }

    private void observeViewModel() {
        viewModel.getProfileLiveData().observe(this, profile -> {
            if (profile != null) {
                ageInput.setText(String.valueOf(profile.getAge()));
                heightInput.setText(String.valueOf(profile.getHeight()));
                weightInput.setText(String.valueOf(profile.getWeight()));
                genderDropdown.setText(profile.getGender(), false);
                activityLevelDropdown.setText(profile.getActivityLevel(), false);
            }
        });
    }

    private void setupSaveButton() {
        saveButton.setOnClickListener(v -> saveProfile());
    }

    private void saveProfile() {
        try {
            int age = Integer.parseInt(ageInput.getText().toString());
            float height = Float.parseFloat(heightInput.getText().toString());
            float weight = Float.parseFloat(weightInput.getText().toString());
            String gender = genderDropdown.getText().toString();
            String activityLevel = activityLevelDropdown.getText().toString();

            viewModel.saveProfile(age, height, weight, gender, activityLevel);

            // Show success message
            Toast.makeText(this, "Profile saved successfully!", Toast.LENGTH_SHORT).show();

            // Navigate to dashboard
            Intent intent = new Intent(this, DashboardActivity.class);
            startActivity(intent);
            finish(); // This closes the profile activity so user can't go back to it using back button
        } catch (NumberFormatException e) {
            // Show error message if number parsing fails
            Toast.makeText(this, "Please enter valid numbers for age, height, and weight", Toast.LENGTH_LONG).show();
        }
    }
} 