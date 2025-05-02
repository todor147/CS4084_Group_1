package com.example.cs4084_group_01;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import com.example.cs4084_group_01.model.WaterIntake;
import com.example.cs4084_group_01.util.DemoDataProvider;
import com.example.cs4084_group_01.viewmodel.WaterViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WaterTrackingActivity extends BaseActivity {
    private WaterViewModel waterViewModel;
    private CircularProgressIndicator waterProgressIndicator;
    private TextView waterProgressText;
    private TextView waterAmountText;
    private TextView goalText;
    private ListView historyListView;
    private ArrayAdapter<String> historyAdapter;
    private List<String> historyDisplayList;
    private List<WaterIntake> historyDataList;
    private SimpleDateFormat timeFormat;
    private SimpleDateFormat dateFormat;
    private boolean isDemoMode = false;
    
    // Demo mode variables
    private WaterIntake demoDayIntake;
    private List<WaterIntake> demoHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_tracking);

        // Check if in demo mode
        isDemoMode = DemoDataProvider.isDemoModeEnabled(this);

        // Initialize ViewModel (only if not in demo mode)
        if (!isDemoMode) {
            waterViewModel = new ViewModelProvider(this).get(WaterViewModel.class);
        } else {
            setupDemoData();
        }

        // Initialize time formatters
        timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        // Initialize UI components
        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        waterProgressIndicator = findViewById(R.id.waterProgressIndicator);
        waterProgressText = findViewById(R.id.waterProgressText);
        waterAmountText = findViewById(R.id.waterAmountText);
        goalText = findViewById(R.id.goalText);
        historyListView = findViewById(R.id.historyListView);
        
        // Set up list adapter for water history
        historyDisplayList = new ArrayList<>();
        historyDataList = new ArrayList<>();
        historyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, historyDisplayList);
        historyListView.setAdapter(historyAdapter);
        
        // Set up listeners for history items
        historyListView.setOnItemClickListener((parent, view, position, id) -> {
            if (historyDataList.isEmpty() || position >= historyDataList.size()) {
                return; // No data or invalid position
            }
            
            if (isDemoMode) {
                Toast.makeText(this, "Demo mode: Cannot edit entries", Toast.LENGTH_SHORT).show();
                return;
            }
            
            WaterIntake intake = historyDataList.get(position);
            if (isToday(intake.getDate())) {
                showWaterIntakeOptionsDialog(intake, position);
            } else {
                Toast.makeText(this, "You can only edit today's entries", Toast.LENGTH_SHORT).show();
            }
        });

        // Set up buttons for adding water
        Button add100ml = findViewById(R.id.add100ml);
        Button add250ml = findViewById(R.id.add250ml);
        Button add500ml = findViewById(R.id.add500ml);
        Button addCustom = findViewById(R.id.addCustom);
        Button setGoalButton = findViewById(R.id.setGoalButton);
        Button reduceWaterButton = findViewById(R.id.reduceWaterButton);

        // Set up click listeners
        if (isDemoMode) {
            // In demo mode, just show toasts
            add100ml.setOnClickListener(v -> showDemoToast("Added 100ml in demo mode"));
            add250ml.setOnClickListener(v -> showDemoToast("Added 250ml in demo mode"));
            add500ml.setOnClickListener(v -> showDemoToast("Added 500ml in demo mode"));
            addCustom.setOnClickListener(v -> showDemoToast("Added custom amount in demo mode"));
            setGoalButton.setOnClickListener(v -> showDemoToast("Set goal in demo mode"));
            if (reduceWaterButton != null) {
                reduceWaterButton.setOnClickListener(v -> showDemoToast("Reduced water in demo mode"));
            }
        } else {
            // Normal mode functionality
            add100ml.setOnClickListener(v -> addWater(100));
            add250ml.setOnClickListener(v -> addWater(250));
            add500ml.setOnClickListener(v -> addWater(500));
            addCustom.setOnClickListener(v -> showCustomAmountDialog());
            setGoalButton.setOnClickListener(v -> showSetGoalDialog());
            if (reduceWaterButton != null) {
                reduceWaterButton.setOnClickListener(v -> reduceWater());
            }
            
            // Observe water intake data in normal mode
            waterViewModel.getCurrentWaterIntake().observe(this, waterIntake -> {
                updateUI(true);
            });
        }
        
        // Update UI initially
        updateUI(false);
    }
    
    /**
     * Set up sample data for demo mode
     */
    private void setupDemoData() {
        // Toast to indicate demo mode
        Toast.makeText(this, "Demo mode: Sample water data is shown", Toast.LENGTH_SHORT).show();
        
        // Create demo day intake
        float todayIntake = DemoDataProvider.getTodayWaterIntake();
        float waterGoal = DemoDataProvider.getWaterGoal();
        
        demoDayIntake = new WaterIntake(new Date(), todayIntake, waterGoal);
        
        // Create demo history
        demoHistory = new ArrayList<>();
        List<Float> waterData = DemoDataProvider.getSampleWaterData(7);
        
        // Create entries for each past day
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -7); // Start 7 days ago
        
        for (int i = 0; i < waterData.size(); i++) {
            // Move to next day and create an intake at morning and evening
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            
            // Morning entry (25-50% of total)
            Calendar morningTime = (Calendar) calendar.clone();
            morningTime.set(Calendar.HOUR_OF_DAY, 9); // 9 AM
            morningTime.set(Calendar.MINUTE, DemoDataProvider.getRandomInt(0, 59));
            
            float morningAmount = waterData.get(i) * (0.25f + DemoDataProvider.getRandomFloat(0f, 0.25f));
            demoHistory.add(new WaterIntake(morningTime.getTime(), morningAmount, waterGoal));
            
            // Evening entry (remaining amount)
            Calendar eveningTime = (Calendar) calendar.clone();
            eveningTime.set(Calendar.HOUR_OF_DAY, 18); // 6 PM
            eveningTime.set(Calendar.MINUTE, DemoDataProvider.getRandomInt(0, 59));
            
            demoHistory.add(new WaterIntake(eveningTime.getTime(), waterData.get(i), waterGoal));
        }
        
        // Add today's entries
        Calendar today = Calendar.getInstance();
        
        // Morning entry
        Calendar morningToday = (Calendar) today.clone();
        morningToday.set(Calendar.HOUR_OF_DAY, 9);
        morningToday.set(Calendar.MINUTE, DemoDataProvider.getRandomInt(0, 59));
        
        float morningAmount = todayIntake * 0.5f;
        demoHistory.add(new WaterIntake(morningToday.getTime(), morningAmount, waterGoal));
        
        // Current entry
        demoHistory.add(demoDayIntake);
    }
    
    private void showDemoToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void updateUI(boolean animate) {
        WaterIntake waterIntake;
        
        if (isDemoMode) {
            waterIntake = demoDayIntake;
        } else {
            waterIntake = waterViewModel.getCurrentWaterIntake().getValue();
        }
        
        if (waterIntake == null) return;

        float progress = waterIntake.getProgress();
        
        // Update progress indicator
        if (animate) {
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
            waterProgressIndicator.startAnimation(animation);
        }
        waterProgressIndicator.setProgress((int) progress);
        
        // Update text views
        waterProgressText.setText(String.format(Locale.getDefault(), "%.0f%%", progress));
        waterAmountText.setText(waterIntake.getFormattedIntake());
        goalText.setText(String.format("Goal: %s", waterIntake.getFormattedGoal()));
        
        // Update history list
        updateHistoryList();
    }
    
    private void updateHistoryList() {
        historyDisplayList.clear();
        historyDataList.clear();
        
        List<WaterIntake> todayHistory;
        
        if (isDemoMode) {
            // Filter demo history to get today's entries
            final Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);
            
            todayHistory = new ArrayList<>();
            for (WaterIntake intake : demoHistory) {
                Calendar intakeDate = Calendar.getInstance();
                intakeDate.setTime(intake.getDate());
                intakeDate.set(Calendar.HOUR_OF_DAY, 0);
                intakeDate.set(Calendar.MINUTE, 0);
                intakeDate.set(Calendar.SECOND, 0);
                intakeDate.set(Calendar.MILLISECOND, 0);
                
                if (intakeDate.getTimeInMillis() == today.getTimeInMillis()) {
                    todayHistory.add(intake);
                }
            }
        } else {
            todayHistory = waterViewModel.getTodayHistory();
        }
        
        if (todayHistory.isEmpty()) {
            historyDisplayList.add("No water intake recorded today");
        } else {
            // Sort by time in descending order (newest first)
            todayHistory.sort((a, b) -> b.getDate().compareTo(a.getDate()));
            
            float previousAmount = 0;
            for (int i = 0; i < todayHistory.size(); i++) {
                WaterIntake intake = todayHistory.get(i);
                String timeStr = timeFormat.format(intake.getDate());
                String dateStr = dateFormat.format(intake.getDate());
                
                // Determine if this was an addition or reduction
                String transactionType = "";
                if (i < todayHistory.size() - 1) {
                    // Compare with the next item (which is earlier in time since we sorted descending)
                    float difference = intake.getCurrentIntake() - todayHistory.get(i + 1).getCurrentIntake();
                    if (difference > 0) {
                        transactionType = "Added " + String.format(Locale.getDefault(), "%.0f ml", difference);
                    } else if (difference < 0) {
                        transactionType = "Reduced " + String.format(Locale.getDefault(), "%.0f ml", -difference);
                    }
                } else if (i == todayHistory.size() - 1) {
                    // This is the first entry of the day
                    transactionType = "Initial " + String.format(Locale.getDefault(), "%.0f ml", intake.getCurrentIntake());
                }
                
                String entryText = timeStr + " - " + intake.getFormattedIntake() + " (" + transactionType + ")";
                historyDisplayList.add(entryText);
                historyDataList.add(intake);
            }
        }
        
        historyAdapter.notifyDataSetChanged();
    }

    private void addWater(float amount) {
        if (amount <= 0) {
            Toast.makeText(this, "Amount must be greater than 0", Toast.LENGTH_SHORT).show();
            return;
        }
        
        waterViewModel.updateWaterIntake(amount);
        
        // Show toast confirmation
        Toast.makeText(this, 
            String.format(Locale.getDefault(), "Added %.0f ml of water", amount), 
            Toast.LENGTH_SHORT).show();
    }
    
    private void reduceWater() {
        WaterIntake current = waterViewModel.getCurrentWaterIntake().getValue();
        if (current == null) return;
        
        showReduceWaterDialog(current.getCurrentIntake());
    }
    
    private void showReduceWaterDialog(float currentAmount) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_custom_amount, null);
        final EditText amountInput = view.findViewById(R.id.amountInput);
        TextView dialogTitle = new TextView(this);
        dialogTitle.setText("Reduce Water Amount (ml)");
        dialogTitle.setPadding(30, 30, 30, 30);
        
        builder.setCustomTitle(dialogTitle)
               .setView(view)
               .setPositiveButton("Reduce", (dialog, which) -> {
                   try {
                       float amount = Float.parseFloat(amountInput.getText().toString());
                       if (amount <= 0) {
                           Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show();
                           return;
                       }
                       
                       if (amount > currentAmount) {
                           Toast.makeText(this, "Amount cannot exceed current intake", Toast.LENGTH_SHORT).show();
                           return;
                       }
                       
                       waterViewModel.updateWaterIntake(-amount);
                       Toast.makeText(this, 
                           String.format(Locale.getDefault(), "Removed %.0f ml of water", amount), 
                           Toast.LENGTH_SHORT).show();
                   } catch (NumberFormatException e) {
                       Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
                   }
               })
               .setNegativeButton("Cancel", null)
               .show();
    }
    
    private void showWaterIntakeOptionsDialog(WaterIntake intake, int position) {
        String[] options = {"Edit amount", "Remove entry"};
        
        new AlertDialog.Builder(this)
            .setTitle("Water Intake Options")
            .setItems(options, (dialog, which) -> {
                switch (which) {
                    case 0: // Edit amount
                        showEditAmountDialog(intake);
                        break;
                    case 1: // Remove entry
                        waterViewModel.removeWaterIntakeEntry(intake);
                        updateHistoryList();
                        break;
                }
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
    
    private void showEditAmountDialog(WaterIntake intake) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_custom_amount, null);
        final EditText amountInput = view.findViewById(R.id.amountInput);
        amountInput.setText(String.format(Locale.getDefault(), "%.0f", intake.getCurrentIntake()));
        
        builder.setTitle("Edit Water Amount (ml)")
               .setView(view)
               .setPositiveButton("Save", (dialog, which) -> {
                   try {
                       float amount = Float.parseFloat(amountInput.getText().toString());
                       if (amount > 0) {
                           intake.setCurrentIntake(amount);
                           waterViewModel.updateWaterIntakeEntry(intake);
                       } else {
                           Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show();
                       }
                   } catch (NumberFormatException e) {
                       Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
                   }
               })
               .setNegativeButton("Cancel", null)
               .show();
    }

    private void showCustomAmountDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_custom_amount, null);
        final EditText amountInput = view.findViewById(R.id.amountInput);
        
        builder.setTitle("Add Custom Amount (ml)")
               .setView(view)
               .setPositiveButton("Add", (dialog, which) -> {
                   try {
                       float amount = Float.parseFloat(amountInput.getText().toString());
                       if (amount > 0) {
                           addWater(amount);
                       } else {
                           Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show();
                       }
                   } catch (NumberFormatException e) {
                       Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
                   }
               })
               .setNegativeButton("Cancel", null)
               .show();
    }

    private void showSetGoalDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_set_goal, null);
        final EditText goalInput = view.findViewById(R.id.goalInput);
        
        // Pre-fill with current goal
        WaterIntake currentIntake = waterViewModel.getCurrentWaterIntake().getValue();
        if (currentIntake != null) {
            goalInput.setText(String.format(Locale.getDefault(), "%.0f", currentIntake.getDailyGoal()));
        }
        
        builder.setTitle("Set Daily Goal (ml)")
               .setView(view)
               .setPositiveButton("Save", (dialog, which) -> {
                   try {
                       float goal = Float.parseFloat(goalInput.getText().toString());
                       if (goal > 0) {
                           waterViewModel.setDailyGoal(goal);
                       } else {
                           Toast.makeText(this, "Please enter a valid goal", Toast.LENGTH_SHORT).show();
                       }
                   } catch (NumberFormatException e) {
                       Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
                   }
               })
               .setNegativeButton("Cancel", null)
               .show();
    }
    
    private boolean isToday(Date date) {
        Calendar today = Calendar.getInstance();
        Calendar entryDate = Calendar.getInstance();
        entryDate.setTime(date);
        
        return today.get(Calendar.YEAR) == entryDate.get(Calendar.YEAR) &&
               today.get(Calendar.DAY_OF_YEAR) == entryDate.get(Calendar.DAY_OF_YEAR);
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