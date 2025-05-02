package com.example.cs4084_group_01;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.FileProvider;

import com.example.cs4084_group_01.util.ExportUtil;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExportDataActivity extends AppCompatActivity {
    private static final String TAG = "ExportDataActivity";
    private static final String CHANNEL_ID = "export_notification_channel";
    private static final int NOTIFICATION_ID = 1001;
    
    private CheckBox cbUserData;
    private CheckBox cbActivityData;
    private CheckBox cbNutritionData;
    private CheckBox cbWellbeingData;
    private RadioGroup rgExportAction;
    private RadioButton rbSave;
    private RadioButton rbShare;
    private Button btnExport;
    private ProgressBar progressBar;
    
    private ExecutorService executorService;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_data);
        
        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Export Health Data");
        }
        
        // Initialize views
        cbUserData = findViewById(R.id.cb_user_data);
        cbActivityData = findViewById(R.id.cb_activity_data);
        cbNutritionData = findViewById(R.id.cb_nutrition_data);
        cbWellbeingData = findViewById(R.id.cb_wellbeing_data);
        rgExportAction = findViewById(R.id.rg_export_action);
        rbSave = findViewById(R.id.rb_save);
        rbShare = findViewById(R.id.rb_share);
        btnExport = findViewById(R.id.btn_export);
        progressBar = findViewById(R.id.progress_bar);
        
        // Create notification channel
        createNotificationChannel();
        
        // Set up executor service for background tasks
        executorService = Executors.newSingleThreadExecutor();
        
        // Set up export button
        btnExport.setOnClickListener(v -> exportHealthData());
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
    
    /**
     * Creates the notification channel for export notifications (required for Android O and above)
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Export Notifications";
            String description = "Notifications for health data exports";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
    
    /**
     * Exports the health data based on user selections
     */
    private void exportHealthData() {
        // Check if at least one data type is selected
        if (!cbUserData.isChecked() && !cbActivityData.isChecked() && 
            !cbNutritionData.isChecked() && !cbWellbeingData.isChecked()) {
            Snackbar.make(btnExport, "Please select at least one data type to export", Snackbar.LENGTH_LONG).show();
            return;
        }
        
        // Show progress
        progressBar.setVisibility(View.VISIBLE);
        btnExport.setEnabled(false);
        
        // Perform export in background
        executorService.execute(() -> {
            try {
                // Create JSON data based on selections
                JSONObject jsonData = ExportUtil.createHealthDataJson(
                    this,
                    cbUserData.isChecked(),
                    cbActivityData.isChecked(),
                    cbNutritionData.isChecked(),
                    cbWellbeingData.isChecked()
                );
                
                // Save JSON to file
                Uri fileUri = ExportUtil.saveJsonToFile(this, jsonData);
                
                if (fileUri != null) {
                    // Show success notification
                    showExportSuccessNotification(fileUri);
                    
                    // Handle based on selected action
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        btnExport.setEnabled(true);
                        
                        if (rbShare.isChecked()) {
                            // Share the file
                            ExportUtil.shareExportFile(this, fileUri);
                        } else {
                            // Just show success message
                            Snackbar.make(btnExport, "Data exported successfully", Snackbar.LENGTH_LONG)
                                .setAction("View", v -> {
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setDataAndType(fileUri, "application/json");
                                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                    startActivity(Intent.createChooser(intent, "Open with"));
                                })
                                .show();
                        }
                    });
                } else {
                    // Handle error
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        btnExport.setEnabled(true);
                        Snackbar.make(btnExport, "Failed to export data", Snackbar.LENGTH_LONG).show();
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "Error exporting health data", e);
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    btnExport.setEnabled(true);
                    Snackbar.make(btnExport, "An error occurred: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
                });
            }
        });
    }
    
    /**
     * Shows a notification for successful export
     * 
     * @param fileUri URI of the exported file
     */
    private void showExportSuccessNotification(Uri fileUri) {
        try {
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
            
            // Create intent to view the file
            Intent viewIntent = new Intent(Intent.ACTION_VIEW);
            viewIntent.setDataAndType(fileUri, "application/json");
            viewIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            
            // Create the notification
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Health Data Export Complete")
                .setContentText("Your health data was exported successfully at " + timestamp)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(
                    // Create pending intent to view the file
                    android.app.PendingIntent.getActivity(
                        this,
                        0,
                        Intent.createChooser(viewIntent, "View Export"),
                        android.app.PendingIntent.FLAG_IMMUTABLE
                    )
                );
            
            // Show the notification
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        } catch (Exception e) {
            Log.e(TAG, "Error showing notification", e);
        }
    }
} 