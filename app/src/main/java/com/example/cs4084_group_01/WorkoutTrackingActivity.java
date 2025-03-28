package com.example.cs4084_group_01;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cs4084_group_01.model.WorkoutEntry;
import com.example.cs4084_group_01.repository.WorkoutRepository;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class WorkoutTrackingActivity extends AppCompatActivity {
    private WorkoutRepository workoutRepository;
    private AutoCompleteTextView workoutTypeDropdown;
    private AutoCompleteTextView intensityDropdown;
    private TextInputEditText durationInput;
    private RecyclerView workoutHistoryList;
    private WorkoutAdapter workoutAdapter;

    private static final String[] WORKOUT_TYPES = {
        "Running", "Walking", "Cycling", "Swimming", "Weight Training",
        "Yoga", "HIIT", "Pilates", "Boxing", "Other"
    };

    private static final String[] INTENSITY_LEVELS = {
        "Low", "Medium", "High"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_tracking);

        workoutRepository = new WorkoutRepository(this);

        // Setup toolbar
        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Initialize views
        workoutTypeDropdown = findViewById(R.id.workoutTypeDropdown);
        intensityDropdown = findViewById(R.id.intensityDropdown);
        durationInput = findViewById(R.id.durationInput);
        workoutHistoryList = findViewById(R.id.workoutHistoryList);

        // Setup dropdowns
        ArrayAdapter<String> workoutTypeAdapter = new ArrayAdapter<>(
            this, android.R.layout.simple_dropdown_item_1line, WORKOUT_TYPES
        );
        workoutTypeDropdown.setAdapter(workoutTypeAdapter);

        ArrayAdapter<String> intensityAdapter = new ArrayAdapter<>(
            this, android.R.layout.simple_dropdown_item_1line, INTENSITY_LEVELS
        );
        intensityDropdown.setAdapter(intensityAdapter);

        // Setup RecyclerView
        workoutHistoryList.setLayoutManager(new LinearLayoutManager(this));
        workoutAdapter = new WorkoutAdapter();
        workoutHistoryList.setAdapter(workoutAdapter);

        // Load workout history
        updateWorkoutHistory();

        // Setup save button
        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(v -> saveWorkout());
    }

    private void saveWorkout() {
        String workoutType = workoutTypeDropdown.getText().toString();
        String intensity = intensityDropdown.getText().toString();
        String durationStr = durationInput.getText().toString();

        if (workoutType.isEmpty() || intensity.isEmpty() || durationStr.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int duration = Integer.parseInt(durationStr);
            WorkoutEntry workout = new WorkoutEntry(workoutType, duration, intensity);
            workoutRepository.saveWorkout(workout);

            // Clear inputs
            workoutTypeDropdown.setText("");
            intensityDropdown.setText("");
            durationInput.setText("");

            // Update history
            updateWorkoutHistory();
            Toast.makeText(this, "Workout saved successfully", Toast.LENGTH_SHORT).show();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid duration", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateWorkoutHistory() {
        List<WorkoutEntry> workouts = workoutRepository.getWorkouts();
        workoutAdapter.setWorkouts(workouts);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private static class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder> {
        private List<WorkoutEntry> workouts = new ArrayList<>();
        private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy HH:mm", Locale.getDefault());

        public void setWorkouts(List<WorkoutEntry> workouts) {
            this.workouts = new ArrayList<>(workouts);
            notifyDataSetChanged();
        }

        @Override
        public WorkoutViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            android.view.View view = android.view.LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_workout, parent, false);
            return new WorkoutViewHolder(view);
        }

        @Override
        public void onBindViewHolder(WorkoutViewHolder holder, int position) {
            WorkoutEntry workout = workouts.get(workouts.size() - 1 - position); // Show newest first
            holder.workoutTypeText.setText(workout.getWorkoutType());
            holder.dateText.setText(dateFormat.format(workout.getDate()));
            holder.durationText.setText(workout.getDurationMinutes() + " minutes");
            holder.intensityText.setText(workout.getIntensity());
        }

        @Override
        public int getItemCount() {
            return workouts.size();
        }

        static class WorkoutViewHolder extends RecyclerView.ViewHolder {
            final android.widget.TextView workoutTypeText;
            final android.widget.TextView dateText;
            final android.widget.TextView durationText;
            final android.widget.TextView intensityText;

            WorkoutViewHolder(android.view.View itemView) {
                super(itemView);
                workoutTypeText = itemView.findViewById(R.id.workoutTypeText);
                dateText = itemView.findViewById(R.id.dateText);
                durationText = itemView.findViewById(R.id.durationText);
                intensityText = itemView.findViewById(R.id.intensityText);
            }
        }
    }
} 