package com.example.cs4084_group_01;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.chip.ChipGroup;
import com.example.cs4084_group_01.viewmodel.ProfileViewModel;

public class ProfileActivity extends AppCompatActivity {
    private ProfileViewModel viewModel;
    private TextInputEditText ageInput, heightInput, weightInput;
    private ChipGroup genderGroup, activityGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        // Initialize views
        ageInput = findViewById(R.id.ageInput);
        heightInput = findViewById(R.id.heightInput);
        weightInput = findViewById(R.id.weightInput);
        genderGroup = findViewById(R.id.genderGroup);
        activityGroup = findViewById(R.id.activityGroup);
        Button saveButton = findViewById(R.id.saveButton);

        // Load existing profile data
        viewModel.loadProfile();
        viewModel.getProfileLiveData().observe(this, profile -> {
            if (profile != null) {
                ageInput.setText(String.valueOf(profile.getAge()));
                heightInput.setText(String.valueOf(profile.getHeight()));
                weightInput.setText(String.valueOf(profile.getWeight()));
                // Set gender and activity level chips
                if (profile.getGender() != null) {
                    int genderChipId = getGenderChipId(profile.getGender());
                    if (genderChipId != View.NO_ID) {
                        genderGroup.check(genderChipId);
                    }
                }
                if (profile.getActivityLevel() != null) {
                    int activityChipId = getActivityChipId(profile.getActivityLevel());
                    if (activityChipId != View.NO_ID) {
                        activityGroup.check(activityChipId);
                    }
                }
            }
        });

        saveButton.setOnClickListener(v -> saveProfile());
    }

    private void saveProfile() {
        // Get input values
        String ageStr = ageInput.getText().toString();
        String heightStr = heightInput.getText().toString();
        String weightStr = weightInput.getText().toString();

        // Validate inputs
        if (ageStr.isEmpty() || heightStr.isEmpty() || weightStr.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int age = Integer.parseInt(ageStr);
        float height = Float.parseFloat(heightStr);
        float weight = Float.parseFloat(weightStr);

        // Get selected gender and activity level
        String gender = getSelectedGender();
        String activityLevel = getSelectedActivityLevel();

        // Save profile
        viewModel.saveProfile(age, height, weight, gender, activityLevel);

        // Navigate to Dashboard
        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
        finish();
    }

    private String getSelectedGender() {
        int selectedChipId = genderGroup.getCheckedChipId();
        if (selectedChipId == View.NO_ID) return null;
        return findViewById(selectedChipId).getTag().toString();
    }

    private String getSelectedActivityLevel() {
        int selectedChipId = activityGroup.getCheckedChipId();
        if (selectedChipId == View.NO_ID) return null;
        return findViewById(selectedChipId).getTag().toString();
    }

    private int getGenderChipId(String gender) {
        for (int i = 0; i < genderGroup.getChildCount(); i++) {
            View child = genderGroup.getChildAt(i);
            if (child.getTag().toString().equals(gender)) {
                return child.getId();
            }
        }
        return View.NO_ID;
    }

    private int getActivityChipId(String activityLevel) {
        for (int i = 0; i < activityGroup.getChildCount(); i++) {
            View child = activityGroup.getChildAt(i);
            if (child.getTag().toString().equals(activityLevel)) {
                return child.getId();
            }
        }
        return View.NO_ID;
    }
} 