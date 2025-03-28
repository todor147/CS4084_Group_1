package com.example.cs4084_group_01;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.cs4084_group_01.model.MoodEntry;
import com.example.cs4084_group_01.model.MoodType;
import com.example.cs4084_group_01.viewmodel.MoodViewModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MoodSelection extends AppCompatActivity {
    private MoodViewModel moodViewModel;
    private ChipGroup moodGroup;
    private TextInputEditText notesField;
    private Button saveButton;
    private CalendarView calendarView;
    private ListView moodListView;
    private ArrayAdapter<String> moodAdapter;
    private List<String> moodDisplayList;
    private SimpleDateFormat timeFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_selection);

        // Initialize ViewModel
        moodViewModel = new ViewModelProvider(this).get(MoodViewModel.class);

        // Initialize time formatter
        timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        // Initialize views
        moodGroup = findViewById(R.id.moodGroup);
        notesField = findViewById(R.id.notesField);
        saveButton = findViewById(R.id.saveButton);
        calendarView = findViewById(R.id.calendarView);
        moodListView = findViewById(R.id.moodListView);

        // Setup list adapter
        moodDisplayList = new ArrayList<>();
        moodAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, moodDisplayList);
        moodListView.setAdapter(moodAdapter);

        // Setup calendar view
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar selectedCalendar = Calendar.getInstance();
            selectedCalendar.set(year, month, dayOfMonth);
            moodViewModel.setCurrentDate(selectedCalendar.getTime());
        });

        // Setup save button
        setupSaveButton();

        // Observe mood entries for current date
        moodViewModel.getCurrentDateEntries().observe(this, entries -> {
            updateMoodListView(entries);
        });
    }

    private void setupSaveButton() {
        saveButton.setOnClickListener(v -> {
            int selectedId = moodGroup.getCheckedChipId();
            if (selectedId == -1) {
                Toast.makeText(this, "Please select a mood", Toast.LENGTH_SHORT).show();
                return;
            }

            MoodType moodType;
            Chip selectedChip = findViewById(selectedId);
            String chipText = selectedChip.getText().toString();

            if (chipText.equals("Very Happy") || chipText.equals("Happy")) {
                moodType = MoodType.HAPPY;
            } else if (chipText.equals("Neutral")) {
                moodType = MoodType.NEUTRAL;
            } else if (chipText.equals("Sad")) {
                moodType = MoodType.SAD;
            } else if (chipText.equals("Very Sad")) {
                moodType = MoodType.STRESSED;
            } else {
                moodType = MoodType.CALM;
            }

            String notes = notesField.getText() != null ? notesField.getText().toString() : "";

            // Create and save mood entry
            MoodEntry entry = new MoodEntry(moodType, new Date(), notes);
            moodViewModel.saveMoodEntry(moodType, notes);

            // Clear inputs
            moodGroup.clearCheck();
            notesField.setText("");

            Toast.makeText(this, "Mood saved successfully", Toast.LENGTH_SHORT).show();
        });
    }

    private void updateMoodListView(List<MoodEntry> entries) {
        moodDisplayList.clear();

        if (entries.isEmpty()) {
            moodDisplayList.add("No mood entries for this date");
        } else {
            for (MoodEntry entry : entries) {
                String timeStr = timeFormat.format(entry.getTimestamp());
                String entryText = timeStr + " - " + entry.getMoodType().toString();
                if (!entry.getNotes().isEmpty()) {
                    entryText += "\nNotes: " + entry.getNotes();
                }
                moodDisplayList.add(entryText);
            }
        }

        moodAdapter.notifyDataSetChanged();
    }
}