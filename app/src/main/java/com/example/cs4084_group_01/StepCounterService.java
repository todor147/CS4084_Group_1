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
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.cs4084_group_01.model.StepData;
import com.example.cs4084_group_01.repository.StepDataRepository;

import java.util.Date;

public class StepCounterService extends Service implements SensorEventListener {
    private static final int NOTIFICATION_ID = 1001;
    private static final String CHANNEL_ID = "step_counter_channel";
    private static final String BROADCAST_ACTION = "com.example.cs4084_group_01.STEP_COUNTER_UPDATE";
    private static final String EXTRA_STEP_COUNT = "step_count";

    private SensorManager sensorManager;
    private Sensor stepSensor;
    private PowerManager.WakeLock wakeLock;
    private int initialSteps = -1;
    private int currentSteps = 0;
    private StepDataRepository repository;

    @Override
    public void onCreate() {
        super.onCreate();
        
        repository = StepDataRepository.getInstance(this);
        
        // Initialize sensors
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        
        // Load saved steps
        currentSteps = repository.getCurrentSteps();
        
        // Create wake lock to keep service running
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "StepCounter::WakeLock");
        wakeLock.acquire();
        
        // Create notification channel for Android 8.0+
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Register step counter listener
        if (stepSensor != null) {
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        
        // Start as a foreground service with notification
        startForeground(NOTIFICATION_ID, createNotification());
        
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        // Unregister sensor listener
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
        
        super.onDestroy();
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }
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
            Intent intent = new Intent(BROADCAST_ACTION);
            intent.putExtra(EXTRA_STEP_COUNT, stepsToday);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Set the package to make it explicit for Android 13+
                intent.setPackage(getPackageName());
            }
            sendBroadcast(intent);
            
            // Update notification
            NotificationManager notificationManager = 
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(NOTIFICATION_ID, createNotification());
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not needed for this implementation
    }
    
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Step Counter Service",
                    NotificationManager.IMPORTANCE_LOW);
            channel.setDescription("Shows current step count");
            
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    
    private Notification createNotification() {
        Intent notificationIntent = new Intent(this, StepCounterActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Step Counter")
                .setContentText(currentSteps + " steps today")
                .setSmallIcon(R.drawable.ic_directions_walk)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_LOW);
                
        return builder.build();
    }
} 