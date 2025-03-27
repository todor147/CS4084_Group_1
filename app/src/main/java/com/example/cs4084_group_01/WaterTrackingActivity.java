package com.example.cs4084_group_01;

import android.os.Bundle;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.cs4084_group_01.model.WaterIntake;
import com.example.cs4084_group_01.viewmodel.WaterViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WaterTrackingActivity extends AppCompatActivity {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_tracking);

        // Initialize ViewModel
        waterViewModel = new ViewModelProvider(this).get(WaterViewModel.class);

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

        add100ml.setOnClickListener(v -> addWater(100));
        add250ml.setOnClickListener(v -> addWater(250));
        add500ml.setOnClickListener(v -> addWater(500));
        addCustom.setOnClickListener(v -> showCustomAmountDialog());
        setGoalButton.setOnClickListener(v -> showSetGoalDialog());
        
        // Add the reduceWaterButton listener if it's in the layout
        if (reduceWaterButton != null) {
            reduceWaterButton.setOnClickListener(v -> reduceWater());
        }

        // Observe water intake data
        waterViewModel.getCurrentWaterIntake().observe(this, waterIntake -> {
            updateUI(true);
        });
    }

    private void updateUI(boolean animate) {
        WaterIntake waterIntake = waterViewModel.getCurrentWaterIntake().getValue();
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
        
        List<WaterIntake> todayHistory = waterViewModel.getTodayHistory();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 