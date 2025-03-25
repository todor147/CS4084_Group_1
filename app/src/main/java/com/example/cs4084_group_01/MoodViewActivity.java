package com.example.cs4084_group_01;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.cs4084_group_01.model.MoodEntry;
import com.example.cs4084_group_01.viewmodel.MoodViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MoodViewActivity extends AppCompatActivity {
    public static final String EXTRA_DATE = "selected_date";
    private MoodViewModel moodViewModel;
    private TextView dateText, moodText, notesText;
    private Button editButton;
    private SimpleDateFormat displayDateFormat;
    private Date selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_view);

        // Initialize ViewModel
        moodViewModel = new ViewModelProvider(this).get(MoodViewModel.class);

        // Setup toolbar
        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Initialize views
        dateText = findViewById(R.id.dateText);
        moodText = findViewById(R.id.moodText);
        notesText = findViewById(R.id.notesText);
        editButton = findViewById(R.id.editButton);

        displayDateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());

        // Get selected date from intent
        long dateMillis = getIntent().getLongExtra(EXTRA_DATE, System.currentTimeMillis());
        selectedDate = new Date(dateMillis);

        // Check if this is today's date
        Calendar selectedCal = Calendar.getInstance();
        selectedCal.setTime(selectedDate);
        Calendar today = Calendar.getInstance();
        
        boolean isToday = selectedCal.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                         selectedCal.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                         selectedCal.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH);

        // Show edit button for all entries
        editButton.setVisibility(View.VISIBLE);
        editButton.setText("Edit Entry");

        // Set the date in ViewModel
        moodViewModel.setCurrentDate(selectedDate);

        // Setup edit button
        editButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, MoodTrackerActivity.class);
            intent.putExtra(MoodTrackerActivity.EXTRA_DATE, selectedDate.getTime());
            intent.putExtra("force_edit", true); // Add flag to force edit mode
            startActivity(intent);
            finish();
        });

        // Observe mood entries
        moodViewModel.getCurrentDateEntries().observe(this, this::updateDisplay);
    }

    private void updateDisplay(List<MoodEntry> entries) {
        if (entries != null && !entries.isEmpty()) {
            // Display the most recent entry for the day
            MoodEntry entry = entries.get(entries.size() - 1);
            
            dateText.setText(displayDateFormat.format(selectedDate));
            moodText.setText(entry.getMoodType().toString());
            notesText.setText(entry.getNotes().isEmpty() ? "No notes" : entry.getNotes());
        } else {
            dateText.setText(displayDateFormat.format(selectedDate));
            moodText.setText("No mood recorded");
            notesText.setText("No notes");
        }
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