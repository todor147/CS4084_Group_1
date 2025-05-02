package com.example.cs4084_group_01;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.cs4084_group_01.model.GymInfo;
import com.example.cs4084_group_01.repository.GymInfoRepository;

import java.util.Locale;
import java.util.Map;

public class CampusGymActivity extends BaseActivity {
    private GymInfoRepository repository;
    
    private TextView statusTextView;
    private View statusIndicator;
    private TextView hoursTextView;
    private TextView descriptionTextView;
    private ViewGroup facilitiesContainer;
    private ViewGroup rulesContainer;
    private TextView contactTextView;
    private TextView locationTextView;
    private TableLayout scheduleTableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campus_gym);
        
        // Set up toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        
        // Initialize repository
        repository = new GymInfoRepository(this);
        
        // Initialize views
        initializeViews();
        
        // Display gym information
        displayGymInfo();
    }
    
    private void initializeViews() {
        statusTextView = findViewById(R.id.statusTextView);
        statusIndicator = findViewById(R.id.statusIndicator);
        hoursTextView = findViewById(R.id.hoursTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        facilitiesContainer = findViewById(R.id.facilitiesContainer);
        rulesContainer = findViewById(R.id.rulesContainer);
        contactTextView = findViewById(R.id.contactTextView);
        locationTextView = findViewById(R.id.locationTextView);
        scheduleTableLayout = findViewById(R.id.scheduleTableLayout);
    }
    
    private void displayGymInfo() {
        GymInfo gymInfo = repository.getGymInfo();
        
        // Display open/closed status
        boolean isOpen = repository.isGymOpen();
        statusTextView.setText(isOpen ? "OPEN NOW" : "CLOSED NOW");
        statusIndicator.setBackground(ContextCompat.getDrawable(this, 
                isOpen ? R.drawable.circle_green : R.drawable.circle_red));
        
        // Display today's hours
        hoursTextView.setText("Today: " + repository.getTodayHours());
        
        // Display description
        descriptionTextView.setText(gymInfo.getDescription());
        
        // Display weekly schedule
        displayWeeklySchedule(gymInfo);
        
        // Display facilities
        if (gymInfo.getFacilities() != null) {
            facilitiesContainer.removeAllViews();
            for (String facility : gymInfo.getFacilities()) {
                TextView textView = new TextView(this);
                textView.setText("• " + facility);
                textView.setTextSize(14); // Using explicit text size instead of style
                facilitiesContainer.addView(textView);
            }
        }
        
        // Display rules
        if (gymInfo.getRules() != null) {
            rulesContainer.removeAllViews();
            for (String rule : gymInfo.getRules()) {
                TextView textView = new TextView(this);
                textView.setText("• " + rule);
                textView.setTextSize(14); // Using explicit text size instead of style
                rulesContainer.addView(textView);
            }
        }
        
        // Display contact information
        contactTextView.setText(String.format("Phone: %s\nEmail: %s", 
                gymInfo.getContactPhone(), gymInfo.getContactEmail()));
        
        // Display location
        locationTextView.setText(gymInfo.getLocation());
    }
    
    private void displayWeeklySchedule(GymInfo gymInfo) {
        scheduleTableLayout.removeAllViews();
        
        // Add headers
        TableRow headerRow = new TableRow(this);
        
        TextView dayHeader = new TextView(this);
        dayHeader.setText("Day");
        dayHeader.setPadding(0, 8, 16, 8);
        dayHeader.setTextSize(14);
        dayHeader.setTypeface(null, android.graphics.Typeface.BOLD);
        
        TextView hoursHeader = new TextView(this);
        hoursHeader.setText("Hours");
        hoursHeader.setPadding(16, 8, 0, 8);
        hoursHeader.setTextSize(14);
        hoursHeader.setTypeface(null, android.graphics.Typeface.BOLD);
        
        headerRow.addView(dayHeader);
        headerRow.addView(hoursHeader);
        scheduleTableLayout.addView(headerRow);
        
        // Add days and hours
        String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        String[] daysKeys = {"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};
        
        for (int i = 0; i < daysOfWeek.length; i++) {
            TableRow row = new TableRow(this);
            
            TextView dayText = new TextView(this);
            dayText.setText(daysOfWeek[i]);
            dayText.setPadding(0, 8, 16, 8);
            dayText.setTextSize(14);
            
            TextView hoursText = new TextView(this);
            GymInfo.OperatingHours hours = gymInfo.getHours().get(daysKeys[i]);
            
            if (hours != null) {
                hoursText.setText(formatTime(hours.getOpen()) + " - " + formatTime(hours.getClose()));
            } else {
                hoursText.setText("Closed");
            }
            
            hoursText.setPadding(16, 8, 0, 8);
            hoursText.setTextSize(14);
            
            row.addView(dayText);
            row.addView(hoursText);
            scheduleTableLayout.addView(row);
        }
    }
    
    private String formatTime(String time) {
        // The repository already has a method for this, but we'll keep this simple
        String[] parts = time.split(":");
        int hour = Integer.parseInt(parts[0]);
        String ampm = hour >= 12 ? "PM" : "AM";
        hour = hour > 12 ? hour - 12 : hour;
        hour = hour == 0 ? 12 : hour;
        return String.format(Locale.getDefault(), "%d:%s %s", hour, parts[1], ampm);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 