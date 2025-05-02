package com.example.cs4084_group_01;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import com.example.cs4084_group_01.model.MealEntry;
import com.example.cs4084_group_01.model.MealType;
import com.example.cs4084_group_01.model.MeditationSession;
import com.example.cs4084_group_01.model.MoodEntry;
import com.example.cs4084_group_01.model.MoodType;
import com.example.cs4084_group_01.model.SleepEntry;
import com.example.cs4084_group_01.model.StepData;
import com.example.cs4084_group_01.model.WaterIntake;
import com.example.cs4084_group_01.model.WorkoutEntry;
import com.example.cs4084_group_01.repository.MoodRepository;
import com.example.cs4084_group_01.repository.SleepRepository;
import com.example.cs4084_group_01.repository.StepDataRepository;
import com.example.cs4084_group_01.repository.WaterIntakeRepository;
import com.example.cs4084_group_01.util.DateUtils;
import com.example.cs4084_group_01.util.DemoDataProvider;
import com.example.cs4084_group_01.util.UnitManager;
import com.example.cs4084_group_01.viewmodel.HealthDashboardViewModel;
import com.example.cs4084_group_01.viewmodel.SleepViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;

public class HealthDashboardActivity extends BaseActivity {
    private HealthDashboardViewModel viewModel;
    private SleepViewModel sleepViewModel;
    
    // UI Components
    private TextView todayDateText;
    private TextView stepsCountText;
    private TextView stepsGoalText;
    private LinearProgressIndicator stepsProgressIndicator;
    private TextView waterIntakeText;
    private TextView waterGoalText;
    private LinearProgressIndicator waterProgressIndicator;
    private TextView moodTypeText;
    private TextView moodTimeText;
    private TextView mealsCountText;
    private TextView caloriesText;
    private TextView mealsSummaryText;
    private TextView workoutsCountText;
    private TextView totalMinutesText;
    private TextView workoutsSummaryText;
    private TextView meditationCountText;
    private TextView totalMeditationMinutesText;
    private TextView meditationSummaryText;
    private TextView sleepDurationText;
    private TextView sleepQualityText;
    private TextView sleepTimeRangeText;
    private MaterialButton resetButton;
    private FloatingActionButton addButton;
    private MaterialButton trackButton;
    private MaterialButton gymButton;
    
    // Cards
    private MaterialCardView stepsCard;
    private MaterialCardView waterCard;
    private MaterialCardView moodCard;
    private MaterialCardView mealsCard;
    private MaterialCardView workoutsCard;
    private MaterialCardView meditationCard;
    private MaterialCardView sleepCard;
    
    private SimpleDateFormat dateFormat;
    private SimpleDateFormat timeFormat;
    
