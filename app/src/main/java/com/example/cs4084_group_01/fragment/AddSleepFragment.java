package com.example.cs4084_group_01.fragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.cs4084_group_01.R;
import com.example.cs4084_group_01.model.SleepEntry;
import com.example.cs4084_group_01.viewmodel.SleepViewModel;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class AddSleepFragment extends Fragment {
    private static final String TAG = "AddSleepFragment";

    private SleepViewModel viewModel;
    
    private Button datePickerButton;
    private Button startTimeButton;
    private Button endTimeButton;
    private TextView durationText;
    private RatingBar qualityRatingBar;
    private TextInputEditText notesEditText;
    private Button saveButton;
    
    private Calendar selectedDate = Calendar.getInstance();
    private Calendar startTime = Calendar.getInstance();
    private Calendar endTime = Calendar.getInstance();
    
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
    private SimpleDateFormat timeFormatter = new SimpleDateFormat("h:mm a", Locale.getDefault());
    
    private String sleepEntryId = null; // For editing existing entries
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView called");
        return inflater.inflate(R.layout.fragment_add_sleep, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated called");
        
        // Initialize ViewModel
        viewModel = new ViewModelProvider(requireActivity()).get(SleepViewModel.class);
        
        // Initialize UI elements
        initializeViews(view);
        
        // Set default times
        startTime.set(Calendar.HOUR_OF_DAY, 22);
        startTime.set(Calendar.MINUTE, 0);
        endTime.set(Calendar.HOUR_OF_DAY, 7);
        endTime.set(Calendar.MINUTE, 0);
        
        // Update UI with current values
        updateDateButtonText();
        updateTimeButtonsText();
        updateDurationText();
        
        // Set up click listeners
        setupClickListeners();
        
        Log.d(TAG, "Fragment setup complete");
    }
    
    private void initializeViews(View view) {
        datePickerButton = view.findViewById(R.id.datePickerButton);
        startTimeButton = view.findViewById(R.id.startTimeButton);
        endTimeButton = view.findViewById(R.id.endTimeButton);
        durationText = view.findViewById(R.id.durationText);
        qualityRatingBar = view.findViewById(R.id.qualityRatingBar);
        notesEditText = view.findViewById(R.id.notesEditText);
        saveButton = view.findViewById(R.id.saveButton);
    }
    
    private void setupClickListeners() {
        datePickerButton.setOnClickListener(v -> showDatePickerDialog());
        startTimeButton.setOnClickListener(v -> showTimePickerDialog(true));
        endTimeButton.setOnClickListener(v -> showTimePickerDialog(false));
        saveButton.setOnClickListener(v -> saveSleepEntry());
    }
    
    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    selectedDate.set(Calendar.YEAR, year);
                    selectedDate.set(Calendar.MONTH, month);
                    selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateDateButtonText();
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }
    
    private void showTimePickerDialog(boolean isStartTime) {
        Calendar time = isStartTime ? startTime : endTime;
        
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                requireContext(),
                (view, hourOfDay, minute) -> {
                    time.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    time.set(Calendar.MINUTE, minute);
                    
                    updateTimeButtonsText();
                    updateDurationText();
                },
                time.get(Calendar.HOUR_OF_DAY),
                time.get(Calendar.MINUTE),
                false
        );
        timePickerDialog.show();
    }
    
    private void updateDateButtonText() {
        datePickerButton.setText(dateFormatter.format(selectedDate.getTime()));
    }
    
    private void updateTimeButtonsText() {
        startTimeButton.setText(timeFormatter.format(startTime.getTime()));
        endTimeButton.setText(timeFormatter.format(endTime.getTime()));
    }
    
    private void updateDurationText() {
        // Calculate duration in minutes
        long durationMillis = getAdjustedEndTime().getTimeInMillis() - getStartTimeWithDate().getTimeInMillis();
        int durationMinutes = (int) TimeUnit.MILLISECONDS.toMinutes(durationMillis);
        
        // Format duration
        int hours = durationMinutes / 60;
        int minutes = durationMinutes % 60;
        String durationString = String.format(Locale.getDefault(), "%d hr %d min", hours, minutes);
        
        durationText.setText(durationString);
    }
    
    private Calendar getStartTimeWithDate() {
        Calendar calendar = (Calendar) selectedDate.clone();
        calendar.set(Calendar.HOUR_OF_DAY, startTime.get(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, startTime.get(Calendar.MINUTE));
        return calendar;
    }
    
    private Calendar getAdjustedEndTime() {
        Calendar calendar = (Calendar) selectedDate.clone();
        calendar.set(Calendar.HOUR_OF_DAY, endTime.get(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, endTime.get(Calendar.MINUTE));
        
        // If end time is earlier than start time, assume it's the next day
        if (calendar.before(getStartTimeWithDate())) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        
        return calendar;
    }
    
    private void saveSleepEntry() {
        // Get quality rating
        int quality = Math.round(qualityRatingBar.getRating());
        if (quality == 0) {
            Toast.makeText(requireContext(), "Please rate your sleep quality", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Get notes
        String notes = notesEditText.getText() != null ? notesEditText.getText().toString() : "";
        
        // Create sleep entry
        Date startDate = getStartTimeWithDate().getTime();
        Date endDate = getAdjustedEndTime().getTime();
        
        SleepEntry sleepEntry;
        if (sleepEntryId != null) {
            // Editing existing entry
            sleepEntry = new SleepEntry(sleepEntryId, startDate, endDate, quality, notes);
            viewModel.updateSleepEntry(sleepEntry);
            Toast.makeText(requireContext(), "Sleep entry updated", Toast.LENGTH_SHORT).show();
        } else {
            // Creating new entry
            sleepEntry = new SleepEntry(startDate, endDate, quality, notes);
            viewModel.addSleepEntry(sleepEntry);
            Toast.makeText(requireContext(), "Sleep entry saved", Toast.LENGTH_SHORT).show();
        }
        
        // Reset form for new entry
        resetForm();
    }
    
    private void resetForm() {
        sleepEntryId = null;
        qualityRatingBar.setRating(0);
        notesEditText.setText("");
        
        // Reset to current date
        selectedDate = Calendar.getInstance();
        
        // Reset to default times
        startTime = Calendar.getInstance();
        startTime.set(Calendar.HOUR_OF_DAY, 22);
        startTime.set(Calendar.MINUTE, 0);
        
        endTime = Calendar.getInstance();
        endTime.set(Calendar.HOUR_OF_DAY, 7);
        endTime.set(Calendar.MINUTE, 0);
        
        updateDateButtonText();
        updateTimeButtonsText();
        updateDurationText();
    }
    
    public void editSleepEntry(SleepEntry entry) {
        sleepEntryId = entry.getId();
        
        // Set date from entry's start time
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(entry.getStartTime());
        selectedDate.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
        selectedDate.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
        selectedDate.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH));
        
        // Set start time
        calendar.setTime(entry.getStartTime());
        startTime.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
        startTime.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE));
        
        // Set end time
        calendar.setTime(entry.getEndTime());
        endTime.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
        endTime.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE));
        
        // Set quality
        qualityRatingBar.setRating(entry.getQuality());
        
        // Set notes
        notesEditText.setText(entry.getNotes());
        
        // Update UI
        updateDateButtonText();
        updateTimeButtonsText();
        updateDurationText();
    }
} 