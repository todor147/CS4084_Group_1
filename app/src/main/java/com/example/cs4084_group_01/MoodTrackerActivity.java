package com.example.cs4084_group_01;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.cs4084_group_01.model.MoodEntry;
import com.example.cs4084_group_01.model.MoodType;
import com.example.cs4084_group_01.viewmodel.MoodViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MoodTrackerActivity extends AppCompatActivity {

    public static final String EXTRA_DATE = "selected_date";
    private MoodViewModel moodViewModel;
    private RadioGroup moodGroup;
    private TextInputEditText notesField;
    private Button saveButton;
    private CalendarView calendarView;
    private SimpleDateFormat dateFormat;
    private Date selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_tracker);

        // Initialize ViewModel
        moodViewModel = new ViewModelProvider(this).get(MoodViewModel.class);

        // Initialize date formatter
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        // Get date from intent or use current date
        long dateMillis = getIntent().getLongExtra(EXTRA_DATE, System.currentTimeMillis());
        selectedDate = new Date(dateMillis);
        boolean forceEdit = getIntent().getBooleanExtra("force_edit", false);

        // Check if there's already an entry for today
        moodViewModel.setCurrentDate(selectedDate);
        moodViewModel.getCurrentDateEntries().observe(this, entries -> {
            if (entries != null && !entries.isEmpty() && !forceEdit) {
                // If this is today's date and we already have an entry, switch to view mode
                Calendar selectedCal = Calendar.getInstance();
                selectedCal.setTime(selectedDate);
                Calendar today = Calendar.getInstance();
                
                if (selectedCal.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                    selectedCal.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                    selectedCal.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH)) {
                    // Switch to view mode for today
                    Intent intent = new Intent(this, MoodViewActivity.class);
                    intent.putExtra(MoodViewActivity.EXTRA_DATE, selectedDate.getTime());
                    startActivity(intent);
                    finish();
                    return;
                }
            }
            
            // Continue with normal initialization if we haven't redirected
            initializeUI();
        });
    }

    private void initializeUI() {
        // Initialize UI components
        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        moodGroup = findViewById(R.id.moodGroup);
        notesField = findViewById(R.id.notesField);
        saveButton = findViewById(R.id.saveButton);
        calendarView = findViewById(R.id.calendarView);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(selectedDate);
        calendarView.setDate(selectedDate.getTime());

        // Set up calendar to select dates
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar selectedCalendar = Calendar.getInstance();
            selectedCalendar.set(year, month, dayOfMonth);
            Date newDate = selectedCalendar.getTime();
            
            // If selected date is not today, go to view mode
            Calendar today = Calendar.getInstance();
            if (selectedCalendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                selectedCalendar.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                selectedCalendar.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH)) {
                selectedDate = newDate;
                moodViewModel.setCurrentDate(selectedDate);
            } else {
                Intent intent = new Intent(this, MoodViewActivity.class);
                intent.putExtra(MoodViewActivity.EXTRA_DATE, newDate.getTime());
                startActivity(intent);
            }
        });

        // Set up save button
        saveButton.setOnClickListener(v -> saveMoodEntry());

        // Load existing entry if available
        moodViewModel.getCurrentDateEntries().observe(this, entries -> {
            if (entries != null && !entries.isEmpty()) {
                MoodEntry lastEntry = entries.get(entries.size() - 1);
                updateUIWithEntry(lastEntry);
            }
        });
    }

    private void updateUIWithEntry(MoodEntry entry) {
        // Set the mood radio button
        switch (entry.getMoodType()) {
            case HAPPY:
                moodGroup.check(R.id.moodHappy);
                break;
            case CALM:
                moodGroup.check(R.id.moodCalm);
                break;
            case NEUTRAL:
                moodGroup.check(R.id.moodNeutral);
                break;
            case SAD:
                moodGroup.check(R.id.moodSad);
                break;
            case STRESSED:
                moodGroup.check(R.id.moodStressed);
                break;
        }

        // Set the notes
        notesField.setText(entry.getNotes());
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
        MoodEntry entry = new MoodEntry(moodType, selectedDate, notes);
        moodViewModel.saveMoodEntry(entry);

        Toast.makeText(this, "Mood saved successfully", Toast.LENGTH_SHORT).show();

        // Switch to view mode
        Intent intent = new Intent(this, MoodViewActivity.class);
        intent.putExtra(MoodViewActivity.EXTRA_DATE, selectedDate.getTime());
        startActivity(intent);
        finish();
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