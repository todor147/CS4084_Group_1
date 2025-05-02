package com.example.cs4084_group_01.repository;

import android.content.Context;

import com.example.cs4084_group_01.model.GymInfo;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class GymInfoRepository {
    private static final String GYM_JSON_FILE = "ul_gym_info.json";
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    
    private Context context;
    private GymInfo gymInfo;

    public GymInfoRepository(Context context) {
        this.context = context;
        loadGymInfo();
    }

    private void loadGymInfo() {
        try {
            InputStream is = context.getAssets().open(GYM_JSON_FILE);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, StandardCharsets.UTF_8);

            Gson gson = new Gson();
            gymInfo = gson.fromJson(json, GymInfo.class);
        } catch (IOException e) {
            e.printStackTrace();
            // If there's an error, create a default gym info
            gymInfo = createDefaultGymInfo();
        }
    }

    private GymInfo createDefaultGymInfo() {
        // Create a basic gym info if JSON loading fails
        GymInfo defaultInfo = new GymInfo();
        defaultInfo.setName("UL Sport Gym");
        defaultInfo.setDescription("Information currently unavailable.");
        return defaultInfo;
    }

    public GymInfo getGymInfo() {
        return gymInfo;
    }

    public boolean isGymOpen() {
        try {
            Calendar calendar = Calendar.getInstance();
            Date currentTime = calendar.getTime();
            String currentDateString = dateFormat.format(currentTime);
            
            // Check if today is a special day
            if (gymInfo.getSpecialHours() != null) {
                for (GymInfo.SpecialHours special : gymInfo.getSpecialHours()) {
                    if (special.getDate().equals(currentDateString)) {
                        // If open and close are both 00:00, gym is closed
                        if (special.getOpen().equals("00:00") && special.getClose().equals("00:00")) {
                            return false;
                        }
                        
                        Date openTime = timeFormat.parse(special.getOpen());
                        Date closeTime = timeFormat.parse(special.getClose());
                        
                        // Check if current time is between open and close times
                        String currentTimeString = timeFormat.format(currentTime);
                        Date parsedCurrentTime = timeFormat.parse(currentTimeString);
                        
                        return !parsedCurrentTime.before(openTime) && parsedCurrentTime.before(closeTime);
                    }
                }
            }
            
            // Get day of week (Monday = 1, Sunday = 7)
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            // Convert to 0-based index and adjust (in Calendar, Sunday = 1)
            int dayIndex = (dayOfWeek + 5) % 7;
            String[] daysOfWeek = {"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};
            String today = daysOfWeek[dayIndex];
            
            // Get regular hours for today
            GymInfo.OperatingHours hours = gymInfo.getHours().get(today);
            if (hours == null) {
                return false; // No hours found for today
            }
            
            Date openTime = timeFormat.parse(hours.getOpen());
            Date closeTime = timeFormat.parse(hours.getClose());
            
            // Check if current time is between open and close times
            String currentTimeString = timeFormat.format(currentTime);
            Date parsedCurrentTime = timeFormat.parse(currentTimeString);
            
            return !parsedCurrentTime.before(openTime) && parsedCurrentTime.before(closeTime);
            
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public String getTodayHours() {
        Calendar calendar = Calendar.getInstance();
        
        // Check for special hours
        String currentDateString = dateFormat.format(calendar.getTime());
        if (gymInfo.getSpecialHours() != null) {
            for (GymInfo.SpecialHours special : gymInfo.getSpecialHours()) {
                if (special.getDate().equals(currentDateString)) {
                    // If open and close are both 00:00, gym is closed
                    if (special.getOpen().equals("00:00") && special.getClose().equals("00:00")) {
                        return "Closed today" + (special.getNote() != null ? " - " + special.getNote() : "");
                    }
                    return formatTime(special.getOpen()) + " - " + formatTime(special.getClose()) + 
                           (special.getNote() != null ? " (" + special.getNote() + ")" : "");
                }
            }
        }
        
        // Regular hours
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int dayIndex = (dayOfWeek + 5) % 7;
        String[] daysOfWeek = {"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};
        String today = daysOfWeek[dayIndex];
        
        GymInfo.OperatingHours hours = gymInfo.getHours().get(today);
        if (hours == null) {
            return "Hours not available";
        }
        
        return formatTime(hours.getOpen()) + " - " + formatTime(hours.getClose());
    }
    
    private String formatTime(String time) {
        try {
            Date date = timeFormat.parse(time);
            return new SimpleDateFormat("h:mm a", Locale.getDefault()).format(date);
        } catch (ParseException e) {
            return time;
        }
    }
} 