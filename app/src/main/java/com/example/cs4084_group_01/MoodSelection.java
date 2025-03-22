package com.example.cs4084_group_01;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.cs4084_group_01.viewmodel.ProfileViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Date;
import java.text.SimpleDateFormat;

public class MoodSelection extends AppCompatActivity {
    private ProfileViewModel viewModel;
    private TextView mood;
    private RadioGroup moodGroup;
    private EditText notesField;
    private Gson gson;
    private CalendarView calendarView;
    private ListView moodListView;
    private HashMap<String, MoodData> moodDataMap;
    private ArrayAdapter<String> moodListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_selection);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        // Initialize views
        moodGroup = findViewById(R.id.moodGroup);
        notesField = findViewById(R.id.notesField);
        calendarView = findViewById(R.id.calendarView);
        moodListView = findViewById(R.id.moodListView);
        gson = new Gson();
        moodDataMap = new HashMap<>();

        // Setup mood selection
        setupMoodSelection();

        // Setup calendar view
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            String dateKey = year + "-" + (month + 1) + "-" + dayOfMonth;
            updateMoodListView(dateKey);
        });

        // Initialize list adapter
        moodListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        moodListView.setAdapter(moodListAdapter);
    }

    private void setupMoodSelection() {
        moodGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selectedMood = findViewById(checkedId);
            String mood = selectedMood.getText().toString();
            String notes = notesField.getText().toString();

            // Get current date
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String dateKey = sdf.format(new Date());

            // Store mood data
            moodDataMap.put(dateKey, new MoodData(mood, notes));
            updateMoodListView(dateKey);
        });
    }

    private void updateMoodListView(String dateKey) {
        moodListAdapter.clear();
        MoodData moodData = moodDataMap.get(dateKey);
        if (moodData != null) {
            moodListAdapter.add("Mood: " + moodData.mood + "\nNotes: " + moodData.notes);
        }
    }

    private static class MoodData {
        String mood;
        String notes;

        MoodData(String mood, String notes) {
            this.mood = mood;
            this.notes = notes;
        }
    }
}