package com.example.cs4084_group_01;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.ArrayAdapter;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.cs4084_group_01.model.MoodEntry;
import com.example.cs4084_group_01.model.MoodType;
import com.example.cs4084_group_01.repository.MoodRepository;
import com.example.cs4084_group_01.viewmodel.MoodViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MoodTrackerActivity extends AppCompatActivity {

    private MoodViewModel moodViewModel;
    private RadioGroup moodGroup;
    private TextInputEditText notesField;
    private Button saveButton;
    private CalendarView calendarView;
    private ListView moodListView;
    private ArrayAdapter<String> moodAdapter;
    private List<String> moodDisplayList;
    private SimpleDateFormat dateFormat;
    private SimpleDateFormat timeFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_tracker);

        // Initialize ViewModel
        moodViewModel = new ViewModelProvider(this).get(MoodViewModel.class);

        // Initialize date formatters
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        // Initialize UI components
        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        moodGroup = findViewById(R.id.moodGroup);
        notesField = findViewById(R.id.notesField);
        saveButton = findViewById(R.id.saveButton);
        calendarView = findViewById(R.id.calendarView);
        moodListView = findViewById(R.id.moodListView);

        // Set up list adapter for mood entries
        moodDisplayList = new ArrayList<>();
        moodAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, moodDisplayList);
        moodListView.setAdapter(moodAdapter);

        // Set up calendar to select dates
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar selectedCalendar = Calendar.getInstance();
            selectedCalendar.set(year, month, dayOfMonth);
            moodViewModel.setCurrentDate(selectedCalendar.getTime());
        });

        // Set up save button
        saveButton.setOnClickListener(v -> saveMoodEntry());

        // Observe mood entries for current date
        moodViewModel.getCurrentDateEntries().observe(this, entries -> {
            updateMoodListView(entries);
        });
    }

    private void saveMoodEntry() {
        // Get selected mood
        int selectedId = moodGroup.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(this, "Please select a mood", Toast.LENGTH_SHORT).show();
            return;
        }

        MoodType moodType;
        if (selectedId == R.id.moodHappy) {
            moodType = MoodType.HAPPY;
        } else if (selectedId == R.id.moodCalm) {
            moodType = MoodType.CALM;
        } else if (selectedId == R.id.moodNeutral) {
            moodType = MoodType.NEUTRAL;
        } else if (selectedId == R.id.moodSad) {
            moodType = MoodType.SAD;
        } else {
            moodType = MoodType.STRESSED;
        }

        // Get notes
        String notes = notesField.getText() != null ? notesField.getText().toString() : "";

        // Create and save mood entry
        MoodEntry entry = new MoodEntry(moodType, new Date(), notes);
        moodViewModel.saveMoodEntry(moodType, notes);

        // Clear inputs
        moodGroup.clearCheck();
        notesField.setText("");

        Toast.makeText(this, "Mood saved successfully", Toast.LENGTH_SHORT).show();
    }

    private void updateMoodListView(List<MoodEntry> entries) {
        moodDisplayList.clear();

        if (entries == null || entries.isEmpty()) {
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 