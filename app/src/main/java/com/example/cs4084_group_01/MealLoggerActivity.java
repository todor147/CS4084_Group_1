package com.example.cs4084_group_01;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cs4084_group_01.adapter.MealHistoryAdapter;
import com.example.cs4084_group_01.model.FoodGroup;
import com.example.cs4084_group_01.model.MealEntry;
import com.example.cs4084_group_01.model.MealType;
import com.example.cs4084_group_01.viewmodel.MealViewModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MealLoggerActivity extends BaseActivity {
    
    private MealViewModel mealViewModel;
    private CalendarView calendarView;
    private TextView dateText;
    private TextView foodGroupSummaryText;
    private RecyclerView mealHistoryRecyclerView;
    private MealHistoryAdapter mealHistoryAdapter;
    private FloatingActionButton addMealFab;
    
    private SimpleDateFormat dateFormat;
    private Date currentDate;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_logger);
        
        // Set up toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        
        // Initialize ViewModel
        mealViewModel = new ViewModelProvider(this).get(MealViewModel.class);
        
        // Initialize date format
        dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
        currentDate = new Date();
        
        // Initialize views
        calendarView = findViewById(R.id.calendarView);
        dateText = findViewById(R.id.dateText);
        foodGroupSummaryText = findViewById(R.id.foodGroupSummaryText);
        mealHistoryRecyclerView = findViewById(R.id.mealHistoryRecyclerView);
        addMealFab = findViewById(R.id.addMealFab);
        
        // Set current date text
        dateText.setText(dateFormat.format(currentDate));
        
        // Set up RecyclerView
        mealHistoryAdapter = new MealHistoryAdapter(this, new ArrayList<>());
        mealHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mealHistoryRecyclerView.setAdapter(mealHistoryAdapter);
        
        // Set up calendar view
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar selectedCalendar = Calendar.getInstance();
            selectedCalendar.set(year, month, dayOfMonth);
            currentDate = selectedCalendar.getTime();
            dateText.setText(dateFormat.format(currentDate));
            mealViewModel.setCurrentDate(currentDate);
        });
        
        // Set up FAB
        addMealFab.setOnClickListener(v -> showAddMealDialog());
        
        // Observe meal entries for current date
        mealViewModel.getCurrentDateEntries().observe(this, entries -> {
            if (entries == null || entries.isEmpty()) {
                foodGroupSummaryText.setText("You haven't logged any meals today");
            } else {
                updateNutritionSummary();
            }
            
            mealHistoryAdapter.updateData(entries != null ? entries : new ArrayList<>());
        });
    }
    
    private void updateNutritionSummary() {
        Map<FoodGroup, Integer> foodGroupsBreakdown = mealViewModel.getFoodGroupsBreakdown().getValue();
        Map<MealType, Integer> mealTypeBreakdown = mealViewModel.getMealTypeBreakdown().getValue();
        List<MealEntry> entries = mealViewModel.getCurrentDateEntries().getValue();
        
        if (entries == null || entries.isEmpty()) {
            foodGroupSummaryText.setText("You haven't logged any meals today");
            return;
        }
        
        StringBuilder summary = new StringBuilder();
        
        // Calculate total calories
        int totalCalories = 0;
        for (MealEntry entry : entries) {
            totalCalories += entry.getCalories();
        }
        
        // Add meal type summary
        summary.append("Meals logged today: ");
        if (mealTypeBreakdown != null && !mealTypeBreakdown.isEmpty()) {
            int totalMeals = 0;
            for (Integer count : mealTypeBreakdown.values()) {
                totalMeals += count;
            }
            summary.append(totalMeals).append("\n");
            summary.append("Total calories: ").append(totalCalories).append("\n\n");
            
            summary.append("Food groups in your meals:\n");
            
            // Add food group summary
            for (Map.Entry<FoodGroup, Integer> entry : foodGroupsBreakdown.entrySet()) {
                summary.append("• ").append(entry.getKey().getDisplayName())
                        .append(": ").append(entry.getValue()).append("\n");
            }
        }
        
        foodGroupSummaryText.setText(summary.toString());
    }
    
    private void showAddMealDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_meal);
        
        // Configure dialog to use most of the screen width
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(
                    (int) (getResources().getDisplayMetrics().widthPixels * 0.95),
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            );
            window.setBackgroundDrawableResource(R.drawable.dialog_background);
        }
        
        RadioGroup mealTypeRadioGroup = dialog.findViewById(R.id.mealTypeRadioGroup);
        TextInputEditText mealDescriptionInput = dialog.findViewById(R.id.mealDescriptionInput);
        TextInputEditText caloriesInput = dialog.findViewById(R.id.caloriesInput);
        ChipGroup foodGroupsChipGroup = dialog.findViewById(R.id.foodGroupsChipGroup);
        TextInputEditText notesInput = dialog.findViewById(R.id.notesInput);
        Button saveButton = dialog.findViewById(R.id.saveButton);
        Button cancelButton = dialog.findViewById(R.id.cancelButton);
        
        // Set breakfast as default
        ((RadioButton) dialog.findViewById(R.id.radioBreakfast)).setChecked(true);
        
        // Set up chip IDs and tags for food groups
        setupFoodGroupChips(foodGroupsChipGroup);
        
        saveButton.setOnClickListener(v -> {
            // Get meal type
            int selectedMealTypeId = mealTypeRadioGroup.getCheckedRadioButtonId();
            MealType mealType;
            
            if (selectedMealTypeId == R.id.radioBreakfast) {
                mealType = MealType.BREAKFAST;
            } else if (selectedMealTypeId == R.id.radioLunch) {
                mealType = MealType.LUNCH;
            } else if (selectedMealTypeId == R.id.radioDinner) {
                mealType = MealType.DINNER;
            } else {
                mealType = MealType.SNACK;
            }
            
            // Get meal description
            String description = mealDescriptionInput.getText() != null 
                    ? mealDescriptionInput.getText().toString() : "";
            
            if (description.isEmpty()) {
                Toast.makeText(this, "Please enter a meal description", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Get calories
            int calories = 0;
            try {
                String caloriesText = caloriesInput.getText() != null 
                        ? caloriesInput.getText().toString() : "0";
                if (!caloriesText.isEmpty()) {
                    calories = Integer.parseInt(caloriesText);
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Please enter a valid calorie amount", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Get selected food groups
            List<FoodGroup> selectedFoodGroups = new ArrayList<>();
            for (int i = 0; i < foodGroupsChipGroup.getChildCount(); i++) {
                Chip chip = (Chip) foodGroupsChipGroup.getChildAt(i);
                if (chip.isChecked()) {
                    FoodGroup foodGroup = FoodGroup.valueOf(chip.getTag().toString());
                    selectedFoodGroups.add(foodGroup);
                }
            }
            
            // Get notes
            String notes = notesInput.getText() != null 
                    ? notesInput.getText().toString() : "";
            
            // Create and save meal entry
            MealEntry mealEntry = new MealEntry(mealType, currentDate, description, 
                    selectedFoodGroups, notes, calories);
            mealViewModel.saveMealEntry(mealEntry);
            
            dialog.dismiss();
            Toast.makeText(this, "Meal saved successfully", Toast.LENGTH_SHORT).show();
        });
        
        cancelButton.setOnClickListener(v -> dialog.dismiss());
        
        dialog.show();
    }
    
    private void setupFoodGroupChips(ChipGroup chipGroup) {
        // Set tag values for food groups
        Chip chipFruits = chipGroup.findViewById(R.id.chipFruits);
        chipFruits.setTag(FoodGroup.FRUITS.name());
        
        Chip chipVegetables = chipGroup.findViewById(R.id.chipVegetables);
        chipVegetables.setTag(FoodGroup.VEGETABLES.name());
        
        Chip chipGrains = chipGroup.findViewById(R.id.chipGrains);
        chipGrains.setTag(FoodGroup.GRAINS.name());
        
        Chip chipProtein = chipGroup.findViewById(R.id.chipProtein);
        chipProtein.setTag(FoodGroup.PROTEIN.name());
        
        Chip chipDairy = chipGroup.findViewById(R.id.chipDairy);
        chipDairy.setTag(FoodGroup.DAIRY.name());
        
        Chip chipFats = chipGroup.findViewById(R.id.chipFats);
        chipFats.setTag(FoodGroup.FATS.name());
        
        Chip chipSweets = chipGroup.findViewById(R.id.chipSweets);
        chipSweets.setTag(FoodGroup.SWEETS.name());
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.common_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.action_health_summary) {
            Intent intent = new Intent(this, HealthDashboardActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 