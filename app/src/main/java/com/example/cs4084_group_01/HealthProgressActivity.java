package com.example.cs4084_group_01;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.cs4084_group_01.adapter.ViewPagerAdapter;
import com.example.cs4084_group_01.model.GoalType;
import com.example.cs4084_group_01.viewmodel.GoalViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HealthProgressActivity extends BaseActivity {
    private GoalViewModel viewModel;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private FloatingActionButton addGoalFab;
    private SimpleDateFormat dateFormat;
    private Calendar targetDateCalendar;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_progress);
        
        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(GoalViewModel.class);
        
        // Initialize date format
        dateFormat = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());
        
        // Set up toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        
        // Set up ViewPager and TabLayout
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        
        setupViewPager();
        
        // Set up FAB
        addGoalFab = findViewById(R.id.addGoalFab);
        addGoalFab.setOnClickListener(v -> showCreateGoalDialog());
    }
    
    private void setupViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        adapter.addFragment(GoalsListFragment.newInstance(false), "Active");
        adapter.addFragment(GoalsListFragment.newInstance(true), "Completed");
        viewPager.setAdapter(adapter);
        
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("Active");
            } else {
                tab.setText("Completed");
            }
        }).attach();
        
        // Hide FAB when on completed goals tab
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    addGoalFab.show();
                } else {
                    addGoalFab.hide();
                }
            }
        });
    }
    
    private void showCreateGoalDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_create_goal, null);
        
        TextInputEditText goalTitleInput = dialogView.findViewById(R.id.goalTitleInput);
        AutoCompleteTextView goalTypeDropdown = dialogView.findViewById(R.id.goalTypeDropdown);
        TextInputEditText targetValueInput = dialogView.findViewById(R.id.targetValueInput);
        TextInputEditText unitInput = dialogView.findViewById(R.id.unitInput);
        TextInputEditText targetDateInput = dialogView.findViewById(R.id.targetDateInput);
        TextInputEditText notesInput = dialogView.findViewById(R.id.notesInput);
        MaterialButton cancelButton = dialogView.findViewById(R.id.cancelButton);
        MaterialButton createButton = dialogView.findViewById(R.id.createButton);
        
        // Set up goal type dropdown
        List<String> goalTypeOptions = new ArrayList<>();
        Map<String, GoalType> goalTypeMap = new HashMap<>();
        
        for (GoalType type : GoalType.values()) {
            if (type != GoalType.CUSTOM) { // Exclude CUSTOM for simplicity
                String displayName = type.getDisplayName();
                goalTypeOptions.add(displayName);
                goalTypeMap.put(displayName, type);
            }
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, goalTypeOptions);
        goalTypeDropdown.setAdapter(adapter);
        
        // Initialize calendar for date picker
        targetDateCalendar = Calendar.getInstance();
        targetDateCalendar.add(Calendar.DAY_OF_MONTH, 30); // Default to one month ahead
        targetDateInput.setText(dateFormat.format(targetDateCalendar.getTime()));
        
        // Set up date picker
        targetDateInput.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {
                        targetDateCalendar.set(Calendar.YEAR, year);
                        targetDateCalendar.set(Calendar.MONTH, month);
                        targetDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        targetDateInput.setText(dateFormat.format(targetDateCalendar.getTime()));
                    },
                    targetDateCalendar.get(Calendar.YEAR),
                    targetDateCalendar.get(Calendar.MONTH),
                    targetDateCalendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
            datePickerDialog.show();
        });
        
        // Update unit based on selected goal type
        goalTypeDropdown.setOnItemClickListener((parent, view, position, id) -> {
            String selectedTypeName = (String) parent.getItemAtPosition(position);
            GoalType selectedType = goalTypeMap.get(selectedTypeName);
            if (selectedType != null) {
                unitInput.setText(selectedType.getUnit());
            }
        });
        
        // Set default goal type
        goalTypeDropdown.setText(goalTypeOptions.get(0), false);
        unitInput.setText(GoalType.STEPS.getUnit());
        
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();
        
        // Set up button click listeners
        cancelButton.setOnClickListener(v -> dialog.dismiss());
        
        createButton.setOnClickListener(v -> {
            // Validate inputs
            String title = goalTitleInput.getText().toString().trim();
            String selectedTypeName = goalTypeDropdown.getText().toString().trim();
            String targetValueStr = targetValueInput.getText().toString().trim();
            
            if (title.isEmpty()) {
                Toast.makeText(this, "Please enter a goal title", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (selectedTypeName.isEmpty()) {
                Toast.makeText(this, "Please select a goal type", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (targetValueStr.isEmpty()) {
                Toast.makeText(this, "Please enter a target value", Toast.LENGTH_SHORT).show();
                return;
            }
            
            try {
                GoalType selectedType = goalTypeMap.get(selectedTypeName);
                double targetValue = Double.parseDouble(targetValueStr);
                String notes = notesInput.getText().toString().trim();
                
                // Create the goal
                viewModel.addGoal(title, selectedType, targetValue, targetDateCalendar.getTime());
                
                dialog.dismiss();
                Toast.makeText(this, "Goal created successfully", Toast.LENGTH_SHORT).show();
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Please enter a valid number for target value", Toast.LENGTH_SHORT).show();
            }
        });
        
        dialog.show();
    }
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 