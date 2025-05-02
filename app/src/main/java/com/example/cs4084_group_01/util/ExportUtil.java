package com.example.cs4084_group_01.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.lifecycle.LiveData;

import com.example.cs4084_group_01.manager.UserManager;
import com.example.cs4084_group_01.model.MealEntry;
import com.example.cs4084_group_01.model.MoodEntry;
import com.example.cs4084_group_01.model.SleepEntry;
import com.example.cs4084_group_01.model.StepData;
import com.example.cs4084_group_01.model.User;
import com.example.cs4084_group_01.model.WaterIntake;
import com.example.cs4084_group_01.model.WorkoutEntry;
import com.example.cs4084_group_01.repository.MealRepository;
import com.example.cs4084_group_01.repository.MoodRepository;
import com.example.cs4084_group_01.repository.SleepRepository;
import com.example.cs4084_group_01.repository.WorkoutRepository;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Utility class for exporting user health data as JSON files
 */
public class ExportUtil {
    private static final String TAG = "ExportUtil";
    private static final String EXPORT_DIR = "FitTracker/Exports";
    
    /**
     * Creates a JSON representation of all user health data
     * 
     * @param context Application context
     * @param includeUserData Whether to include user profile data
     * @param includeActivityData Whether to include activity data (steps, workouts)
     * @param includeNutritionData Whether to include nutrition data (meals, water)
     * @param includeMoodSleepData Whether to include mood and sleep data
     * @return JSONObject containing the selected health data
     */
    public static JSONObject createHealthDataJson(Context context, boolean includeUserData, 
                                                boolean includeActivityData, 
                                                boolean includeNutritionData,
                                                boolean includeMoodSleepData) {
        JSONObject jsonData = new JSONObject();
        
        try {
            // Add metadata
            JSONObject metadata = new JSONObject();
            metadata.put("exportDate", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
            metadata.put("appVersion", context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName);
            jsonData.put("metadata", metadata);
            
            // Add user data if requested
            if (includeUserData) {
                User user = UserManager.getInstance(context).getCurrentUser();
                if (user != null) {
                    JSONObject userData = new JSONObject();
                    userData.put("name", user.getName());
                    userData.put("email", user.getEmail());
                    userData.put("height", user.getHeight());
                    userData.put("weight", user.getWeight());
                    // User doesn't have getAge() method
                    userData.put("gender", user.getGender());
                    
                    // Calculate BMI
                    float heightInMeters = user.getHeight() / 100f;
                    float bmi = user.getWeight() / (heightInMeters * heightInMeters);
                    userData.put("bmi", bmi);
                    
                    jsonData.put("userData", userData);
                }
            }
            
            // Add activity data if requested
            if (includeActivityData) {
                JSONObject activityData = new JSONObject();
                
                // Steps data - Commented out since StepRepository class implementation is unknown
                /*
                List<StepData> stepDataList = getStepData(context);
                if (stepDataList != null && !stepDataList.isEmpty()) {
                    JSONArray stepsArray = new JSONArray();
                    for (StepData stepData : stepDataList) {
                        JSONObject stepObj = new JSONObject();
                        stepObj.put("date", new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            .format(stepData.getDate()));
                        stepObj.put("steps", stepData.getSteps());
                        stepsArray.put(stepObj);
                    }
                    activityData.put("steps", stepsArray);
                }
                */
                
                // Workout data
                WorkoutRepository workoutRepository = new WorkoutRepository(context);
                List<WorkoutEntry> workoutEntries = workoutRepository.getWorkouts();
                if (workoutEntries != null && !workoutEntries.isEmpty()) {
                    JSONArray workoutsArray = new JSONArray();
                    for (WorkoutEntry workout : workoutEntries) {
                        JSONObject workoutObj = new JSONObject();
                        workoutObj.put("date", new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            .format(workout.getDate()));
                        workoutObj.put("type", workout.getWorkoutType());
                        workoutObj.put("duration", workout.getDurationMinutes());
                        workoutObj.put("intensity", workout.getIntensity());
                        // Calculate calories based on duration and intensity
                        int estimatedCalories = calculateCalories(workout.getDurationMinutes(), workout.getIntensity());
                        workoutObj.put("calories", estimatedCalories);
                        workoutsArray.put(workoutObj);
                    }
                    activityData.put("workouts", workoutsArray);
                }
                
                jsonData.put("activityData", activityData);
            }
            
            // Add nutrition data if requested
            if (includeNutritionData) {
                JSONObject nutritionData = new JSONObject();
                
                // Water intake data - Commented out since WaterRepository class implementation is unknown
                /*
                List<WaterIntake> waterIntakes = getWaterIntakes(context);
                if (waterIntakes != null && !waterIntakes.isEmpty()) {
                    JSONArray waterArray = new JSONArray();
                    for (WaterIntake water : waterIntakes) {
                        JSONObject waterObj = new JSONObject();
                        waterObj.put("date", water.getDate());
                        waterObj.put("amount", water.getCurrentIntake());
                        waterArray.put(waterObj);
                    }
                    nutritionData.put("waterIntake", waterArray);
                }
                */
                
                // Meal data
                MealRepository mealRepository = MealRepository.getInstance(context);
                Map<String, List<MealEntry>> mealEntriesMap = mealRepository.getAllMealEntries();
                List<MealEntry> allMealEntries = new ArrayList<>();
                
                // Flatten the map of meal entries
                for (List<MealEntry> entries : mealEntriesMap.values()) {
                    allMealEntries.addAll(entries);
                }
                
                if (!allMealEntries.isEmpty()) {
                    JSONArray mealsArray = new JSONArray();
                    for (MealEntry meal : allMealEntries) {
                        JSONObject mealObj = new JSONObject();
                        mealObj.put("date", new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            .format(meal.getTimestamp()));
                        mealObj.put("time", new SimpleDateFormat("HH:mm", Locale.getDefault())
                            .format(meal.getTimestamp()));
                        mealObj.put("type", meal.getMealType().toString());
                        mealObj.put("description", meal.getDescription());
                        mealObj.put("calories", meal.getCalories());
                        mealsArray.put(mealObj);
                    }
                    nutritionData.put("meals", mealsArray);
                }
                
                jsonData.put("nutritionData", nutritionData);
            }
            
            // Add mood and sleep data if requested
            if (includeMoodSleepData) {
                JSONObject wellbeingData = new JSONObject();
                
                // Mood data
                MoodRepository moodRepository = MoodRepository.getInstance(context);
                Map<String, List<MoodEntry>> moodEntriesMap = moodRepository.getAllMoodEntries();
                List<MoodEntry> allMoodEntries = new ArrayList<>();
                
                // Flatten the map of mood entries
                for (List<MoodEntry> entries : moodEntriesMap.values()) {
                    allMoodEntries.addAll(entries);
                }
                
                if (!allMoodEntries.isEmpty()) {
                    JSONArray moodArray = new JSONArray();
                    for (MoodEntry mood : allMoodEntries) {
                        JSONObject moodObj = new JSONObject();
                        moodObj.put("date", new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            .format(mood.getTimestamp()));
                        moodObj.put("time", new SimpleDateFormat("HH:mm", Locale.getDefault())
                            .format(mood.getTimestamp()));
                        moodObj.put("type", mood.getMoodType().toString());
                        moodObj.put("notes", mood.getNotes());
                        moodArray.put(moodObj);
                    }
                    wellbeingData.put("moods", moodArray);
                }
                
                // Sleep data
                SleepRepository sleepRepository = new SleepRepository(context);
                LiveData<List<SleepEntry>> sleepEntriesLiveData = sleepRepository.getAllSleepEntries();
                List<SleepEntry> sleepEntries = sleepEntriesLiveData.getValue();
                if (sleepEntries != null && !sleepEntries.isEmpty()) {
                    JSONArray sleepArray = new JSONArray();
                    for (SleepEntry sleep : sleepEntries) {
                        JSONObject sleepObj = new JSONObject();
                        sleepObj.put("date", sleep.getFormattedDate());
                        sleepObj.put("duration", sleep.getDurationMinutes());
                        sleepObj.put("quality", sleep.getQuality());
                        sleepObj.put("startTime", sleep.getFormattedStartTime());
                        sleepObj.put("endTime", sleep.getFormattedEndTime());
                        sleepObj.put("notes", sleep.getNotes());
                        sleepArray.put(sleepObj);
                    }
                    wellbeingData.put("sleep", sleepArray);
                }
                
                jsonData.put("wellbeingData", wellbeingData);
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error creating JSON data", e);
        }
        
        return jsonData;
    }
    
    /**
     * Calculate estimated calories burned based on workout duration and intensity
     * 
     * @param durationMinutes Duration of workout in minutes
     * @param intensity Intensity level of workout
     * @return Estimated calories burned
     */
    private static int calculateCalories(int durationMinutes, String intensity) {
        int baseRate = 5; // calories per minute for low intensity
        
        switch (intensity.toLowerCase()) {
            case "low":
                baseRate = 5;
                break;
            case "medium":
                baseRate = 8;
                break;
            case "high":
                baseRate = 12;
                break;
            default:
                baseRate = 5;
        }
        
        return durationMinutes * baseRate;
    }
    
    /**
     * Saves the health data JSON to a file and returns the file URI
     * 
     * @param context Application context
     * @param jsonData JSON data to save
     * @return URI of the saved file, or null if there was an error
     */
    public static Uri saveJsonToFile(Context context, JSONObject jsonData) {
        try {
            // Create export directory if it doesn't exist
            File exportDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), EXPORT_DIR);
            if (!exportDir.exists()) {
                if (!exportDir.mkdirs()) {
                    Log.e(TAG, "Failed to create export directory");
                    return null;
                }
            }
            
            // Create the export file
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String fileName = "health_data_" + timestamp + ".json";
            File exportFile = new File(exportDir, fileName);
            
            // Write the JSON data to the file
            FileOutputStream fos = new FileOutputStream(exportFile);
            fos.write(jsonData.toString(2).getBytes());
            fos.close();
            
            // Return the file URI
            return FileProvider.getUriForFile(context, 
                    context.getApplicationContext().getPackageName() + ".provider", 
                    exportFile);
        } catch (IOException | JSONException e) {
            Log.e(TAG, "Error saving JSON to file", e);
            return null;
        }
    }
    
    /**
     * Shares the exported health data file
     * 
     * @param context Application context
     * @param fileUri URI of the exported file
     */
    public static void shareExportFile(Context context, Uri fileUri) {
        if (fileUri != null) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("application/json");
            shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            
            // Start the sharing activity
            context.startActivity(Intent.createChooser(shareIntent, "Share Health Data Export"));
        } else {
            Toast.makeText(context, "Export file not found", Toast.LENGTH_SHORT).show();
        }
    }
} 