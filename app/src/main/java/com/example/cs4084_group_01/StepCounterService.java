package com.example.cs4084_group_01;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import androidx.core.app.NotificationCompat;

import com.example.cs4084_group_01.model.StepData;
import com.example.cs4084_group_01.repository.StepDataRepository;
import com.example.cs4084_group_01.util.Constants;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class StepCounterService extends Service implements SensorEventListener {
    private static final String TAG = "StepCounterService";
    private static final long WAKE_LOCK_TIMEOUT = TimeUnit.HOURS.toMillis(1); // 1 hour timeout
    
    private SensorManager sensorManager;
    private Sensor stepSensor;
    private PowerManager.WakeLock wakeLock;
    private int initialSteps = -1;
    private int currentSteps = 0;
    private StepDataRepository repository;
    private boolean sensorAvailable = false;
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        repository = StepDataRepository.getInstance(this);
        
        // Initialize sensors
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepSensor = sensorManager != null ? sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) : null;
        sensorAvailable = (stepSensor != null);
        
        if (!sensorAvailable) {
            Log.w(TAG, "Step sensor not available on this device");
        }
        
        // Load saved steps
        currentSteps = repository.getCurrentSteps();
        
        // Create wake lock with timeout
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (powerManager != null) {
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, Constants.STEP_COUNTER_WAKE_LOCK_TAG);
            wakeLock.setReferenceCounted(false);
        }
        
        // Create notification channel for Android 8.0+
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Acquire wake lock with timeout
        if (wakeLock != null && !wakeLock.isHeld()) {
            wakeLock.acquire(WAKE_LOCK_TIMEOUT);
        }
        
        // Register step counter listener
        if (sensorAvailable) {
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);
            Log.d(TAG, "Step counter sensor registered");
        }
        
        // Start as a foreground service with notification
        startForeground(Constants.STEP_COUNTER_NOTIFICATION_ID, createNotification());
        
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        // Unregister sensor listener
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
            Log.d(TAG, "Step counter sensor unregistered");
        }
        
        // Release wake lock if held
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
            Log.d(TAG, "Wake lock released");
        }
        
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            int totalSteps = (int) event.values[0];
            
            // Initialize steps on first reading
            if (initialSteps == -1) {
                initialSteps = totalSteps;
                Log.d(TAG, "Initialized step counter with base value: " + initialSteps);
            }
            
            // Calculate steps since service started
            int stepsToday = totalSteps - initialSteps + currentSteps;
            
            // Save steps to repository
            repository.saveCurrentSteps(stepsToday);
            
            // Save to daily history with current goal
            int goal = getSharedPreferences(StepCounterActivity.PREFS_NAME, MODE_PRIVATE)
                    .getInt(StepCounterActivity.KEY_STEP_GOAL, 10000);
            StepData todayData = new StepData(new Date(), stepsToday, goal);
            repository.saveStepData(todayData);
            
            // Broadcast step update
            Intent intent = new Intent(Constants.ACTION_STEP_COUNTER_UPDATE);
            intent.putExtra(Constants.EXTRA_STEP_COUNT, stepsToday);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Set the package to make it explicit for Android 13+
                intent.setPackage(getPackageName());
            }
            sendBroadcast(intent);
            
            // Update notification
            NotificationManager notificationManager = 
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.notify(Constants.STEP_COUNTER_NOTIFICATION_ID, createNotification());
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Log accuracy changes but no action needed
        if (sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            Log.d(TAG, "Step sensor accuracy changed to: " + accuracy);
        }
    }
    
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    Constants.STEP_COUNTER_CHANNEL_ID,
                    "Step Counter Service",
                    NotificationManager.IMPORTANCE_LOW);
            channel.setDescription("Shows current step count");
            
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
    
    private Notification createNotification() {
        Intent notificationIntent = new Intent(this, StepCounterActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, Constants.STEP_COUNTER_CHANNEL_ID)
                .setContentTitle("Step Counter")
                .setContentText(currentSteps + " steps today")
                .setSmallIcon(R.drawable.ic_directions_walk)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_LOW);
                
        return builder.build();
    }
} 