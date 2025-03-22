package com.example.cs4084_group_01;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cs4084_group_01.adapter.WaterIntakeHistoryAdapter;
import com.example.cs4084_group_01.model.WaterIntake;
import com.example.cs4084_group_01.repository.WaterIntakeRepository;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.slider.Slider;
import android.widget.TextView;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class WaterTrackingActivity extends AppCompatActivity {
    private WaterIntakeRepository repository;
    private WaterIntake currentIntake;
    private WaterIntakeHistoryAdapter historyAdapter;

    // UI Components
    private LinearProgressIndicator waterProgressBar;
    private TextView waterProgressText;
    private TextView glassCountText;
    private TextView goalText;
    private MaterialButton incrementButton;
    private MaterialButton decrementButton;
    private Slider goalSlider;
    private RecyclerView historyRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_tracking);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize repository
        repository = new WaterIntakeRepository(this);
        currentIntake = repository.getWaterIntake();

        // Initialize UI components
        initializeViews();
        setupClickListeners();
        setupGoalSlider();
        setupHistoryRecyclerView();

        // Update UI with current data
        updateUI();
        loadHistory();
    }

    private void initializeViews() {
        waterProgressBar = findViewById(R.id.waterProgressBar);
        waterProgressText = findViewById(R.id.waterProgressText);
        glassCountText = findViewById(R.id.glassCountText);
        goalText = findViewById(R.id.goalText);
        incrementButton = findViewById(R.id.incrementButton);
        decrementButton = findViewById(R.id.decrementButton);
        goalSlider = findViewById(R.id.goalSlider);
        historyRecyclerView = findViewById(R.id.historyRecyclerView);
    }

    private void setupClickListeners() {
        incrementButton.setOnClickListener(v -> {
            currentIntake.addGlass();
            repository.saveWaterIntake(currentIntake);
            updateUI();
            loadHistory();
        });

        decrementButton.setOnClickListener(v -> {
            currentIntake.removeGlass();
            repository.saveWaterIntake(currentIntake);
            updateUI();
            loadHistory();
        });
    }

    private void setupGoalSlider() {
        goalSlider.setValue(currentIntake.getDailyGoal() / 1000); // Convert to liters
        goalSlider.addOnChangeListener((slider, value, fromUser) -> {
            if (fromUser) {
                currentIntake.setDailyGoal(value * 1000); // Convert back to milliliters
                repository.saveWaterIntake(currentIntake);
                updateUI();
            }
        });
    }

    private void setupHistoryRecyclerView() {
        historyAdapter = new WaterIntakeHistoryAdapter();
        historyRecyclerView.setAdapter(historyAdapter);
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void updateUI() {
        // Update progress bar
        int progress = Math.round(currentIntake.getProgress());
        waterProgressBar.setProgress(progress);

        // Update progress text
        waterProgressText.setText(String.format("%s / %s",
                currentIntake.getFormattedIntake(),
                currentIntake.getFormattedGoal()));

        // Update glass count
        int glassCount = currentIntake.getGlassCount();
        glassCountText.setText(String.format("%d glass%s",
                glassCount, glassCount == 1 ? "" : "es"));

        // Update goal text
        goalText.setText(String.format("Goal: %s", currentIntake.getFormattedGoal()));

        // Enable/disable decrement button
        decrementButton.setEnabled(currentIntake.getCurrentIntake() >= WaterIntake.getGlassSize());
    }

    private void loadHistory() {
        List<WaterIntake> history = repository.getWaterIntakeHistory();
        // Sort by date descending
        Collections.sort(history, (a, b) -> b.getDate().compareTo(a.getDate()));
        historyAdapter.setHistory(history);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
} 