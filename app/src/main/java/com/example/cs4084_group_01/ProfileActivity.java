package com.example.cs4084_group_01;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.chip.ChipGroup;
import com.example.cs4084_group_01.viewmodel.ProfileViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import android.widget.TextView;
import com.example.cs4084_group_01.model.User;

public class ProfileActivity extends AppCompatActivity {
    private ProfileViewModel viewModel;
    private TextInputLayout nameInputLayout;
    private TextInputEditText nameInput;
    private TextInputLayout heightInputLayout;
    private TextInputEditText heightInput;
    private TextInputLayout weightInputLayout;
    private TextInputEditText weightInput;
    private MaterialButton saveButton;
    private MaterialCardView bmiCard;
    private TextView bmiValueText;
    private TextView bmiCategoryText;
    private ChipGroup genderGroup, activityGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Set up toolbar with back button
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        
        initializeViews();
        setupClickListeners();
        observeViewModel();
    }

    private void initializeViews() {
        nameInputLayout = findViewById(R.id.nameInputLayout);
        nameInput = findViewById(R.id.nameInput);
        heightInputLayout = findViewById(R.id.heightInputLayout);
        heightInput = findViewById(R.id.heightInput);
        weightInputLayout = findViewById(R.id.weightInputLayout);
        weightInput = findViewById(R.id.weightInput);
        saveButton = findViewById(R.id.saveButton);
        bmiCard = findViewById(R.id.bmiCard);
        bmiValueText = findViewById(R.id.bmiValueText);
        bmiCategoryText = findViewById(R.id.bmiCategoryText);
        genderGroup = findViewById(R.id.genderGroup);
        activityGroup = findViewById(R.id.activityGroup);
    }

    private void setupClickListeners() {
        saveButton.setOnClickListener(v -> handleSave());
    }

    private void observeViewModel() {
        viewModel.getCurrentUser().observe(this, user -> {
            if (user != null) {
                nameInput.setText(user.getName());
                if (user.getHeight() > 0) {
                    heightInput.setText(String.valueOf(user.getHeight()));
                }
                if (user.getWeight() > 0) {
                    weightInput.setText(String.valueOf(user.getWeight()));
                }
                updateBMI(user.getHeight(), user.getWeight());
                // Set gender and activity level chips
                if (user.getGender() != null) {
                    int genderChipId = getGenderChipId(user.getGender());
                    if (genderChipId != View.NO_ID) {
                        genderGroup.check(genderChipId);
                    }
                }
                if (user.getActivityLevel() != null) {
                    int activityChipId = getActivityChipId(user.getActivityLevel());
                    if (activityChipId != View.NO_ID) {
                        activityGroup.check(activityChipId);
                    }
                }
            }
        });
    }

    private void handleSave() {
        String name = nameInput.getText().toString().trim();
        String heightStr = heightInput.getText().toString().trim();
        String weightStr = weightInput.getText().toString().trim();

        if (name.isEmpty()) {
            nameInputLayout.setError("Name is required");
            return;
        }

        float height = 0f;
        float weight = 0f;

        try {
            if (!heightStr.isEmpty()) {
                height = Float.parseFloat(heightStr);
                if (height <= 0) throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            heightInputLayout.setError("Enter a valid height in cm");
            return;
        }

        try {
            if (!weightStr.isEmpty()) {
                weight = Float.parseFloat(weightStr);
                if (weight <= 0) throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            weightInputLayout.setError("Enter a valid weight in kg");
            return;
        }

        // Store the final values to use in lambda
        final float finalHeight = height;
        final float finalWeight = weight;
        
        // Get selected gender and activity level
        String gender = getSelectedGender();
        String activityLevel = getSelectedActivityLevel();

        // We need to get the current user to update all fields
        User user = viewModel.getCurrentUser().getValue();
        if (user != null) {
            user.setName(name);
            user.setHeight(finalHeight);
            user.setWeight(finalWeight);
            user.setGender(gender);
            user.setActivityLevel(activityLevel);
            
            viewModel.updateUser(user).observe(this, success -> {
                if (success) {
                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    updateBMI(finalHeight, finalWeight);
                    // Close this activity and return to dashboard
                    finish();
                } else {
                    Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Failed to update profile: User not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateBMI(float height, float weight) {
        if (height > 0 && weight > 0) {
            float heightInMeters = height / 100f;
            float bmi = weight / (heightInMeters * heightInMeters);
            bmiValueText.setText(String.format("%.1f", bmi));
            bmiCategoryText.setText(getBMICategory(bmi));
            bmiCard.setVisibility(android.view.View.VISIBLE);
        } else {
            bmiCard.setVisibility(android.view.View.GONE);
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

    private String getSelectedGender() {
        int selectedChipId = genderGroup.getCheckedChipId();
        if (selectedChipId == View.NO_ID) return null;
        View view = findViewById(selectedChipId);
        if (view == null || view.getTag() == null) return null;
        return view.getTag().toString();
    }

    private String getSelectedActivityLevel() {
        int selectedChipId = activityGroup.getCheckedChipId();
        if (selectedChipId == View.NO_ID) return null;
        View view = findViewById(selectedChipId);
        if (view == null || view.getTag() == null) return null;
        return view.getTag().toString();
    }

    private int getGenderChipId(String gender) {
        if (gender == null) return View.NO_ID;
        for (int i = 0; i < genderGroup.getChildCount(); i++) {
            View child = genderGroup.getChildAt(i);
            if (child != null && child.getTag() != null && 
                gender.equals(child.getTag().toString())) {
                return child.getId();
            }
        }
        return View.NO_ID;
    }

    private int getActivityChipId(String activityLevel) {
        if (activityLevel == null) return View.NO_ID;
        for (int i = 0; i < activityGroup.getChildCount(); i++) {
            View child = activityGroup.getChildAt(i);
            if (child != null && child.getTag() != null && 
                activityLevel.equals(child.getTag().toString())) {
                return child.getId();
            }
        }
        return View.NO_ID;
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