    // Demo mode
    private boolean isDemoMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_dashboard);
        
        // Set up toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        
        // Check if in demo mode
        isDemoMode = DemoDataProvider.isDemoModeEnabled(this);
        
        // Initialize date formatters
        dateFormat = new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault());
        timeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
        
        // Initialize ViewModels if not in demo mode
        if (!isDemoMode) {
            viewModel = new ViewModelProvider(this).get(HealthDashboardViewModel.class);
            sleepViewModel = new ViewModelProvider(this).get(SleepViewModel.class);
        }
        
        // Initialize views
        initializeViews();
        
        // Set up listeners
        setupListeners();
        
        if (isDemoMode) {
            // In demo mode, load sample data directly
            loadDemoData();
        } else {
            // In normal mode, set up observers and load data from repositories
            setupObservers();
            
            // Load data
            viewModel.loadAllHealthData();
            
            // Set current date for sleep data
            sleepViewModel.setSelectedDate(new Date());
        }
        
        // Update date display
        updateDateDisplay();
    }
    
    /**
     * Load sample data for demo mode
     */
    private void loadDemoData() {
        // Show toast indicating demo mode
        Toast.makeText(this, "Demo mode: Sample health data is shown", Toast.LENGTH_SHORT).show();
        
        // Generate sample step data
        int steps = DemoDataProvider.getTodayStepCount();
        int stepGoal = DemoDataProvider.getStepGoal();
        StepData stepData = new StepData(new Date(), steps, stepGoal);
        updateStepData(stepData);
        
        // Generate sample water intake data
        float waterAmount = DemoDataProvider.getTodayWaterIntake();
        float waterGoal = DemoDataProvider.getWaterGoal();
        WaterIntake waterIntake = new WaterIntake(new Date(), waterAmount, waterGoal);
        updateWaterIntake(waterIntake);
        
        // Generate sample mood data
        int moodLevel = DemoDataProvider.getRandomInt(1, 5);
        MoodType[] moodTypes = MoodType.values();
        MoodType moodType = moodTypes[moodLevel - 1];
        String moodNote = DemoDataProvider.getSampleMoodNote(moodLevel);
        MoodEntry moodEntry = new MoodEntry(moodType, new Date(), moodNote);
        updateMoodData(moodEntry);
        
        // Generate sample meal data
        List<MealEntry> meals = generateSampleMeals();
        updateMealData(meals);
        // Calculate total calories
        int totalCalories = 0;
        for (MealEntry meal : meals) {
            totalCalories += meal.getCalories();
        }
        caloriesText.setText(String.format(Locale.getDefault(), "%d calories", totalCalories));
        
        // Generate sample workout data
        List<WorkoutEntry> workouts = generateSampleWorkouts();
        updateWorkoutData(workouts);
        // Calculate total workout minutes
        int totalWorkoutMinutes = 0;
        for (WorkoutEntry workout : workouts) {
            totalWorkoutMinutes += workout.getDurationMinutes();
        }
        totalMinutesText.setText(String.format(Locale.getDefault(), "%d minutes", totalWorkoutMinutes));
        
        // Generate sample meditation data
        List<MeditationSession> meditationSessions = generateSampleMeditationSessions();
        updateMeditationData(meditationSessions);
        // Calculate total meditation minutes
        int totalMeditationMinutes = 0;
        for (MeditationSession session : meditationSessions) {
            totalMeditationMinutes += session.getDurationMinutes();
        }
        totalMeditationMinutesText.setText(String.format(Locale.getDefault(), "%d minutes", totalMeditationMinutes));
        
        // Generate sample sleep data
        List<SleepEntry> sleepEntries = generateSampleSleepEntries();
        updateSleepData(sleepEntries);
        
        // Generate sample sleep statistics
        Map<String, Double> sleepStats = new HashMap<>();
        sleepStats.put("avgDuration", 7.5);
        sleepStats.put("avgQuality", 4.0);
        updateSleepStatistics(sleepStats);
    }
    
    /**
     * Generate sample meals for demo mode
     */
    private List<MealEntry> generateSampleMeals() {
        List<MealEntry> meals = new ArrayList<>();
        Date today = new Date();
        
        // Generate breakfast
        Calendar breakfast = Calendar.getInstance();
        breakfast.setTime(today);
        breakfast.set(Calendar.HOUR_OF_DAY, 8);
        breakfast.set(Calendar.MINUTE, DemoDataProvider.getRandomInt(0, 59));
        
        List<String> breakfastOptions = DemoDataProvider.getSampleBreakfastOptions();
        String breakfastDesc = breakfastOptions.get(DemoDataProvider.getRandomInt(0, breakfastOptions.size() - 1));
        
        MealEntry breakfastEntry = new MealEntry();
        breakfastEntry.setMealType(MealType.BREAKFAST);
        breakfastEntry.setTimestamp(breakfast.getTime());
        breakfastEntry.setDescription(breakfastDesc);
        breakfastEntry.setCalories(DemoDataProvider.getRandomInt(300, 600));
        breakfastEntry.setNotes("Morning meal");
        meals.add(breakfastEntry);
        
        // Generate lunch
        Calendar lunch = Calendar.getInstance();
        lunch.setTime(today);
        lunch.set(Calendar.HOUR_OF_DAY, 13);
        lunch.set(Calendar.MINUTE, DemoDataProvider.getRandomInt(0, 59));
        
        List<String> lunchOptions = DemoDataProvider.getSampleLunchOptions();
        String lunchDesc = lunchOptions.get(DemoDataProvider.getRandomInt(0, lunchOptions.size() - 1));
        
        MealEntry lunchEntry = new MealEntry();
        lunchEntry.setMealType(MealType.LUNCH);
        lunchEntry.setTimestamp(lunch.getTime());
        lunchEntry.setDescription(lunchDesc);
        lunchEntry.setCalories(DemoDataProvider.getRandomInt(400, 800));
        lunchEntry.setNotes("Midday meal");
        meals.add(lunchEntry);
        
        // Generate dinner
        Calendar dinner = Calendar.getInstance();
        dinner.setTime(today);
        dinner.set(Calendar.HOUR_OF_DAY, 19);
        dinner.set(Calendar.MINUTE, DemoDataProvider.getRandomInt(0, 59));
        
        List<String> dinnerOptions = DemoDataProvider.getSampleDinnerOptions();
        String dinnerDesc = dinnerOptions.get(DemoDataProvider.getRandomInt(0, dinnerOptions.size() - 1));
        
        MealEntry dinnerEntry = new MealEntry();
        dinnerEntry.setMealType(MealType.DINNER);
        dinnerEntry.setTimestamp(dinner.getTime());
        dinnerEntry.setDescription(dinnerDesc);
        dinnerEntry.setCalories(DemoDataProvider.getRandomInt(500, 900));
        dinnerEntry.setNotes("Evening meal");
        meals.add(dinnerEntry);
        
        return meals;
    }
    
    /**
     * Generate sample workouts for demo mode
     */
    private List<WorkoutEntry> generateSampleWorkouts() {
        List<WorkoutEntry> workouts = new ArrayList<>();
        Date today = new Date();
        
        // Morning workout
        Calendar morningWorkout = Calendar.getInstance();
        morningWorkout.setTime(today);
        morningWorkout.set(Calendar.HOUR_OF_DAY, 7);
        morningWorkout.set(Calendar.MINUTE, DemoDataProvider.getRandomInt(0, 59));
        
        List<String> workoutTypes = DemoDataProvider.getSampleWorkoutTypes();
        List<String> intensities = DemoDataProvider.getSampleWorkoutIntensities();
        
        String morningType = workoutTypes.get(DemoDataProvider.getRandomInt(0, workoutTypes.size() - 1));
        String morningIntensity = intensities.get(DemoDataProvider.getRandomInt(0, intensities.size() - 1));
        int morningDuration = DemoDataProvider.getRandomInt(20, 45);
        
        WorkoutEntry morningEntry = new WorkoutEntry(morningType, morningDuration, morningIntensity);
        morningEntry.setDate(morningWorkout.getTime());
        workouts.add(morningEntry);
        
        // Evening workout (50% chance)
        if (DemoDataProvider.getRandomInt(0, 1) > 0) {
            Calendar eveningWorkout = Calendar.getInstance();
            eveningWorkout.setTime(today);
            eveningWorkout.set(Calendar.HOUR_OF_DAY, 18);
            eveningWorkout.set(Calendar.MINUTE, DemoDataProvider.getRandomInt(0, 59));
            
            String eveningType = workoutTypes.get(DemoDataProvider.getRandomInt(0, workoutTypes.size() - 1));
            String eveningIntensity = intensities.get(DemoDataProvider.getRandomInt(0, intensities.size() - 1));
            int eveningDuration = DemoDataProvider.getRandomInt(30, 60);
            
            WorkoutEntry eveningEntry = new WorkoutEntry(eveningType, eveningDuration, eveningIntensity);
            eveningEntry.setDate(eveningWorkout.getTime());
            workouts.add(eveningEntry);
        }
        
        return workouts;
    }
    
    /**
     * Generate sample meditation sessions for demo mode
     */
    private List<MeditationSession> generateSampleMeditationSessions() {
        List<MeditationSession> sessions = new ArrayList<>();
        Date today = new Date();
        
        // Morning meditation session
        Calendar morningSession = Calendar.getInstance();
        morningSession.setTime(today);
        morningSession.set(Calendar.HOUR_OF_DAY, 8);
        morningSession.set(Calendar.MINUTE, DemoDataProvider.getRandomInt(0, 59));
        
        Date sessionTime = morningSession.getTime();
        int duration = DemoDataProvider.getRandomInt(5, 15);
        
        MeditationSession session = new MeditationSession(sessionTime, duration);
        sessions.add(session);
        
        return sessions;
    }
    
    /**
     * Generate sample sleep entries for demo mode
     */
    private List<SleepEntry> generateSampleSleepEntries() {
        List<SleepEntry> sleepEntries = new ArrayList<>();
        
        // Last night's sleep
        Calendar bedtime = Calendar.getInstance();
        bedtime.add(Calendar.DAY_OF_YEAR, -1);
        bedtime.set(Calendar.HOUR_OF_DAY, 23);
        bedtime.set(Calendar.MINUTE, DemoDataProvider.getRandomInt(0, 59));
        
        Calendar wakeTime = Calendar.getInstance();
        wakeTime.set(Calendar.HOUR_OF_DAY, 7);
        wakeTime.set(Calendar.MINUTE, DemoDataProvider.getRandomInt(0, 59));
        
        int quality = DemoDataProvider.getRandomInt(3, 5);
        String notes = quality >= 4 ? "Slept well" : "Average sleep";
        
        SleepEntry sleepEntry = new SleepEntry(bedtime.getTime(), wakeTime.getTime(), quality, notes);
        sleepEntries.add(sleepEntry);
        
        return sleepEntries;
    }
    
    private void initializeViews() {
        // TextViews
        todayDateText = findViewById(R.id.todayDateText);
        stepsCountText = findViewById(R.id.stepsCountText);
        stepsGoalText = findViewById(R.id.stepsGoalText);
        stepsProgressIndicator = findViewById(R.id.stepsProgressIndicator);
        waterIntakeText = findViewById(R.id.waterIntakeText);
        waterGoalText = findViewById(R.id.waterGoalText);
        waterProgressIndicator = findViewById(R.id.waterProgressIndicator);
        moodTypeText = findViewById(R.id.moodTypeText);
        moodTimeText = findViewById(R.id.moodTimeText);
        mealsCountText = findViewById(R.id.mealsCountText);
        caloriesText = findViewById(R.id.caloriesText);
        mealsSummaryText = findViewById(R.id.mealsSummaryText);
        workoutsCountText = findViewById(R.id.workoutsCountText);
        totalMinutesText = findViewById(R.id.totalMinutesText);
        workoutsSummaryText = findViewById(R.id.workoutsSummaryText);
        meditationCountText = findViewById(R.id.meditationCountText);
        totalMeditationMinutesText = findViewById(R.id.totalMeditationMinutesText);
        meditationSummaryText = findViewById(R.id.meditationSummaryText);
        sleepDurationText = findViewById(R.id.sleepDurationText);
        sleepQualityText = findViewById(R.id.sleepQualityText);
        sleepTimeRangeText = findViewById(R.id.sleepTimeRangeText);
        
        // Buttons
        resetButton = findViewById(R.id.resetButton);
        addButton = findViewById(R.id.addButton);
        trackButton = findViewById(R.id.trackButton);
        gymButton = findViewById(R.id.gymButton);
        
        // Cards
        stepsCard = findViewById(R.id.stepsCard);
        waterCard = findViewById(R.id.waterCard);
        moodCard = findViewById(R.id.moodCard);
        mealsCard = findViewById(R.id.mealsCard);
        workoutsCard = findViewById(R.id.workoutsCard);
        meditationCard = findViewById(R.id.meditationCard);
        sleepCard = findViewById(R.id.sleepCard);
    }
    
    private void setupListeners() {
        // Reset button
        resetButton.setOnClickListener(v -> {
            viewModel.resetDailyStats();
            Toast.makeText(this, "Daily stats have been reset", Toast.LENGTH_SHORT).show();
        });
        
        // Add button
        addButton.setOnClickListener(v -> {
            showAddActivityBottomSheet();
        });
        
        // Track button
        trackButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, HealthProgressActivity.class);
            startActivity(intent);
        });
        
        // Gym button
        gymButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, CampusGymActivity.class);
            startActivity(intent);
        });
        
        // Step card
        stepsCard.setOnClickListener(v -> {
            Intent intent = new Intent(this, StepCounterActivity.class);
            startActivity(intent);
        });
        
        // Water card
        waterCard.setOnClickListener(v -> {
            Intent intent = new Intent(this, WaterTrackingActivity.class);
            startActivity(intent);
        });
        
        // Mood card
        moodCard.setOnClickListener(v -> {
            Intent intent = new Intent(this, MoodTrackerActivity.class);
            startActivity(intent);
        });
        
        // Meals card
        mealsCard.setOnClickListener(v -> {
            Intent intent = new Intent(this, MealLoggerActivity.class);
            startActivity(intent);
        });
        
        // Workouts card
        workoutsCard.setOnClickListener(v -> {
            Intent intent = new Intent(this, WorkoutTrackingActivity.class);
            startActivity(intent);
        });
        
        // Meditation card
        meditationCard.setOnClickListener(v -> {
            Intent intent = new Intent(this, MeditationTimerActivity.class);
            startActivity(intent);
        });
        
        // Sleep card
        sleepCard.setOnClickListener(v -> {
            Intent intent = new Intent(this, SleepTrackingActivity.class);
            startActivity(intent);
        });
    }
    
    // Show the add activity bottom sheet dialog
    private void showAddActivityBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_add_activity, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        
        // Set up click listeners for each option
        bottomSheetView.findViewById(R.id.addWaterOption).setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            Intent intent = new Intent(this, WaterTrackingActivity.class);
            intent.putExtra("quick_add", true);
            startActivity(intent);
        });
        
        bottomSheetView.findViewById(R.id.addStepsOption).setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            Intent intent = new Intent(this, StepCounterActivity.class);
            startActivity(intent);
        });
        
        bottomSheetView.findViewById(R.id.addMoodOption).setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            Intent intent = new Intent(this, MoodTrackerActivity.class);
            startActivity(intent);
        });
        
        bottomSheetView.findViewById(R.id.addMealOption).setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            Intent intent = new Intent(this, MealLoggerActivity.class);
            startActivity(intent);
        });
        
        bottomSheetView.findViewById(R.id.addWorkoutOption).setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            Intent intent = new Intent(this, WorkoutTrackingActivity.class);
            startActivity(intent);
        });
        
        bottomSheetView.findViewById(R.id.addMeditationOption).setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            Intent intent = new Intent(this, MeditationTimerActivity.class);
            startActivity(intent);
        });
        
        bottomSheetView.findViewById(R.id.addSleepOption).setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            Intent intent = new Intent(this, SleepTrackingActivity.class);
            startActivity(intent);
        });
        
        bottomSheetDialog.show();
    }
    
    private void setupObservers() {
        // Observe step data
        viewModel.getStepData().observe(this, this::updateStepData);
        
        // Observe water intake data
        viewModel.getWaterIntake().observe(this, this::updateWaterIntake);
        
        // Observe mood data
        viewModel.getMoodEntry().observe(this, this::updateMoodData);
        
        // Observe meal data
        viewModel.getTodayMeals().observe(this, this::updateMealData);
        
        // Observe total calories
        viewModel.getTotalCalories().observe(this, calories -> {
            caloriesText.setText(String.format(Locale.getDefault(), "%d calories", calories));
        });
        
        // Observe workout data
        viewModel.getTodayWorkouts().observe(this, this::updateWorkoutData);
        
        // Observe total workout minutes
        viewModel.getTotalWorkoutMinutes().observe(this, minutes -> {
            totalMinutesText.setText(String.format(Locale.getDefault(), "%d minutes", minutes));
        });
        
        // Observe meditation data
        viewModel.getTodayMeditations().observe(this, this::updateMeditationData);
        
        // Observe total meditation minutes
        viewModel.getTotalMeditationMinutes().observe(this, minutes -> {
            totalMeditationMinutesText.setText(String.format(Locale.getDefault(), "%d minutes", minutes));
        });
        
        // Observe sleep data
        sleepViewModel.getSleepEntriesForSelectedDate().observe(this, this::updateSleepData);
        
        // Observe sleep statistics
        sleepViewModel.getWeeklyStatistics().observe(this, this::updateSleepStatistics);
    }
    
    private void updateDateDisplay() {
        Calendar calendar = Calendar.getInstance();
        todayDateText.setText(dateFormat.format(calendar.getTime()));
    }
    
    private void updateStepData(StepData stepData) {
        if (stepData != null) {
            int steps = stepData.getSteps();
            int goal = stepData.getGoal();
            int progress = (int) (((float) steps / goal) * 100);
            
            stepsCountText.setText(String.valueOf(steps));
            stepsGoalText.setText("Goal: " + goal + " steps");
            stepsProgressIndicator.setProgress(Math.min(progress, 100));
        } else {
            stepsCountText.setText("0");
            stepsGoalText.setText("Goal: 10,000 steps");
            stepsProgressIndicator.setProgress(0);
        }
    }
    
    private void updateWaterIntake(WaterIntake waterIntake) {
        int dailyGoal = 2000; // Default 2L (2000ml)
        if (waterIntake != null) {
            // Display water intake in preferred units
            String formattedWaterAmount = UnitManager.formatVolume(this, (int)waterIntake.getCurrentIntake());
            waterIntakeText.setText(formattedWaterAmount);
            
            // Display goal in preferred units
            String formattedWaterGoal = UnitManager.formatVolume(this, dailyGoal);
            waterGoalText.setText("Goal: " + formattedWaterGoal);
            
            // Update progress
            int progress = (int) (((float) waterIntake.getCurrentIntake() / dailyGoal) * 100);
            waterProgressIndicator.setProgress(progress);
        } else {
            // No water intake data for today
            String formattedWaterAmount = UnitManager.formatVolume(this, 0);
            waterIntakeText.setText(formattedWaterAmount);
            
            String formattedWaterGoal = UnitManager.formatVolume(this, dailyGoal);
            waterGoalText.setText("Goal: " + formattedWaterGoal);
            
            waterProgressIndicator.setProgress(0);
        }
    }
    
    private void updateMoodData(MoodEntry moodEntry) {
        if (moodEntry != null) {
            MoodType moodType = moodEntry.getMoodType();
            Date timestamp = moodEntry.getTimestamp();
            
            moodTypeText.setText(moodType.getDisplayName());
            moodTimeText.setText("Last updated: " + timeFormat.format(timestamp));
        } else {
            moodTypeText.setText("No mood recorded");
            moodTimeText.setText("Last updated: -");
        }
    }
    
    private void updateMealData(List<MealEntry> meals) {
        if (meals != null && !meals.isEmpty()) {
            int mealCount = meals.size();
            mealsCountText.setText(String.format(Locale.getDefault(), "%d meal%s", 
                    mealCount, mealCount == 1 ? "" : "s"));
            
            // Create a meal type summary
            Map<MealType, Integer> mealTypeCounts = new HashMap<>();
            for (MealEntry meal : meals) {
                MealType type = meal.getMealType();
                mealTypeCounts.put(type, mealTypeCounts.getOrDefault(type, 0) + 1);
            }
            
            StringBuilder summary = new StringBuilder();
            for (Map.Entry<MealType, Integer> entry : mealTypeCounts.entrySet()) {
                if (summary.length() > 0) {
                    summary.append(", ");
                }
                summary.append(entry.getKey().getDisplayName())
                       .append(": ")
                       .append(entry.getValue());
            }
            
            mealsSummaryText.setText(summary.toString());
        } else {
            mealsCountText.setText("0 meals");
            mealsSummaryText.setText("No meals recorded today");
        }
    }
    
    private void updateWorkoutData(List<WorkoutEntry> workouts) {
        if (workouts != null && !workouts.isEmpty()) {
            // Count workouts
            int count = workouts.size();
            workoutsCountText.setText(String.valueOf(count));
            
            // Calculate total duration
            int totalMinutes = 0;
            for (WorkoutEntry workout : workouts) {
                totalMinutes += workout.getDurationMinutes();
            }
            
            // Get the most recent workout
            StringBuilder summary = new StringBuilder();
            
            WorkoutEntry latestWorkout = workouts.get(workouts.size() - 1);
            summary.append(latestWorkout.getWorkoutType());
            summary.append(" (");
            summary.append(latestWorkout.getIntensity());
            summary.append(")");
            
            totalMinutesText.setText(String.valueOf(totalMinutes) + " min");
            workoutsSummaryText.setText(summary.toString());
        } else {
            // No workouts data for today
            workoutsCountText.setText("0");
            totalMinutesText.setText("0 min");
            workoutsSummaryText.setText("No workouts today");
        }
    }
    
    private void updateMeditationData(List<MeditationSession> sessions) {
        if (sessions != null && !sessions.isEmpty()) {
            int sessionCount = sessions.size();
            meditationCountText.setText(String.format(Locale.getDefault(), "%d session%s", 
                    sessionCount, sessionCount == 1 ? "" : "s"));
            
            // Create a simple summary of duration ranges
            StringBuilder summary = new StringBuilder();
            int shortSessions = 0;  // < 10 minutes
            int mediumSessions = 0; // 10-20 minutes
            int longSessions = 0;   // > 20 minutes
            
            for (MeditationSession session : sessions) {
                long duration = session.getDurationMinutes();
                if (duration < 10) {
                    shortSessions++;
                } else if (duration <= 20) {
                    mediumSessions++;
                } else {
                    longSessions++;
                }
            }
            
            if (shortSessions > 0) {
                summary.append("Short (<10m): ").append(shortSessions);
            }
            if (mediumSessions > 0) {
                if (summary.length() > 0) summary.append(", ");
                summary.append("Medium (10-20m): ").append(mediumSessions);
            }
            if (longSessions > 0) {
                if (summary.length() > 0) summary.append(", ");
                summary.append("Long (>20m): ").append(longSessions);
            }
            
            meditationSummaryText.setText(summary.toString());
        } else {
            meditationCountText.setText("0 sessions");
            meditationSummaryText.setText("No meditation sessions today");
        }
    }
    
    private void updateSleepData(List<SleepEntry> sleepEntries) {
        if (sleepEntries != null && !sleepEntries.isEmpty()) {
            // Get the most recent sleep entry for display
            SleepEntry latestEntry = sleepEntries.get(0);
            
            sleepDurationText.setText(latestEntry.getFormattedDuration());
            sleepQualityText.setText("Quality: " + latestEntry.getQuality() + "/5");
            sleepTimeRangeText.setText("Time: " + latestEntry.getFormattedStartTime() + " - " + 
                    latestEntry.getFormattedEndTime());
        } else {
            sleepDurationText.setText("No sleep data");
            sleepQualityText.setText("Quality: --");
            sleepTimeRangeText.setText("Time: --");
        }
    }
    
    private void updateSleepStatistics(Map<String, Double> statistics) {
        if (statistics != null) {
            // Format sleep duration based on unit preferences
            double avgDuration = statistics.getOrDefault("averageDuration", 0.0);
            sleepDurationText.setText(String.format("%.1f hrs", avgDuration));
            
            double avgQuality = statistics.getOrDefault("averageQuality", 0.0);
            sleepQualityText.setText(String.format("%.1f/5", avgQuality));
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.health_dashboard_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (itemId == R.id.action_export) {
            Intent intent = new Intent(this, ExportDataActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 