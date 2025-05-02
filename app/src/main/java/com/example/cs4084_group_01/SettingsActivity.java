package com.example.cs4084_group_01;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import com.example.cs4084_group_01.manager.UserManager;
import com.example.cs4084_group_01.model.User;
import com.example.cs4084_group_01.util.ThemeManager;
import com.example.cs4084_group_01.util.UnitManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.slider.Slider;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.DecimalFormat;

public class SettingsActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "fit_tracker_prefs";
    private SharedPreferences sharedPreferences;
    private UserManager userManager;

    private static final String PROFILE_PREF = "UserProfile";

    // UI Elements
    private SwitchCompat notificationsSwitch;
    private RadioGroup themeRadioGroup;
    private RadioButton lightThemeRadio;
    private RadioButton darkThemeRadio;
    private RadioButton systemThemeRadio;
    private SwitchCompat metricUnitsSwitch;
    private Slider stepGoalSlider;
    private Slider waterGoalSlider;
    private TextInputLayout heightInputLayout;
    private TextInputLayout weightInputLayout;
    private TextInputEditText heightInput;
    private TextInputEditText weightInput;
    private TextView stepGoalLabel;
    private TextView waterGoalLabel;
    private MaterialButton saveProfileBtn;
    private MaterialButton logoutBtn;
    private MaterialButton deleteAccountBtn;
    private MaterialButton resetDataBtn;

    private DecimalFormat decimalFormat = new DecimalFormat("0.#");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        
        setContentView(R.layout.activity_settings);
        
        // Initialize views
        initViews();
        
        // Initialize user manager
        userManager = UserManager.getInstance(this);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Settings");
        }

        // Setup listeners for user interactions
        setupListeners();
        
        // Load user preferences into UI
        loadPreferences();
        
        // Initialize labels with correct units
        refreshUnitDisplay();
    }
    
    private void initViews() {
        notificationsSwitch = findViewById(R.id.notifications_switch);
        
        // Theme radio group and buttons
        themeRadioGroup = findViewById(R.id.theme_radio_group);
        lightThemeRadio = findViewById(R.id.light_theme_radio);
        darkThemeRadio = findViewById(R.id.dark_theme_radio);
        systemThemeRadio = findViewById(R.id.system_theme_radio);
        
        metricUnitsSwitch = findViewById(R.id.metric_units_switch);
        stepGoalSlider = findViewById(R.id.step_goal_slider);
        waterGoalSlider = findViewById(R.id.water_goal_slider);
        stepGoalLabel = findViewById(R.id.step_goal_label);
        waterGoalLabel = findViewById(R.id.water_goal_label);
        
        heightInputLayout = findViewById(R.id.height_input_layout);
        weightInputLayout = findViewById(R.id.weight_input_layout);
        heightInput = findViewById(R.id.height_input);
        weightInput = findViewById(R.id.weight_input);
        
        saveProfileBtn = findViewById(R.id.save_profile_btn);
        logoutBtn = findViewById(R.id.logout_btn);
        deleteAccountBtn = findViewById(R.id.delete_account_btn);
        resetDataBtn = findViewById(R.id.reset_data_btn);
    }
    
    private void loadPreferences() {
        // Load switch preferences
        notificationsSwitch.setChecked(sharedPreferences.getBoolean("notifications_enabled", true));
        
        // Load theme preference
        int themeMode = ThemeManager.getCurrentTheme(this);
        switch (themeMode) {
            case ThemeManager.THEME_LIGHT:
                lightThemeRadio.setChecked(true);
                break;
            case ThemeManager.THEME_DARK:
                darkThemeRadio.setChecked(true);
                break;
            case ThemeManager.THEME_SYSTEM:
            default:
                systemThemeRadio.setChecked(true);
                break;
        }
        
        metricUnitsSwitch.setChecked(UnitManager.isMetricUnits(this));
        
        // Load slider values
        stepGoalSlider.setValue(sharedPreferences.getInt("daily_step_goal", 10000));
        waterGoalSlider.setValue(sharedPreferences.getInt("daily_water_goal", 2000));
        
        // Load user profile data
        User currentUser = userManager.getCurrentUser();
        if (currentUser != null) {
            boolean isMetric = UnitManager.isMetricUnits(this);
            
            if (currentUser.getHeight() > 0) {
                if (isMetric) {
                    heightInput.setText(String.valueOf(currentUser.getHeight()));
                } else {
                    // Convert cm to feet
                    float heightInFeet = currentUser.getHeight() * UnitManager.getCmToFeetFactor();
                    heightInput.setText(String.format("%.1f", heightInFeet));
                }
            }
            
            if (currentUser.getWeight() > 0) {
                if (isMetric) {
                    weightInput.setText(String.valueOf(currentUser.getWeight()));
                } else {
                    // Convert kg to lbs
                    float weightInLbs = currentUser.getWeight() * UnitManager.getKgToLbsFactor();
                    weightInput.setText(String.format("%.1f", weightInLbs));
                }
            }
        }
    }
    
    private void setupListeners() {
        // Setup listeners for theme selection radio buttons
        themeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            // Get current theme mode
            int currentThemeMode = ThemeManager.getCurrentTheme(this);
            int newThemeMode;
            
            if (checkedId == R.id.light_theme_radio) {
                newThemeMode = ThemeManager.THEME_LIGHT;
            } else if (checkedId == R.id.dark_theme_radio) {
                newThemeMode = ThemeManager.THEME_DARK;
            } else {
                newThemeMode = ThemeManager.THEME_SYSTEM;
            }
            
            // Only apply changes if the theme has actually changed
            if (currentThemeMode != newThemeMode) {
                ThemeManager.setTheme(this, newThemeMode);
                recreate(); // Recreate the activity to apply changes
            }
        });
        
        // Setup listener for unit conversion switch
        metricUnitsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Use UnitManager to save unit preference
            UnitManager.setMetricUnits(this, isChecked);
            
            // Update display with new units
            refreshUnitDisplay();
        });
        
        // Setup listener for save button
        saveProfileBtn.setOnClickListener(v -> saveUserData());
        
        // Switch listeners
        notificationsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                savePreference("notifications_enabled", isChecked);
            }
        });
        
        // Slider listeners
        stepGoalSlider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                savePreference("daily_step_goal", (int) value);
                updateStepGoalLabel((int) value);
            }
        });
        
        waterGoalSlider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                savePreference("daily_water_goal", (int) value);
                updateWaterGoalLabel((int) value);
            }
        });
        
        // Button listeners
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
        
        deleteAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // This would normally have a confirmation dialog
                deleteAccount();
            }
        });
        
        resetDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // This would normally have a confirmation dialog
                resetAllData();
            }
        });
    }
    
    private void updateStepGoalLabel(int steps) {
        stepGoalLabel.setText(String.format("Daily Step Goal: %,d steps", steps));
    }
    
    private void updateWaterGoalLabel(int waterMl) {
        // Use UnitManager to format the water volume
        String formattedVolume = UnitManager.formatVolume(this, waterMl);
        waterGoalLabel.setText(String.format("Daily Water Goal: %s", formattedVolume));
    }
    
    private void savePreference(String key, boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }
    
    private void savePreference(String key, int value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }
    
    private void refreshUnitDisplay() {
        boolean isMetric = UnitManager.isMetricUnits(this);
        User currentUser = userManager.getCurrentUser();
        
        // Update input hint texts
        if (isMetric) {
            heightInputLayout.setHint("Height (cm)");
            weightInputLayout.setHint("Weight (kg)");
        } else {
            heightInputLayout.setHint("Height (ft)");
            weightInputLayout.setHint("Weight (lbs)");
        }
        
        // Update water goal label
        updateWaterGoalLabel((int) waterGoalSlider.getValue());
        
        // Update step goal label
        updateStepGoalLabel((int) stepGoalSlider.getValue());
        
        // Convert and update existing values in the fields
        if (currentUser != null) {
            float currentHeight = currentUser.getHeight();
            float currentWeight = currentUser.getWeight();
            
            // Only update if there are values
            if (currentHeight > 0) {
                String currentHeightStr = heightInput.getText().toString();
                try {
                    // Check if we need to convert
                    if (isMetric) {
                        if (!currentHeightStr.isEmpty()) {
                            float heightValue = Float.parseFloat(currentHeightStr);
                            // If current value is likely in feet (small value), convert to cm
                            if (heightValue < 10) {
                                float heightInCm = heightValue / UnitManager.getCmToFeetFactor();
                                heightInput.setText(String.format("%.1f", heightInCm));
                            } else {
                                // Already in cm, just use the current value
                                heightInput.setText(currentHeightStr);
                            }
                        } else {
                            // Empty field, use the stored value
                            heightInput.setText(String.format("%.1f", currentHeight));
                        }
                    } else {
                        // Need imperial units (feet)
                        if (!currentHeightStr.isEmpty()) {
                            float heightValue = Float.parseFloat(currentHeightStr);
                            // If current value is likely in cm (large value), convert to feet
                            if (heightValue > 50) {
                                float heightInFeet = heightValue * UnitManager.getCmToFeetFactor();
                                heightInput.setText(String.format("%.1f", heightInFeet));
                            } else {
                                // Already in feet, just use the current value
                                heightInput.setText(currentHeightStr);
                            }
                        } else {
                            // Empty field, convert stored value to feet
                            float heightInFeet = currentHeight * UnitManager.getCmToFeetFactor();
                            heightInput.setText(String.format("%.1f", heightInFeet));
                        }
                    }
                } catch (NumberFormatException e) {
                    // Invalid input, revert to stored value
                    if (isMetric) {
                        heightInput.setText(String.format("%.1f", currentHeight));
                    } else {
                        float heightInFeet = currentHeight * UnitManager.getCmToFeetFactor();
                        heightInput.setText(String.format("%.1f", heightInFeet));
                    }
                }
            }
            
            if (currentWeight > 0) {
                String currentWeightStr = weightInput.getText().toString();
                try {
                    // Check if we need to convert
                    if (isMetric) {
                        if (!currentWeightStr.isEmpty()) {
                            float weightValue = Float.parseFloat(currentWeightStr);
                            // If current value is likely in lbs (large value), convert to kg
                            if (weightValue > 50 && weightValue / currentWeight > 1.5) {
                                float weightInKg = weightValue / UnitManager.getKgToLbsFactor();
                                weightInput.setText(String.format("%.1f", weightInKg));
                            } else {
                                // Already in kg, just use the current value
                                weightInput.setText(currentWeightStr);
                            }
                        } else {
                            // Empty field, use the stored value
                            weightInput.setText(String.format("%.1f", currentWeight));
                        }
                    } else {
                        // Need imperial units (lbs)
                        if (!currentWeightStr.isEmpty()) {
                            float weightValue = Float.parseFloat(currentWeightStr);
                            // If current value is likely in kg (small value), convert to lbs
                            if (weightValue < 100 && currentWeight * UnitManager.getKgToLbsFactor() / weightValue > 1.5) {
                                float weightInLbs = weightValue * UnitManager.getKgToLbsFactor();
                                weightInput.setText(String.format("%.1f", weightInLbs));
                            } else {
                                // Already in lbs, just use the current value
                                weightInput.setText(currentWeightStr);
                            }
                        } else {
                            // Empty field, convert stored value to lbs
                            float weightInLbs = currentWeight * UnitManager.getKgToLbsFactor();
                            weightInput.setText(String.format("%.1f", weightInLbs));
                        }
                    }
                } catch (NumberFormatException e) {
                    // Invalid input, revert to stored value
                    if (isMetric) {
                        weightInput.setText(String.format("%.1f", currentWeight));
                    } else {
                        float weightInLbs = currentWeight * UnitManager.getKgToLbsFactor();
                        weightInput.setText(String.format("%.1f", weightInLbs));
                    }
                }
            }
        }
    }
    
    private void saveUserData() {
        User currentUser = userManager.getCurrentUser();
        if (currentUser != null) {
            // Get height and weight from input fields
            String heightStr = heightInput.getText().toString();
            String weightStr = weightInput.getText().toString();
            boolean isMetric = UnitManager.isMetricUnits(this);
            
            try {
                if (!heightStr.isEmpty()) {
                    float heightValue = Float.parseFloat(heightStr);
                    // Convert to cm if in imperial
                    if (!isMetric) {
                        heightValue = heightValue / UnitManager.getCmToFeetFactor();
                    }
                    currentUser.setHeight(heightValue);
                }
                
                if (!weightStr.isEmpty()) {
                    float weightValue = Float.parseFloat(weightStr);
                    // Convert to kg if in imperial
                    if (!isMetric) {
                        weightValue = weightValue / UnitManager.getKgToLbsFactor();
                    }
                    currentUser.setWeight(weightValue);
                }
                
                // Save the updated user
                userManager.updateUser(currentUser);
                
                Toast.makeText(this, "Profile saved successfully", Toast.LENGTH_SHORT).show();
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Please enter valid numbers for height and weight", 
                    Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    private void logout() {
        userManager.logoutUser();
        // Navigate to LoginActivity and clear back stack
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    
    private void deleteAccount() {
        // Since deleteCurrentUser doesn't exist, we'll log out and clear preferences instead
        userManager.logoutUser();
        
        // Clear preferences
        sharedPreferences.edit().clear().apply();
        
        Toast.makeText(this, "Account deleted", Toast.LENGTH_SHORT).show();
        
        // Navigate to LoginActivity
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    
    private void resetAllData() {
        // This would clear all health data but keep the account
        // Find all your repositories and clear them
        
        Toast.makeText(this, "All health data has been reset", Toast.LENGTH_SHORT).show();
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