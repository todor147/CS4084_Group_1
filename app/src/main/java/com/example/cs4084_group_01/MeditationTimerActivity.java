package com.example.cs4084_group_01;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cs4084_group_01.model.MeditationSession;
import com.example.cs4084_group_01.repository.MeditationRepository;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.slider.Slider;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MeditationTimerActivity extends BaseActivity {

    private static final String CHANNEL_ID = "meditation_timer_channel";
    private static final int NOTIFICATION_ID = 101;

    private Slider durationSlider;
    private TextView timerText;
    private TextView durationText;
    private MaterialButton startPauseButton;
    private MaterialButton resetButton;
    private RecyclerView sessionHistoryList;
    private MeditationSessionAdapter sessionAdapter;
    private MeditationRepository meditationRepository;

    private CountDownTimer countDownTimer;
    private boolean isTimerRunning = false;
    private long remainingTimeMillis = 0;
    private long selectedDurationMillis = 5 * 60 * 1000; // Default 5 minutes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meditation_timer);

        // Initialize repository
        meditationRepository = new MeditationRepository(this);

        // Setup toolbar
        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Meditation Timer");

        // Initialize views
        durationSlider = findViewById(R.id.durationSlider);
        timerText = findViewById(R.id.timerText);
        durationText = findViewById(R.id.durationText);
        startPauseButton = findViewById(R.id.startPauseButton);
        resetButton = findViewById(R.id.resetButton);
        sessionHistoryList = findViewById(R.id.sessionHistoryList);

        // Setup slider
        durationSlider.addOnChangeListener((slider, value, fromUser) -> {
            selectedDurationMillis = (long) (value * 60 * 1000);
            updateTimerText(selectedDurationMillis);
            durationText.setText(formatDuration((long) value));
        });

        // Setup buttons
        startPauseButton.setOnClickListener(v -> toggleTimer());
        resetButton.setOnClickListener(v -> resetTimer());

        // Setup recycler view
        sessionHistoryList.setLayoutManager(new LinearLayoutManager(this));
        sessionAdapter = new MeditationSessionAdapter();
        sessionHistoryList.setAdapter(sessionAdapter);

        // Initial timer display
        updateTimerText(selectedDurationMillis);
        durationText.setText(formatDuration(selectedDurationMillis / (60 * 1000)));

        // Create notification channel
        createNotificationChannel();

        // Load session history
        updateSessionHistory();
    }

    private void toggleTimer() {
        if (isTimerRunning) {
            pauseTimer();
        } else {
            startTimer();
        }
    }

    private void startTimer() {
        if (remainingTimeMillis == 0) {
            remainingTimeMillis = selectedDurationMillis;
        }

        countDownTimer = new CountDownTimer(remainingTimeMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                remainingTimeMillis = millisUntilFinished;
                updateTimerText(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                isTimerRunning = false;
                remainingTimeMillis = 0;
                updateTimerText(0);
                startPauseButton.setText("Start");
                durationSlider.setEnabled(true);

                // Save the session
                saveMeditationSession(selectedDurationMillis);

                // Show notification
                showCompletionNotification();
            }
        }.start();

        isTimerRunning = true;
        startPauseButton.setText("Pause");
        durationSlider.setEnabled(false);
    }

    private void pauseTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        isTimerRunning = false;
        startPauseButton.setText("Resume");
    }

    private void resetTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        isTimerRunning = false;
        remainingTimeMillis = 0;
        updateTimerText(selectedDurationMillis);
        startPauseButton.setText("Start");
        durationSlider.setEnabled(true);
    }

    private void updateTimerText(long millis) {
        int minutes = (int) (millis / (60 * 1000));
        int seconds = (int) ((millis % (60 * 1000)) / 1000);
        timerText.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
    }

    private String formatDuration(long minutes) {
        if (minutes == 1) {
            return "1 minute";
        } else {
            return minutes + " minutes";
        }
    }

    private void saveMeditationSession(long durationMillis) {
        MeditationSession session = new MeditationSession(
                new Date(), durationMillis / (60 * 1000));
        meditationRepository.saveSession(session);
        updateSessionHistory();
        
        // Scroll to the top to see the new session
        sessionHistoryList.smoothScrollToPosition(0);
        Toast.makeText(this, "Meditation session saved", Toast.LENGTH_SHORT).show();
    }

    private void updateSessionHistory() {
        List<MeditationSession> sessions = meditationRepository.getSessions();
        sessionAdapter.setSessions(sessions);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Meditation Timer";
            String description = "Notifications for meditation timer completion";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void showCompletionNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_meditation)
                .setContentTitle("Meditation Complete")
                .setContentText("Your meditation session has ended.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManager notificationManager = 
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.common_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.action_health_summary) {
            Intent intent = new Intent(this, HealthDashboardActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Don't stop the timer when app goes to background
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    private class MeditationSessionAdapter extends 
            RecyclerView.Adapter<MeditationSessionAdapter.SessionViewHolder> {
        
        private List<MeditationSession> sessions = new ArrayList<>();
        private final SimpleDateFormat dateFormat = 
                new SimpleDateFormat("MMM d, yyyy HH:mm", Locale.getDefault());

        public void setSessions(List<MeditationSession> sessions) {
            this.sessions = new ArrayList<>(sessions);
            notifyDataSetChanged();
        }

        @Override
        public SessionViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(
                    R.layout.item_meditation_session, parent, false);
            return new SessionViewHolder(view);
        }

        @Override
        public void onBindViewHolder(SessionViewHolder holder, int position) {
            MeditationSession session = sessions.get(sessions.size() - 1 - position); // Show newest first
            holder.dateText.setText(dateFormat.format(session.getDate()));
            holder.durationText.setText(formatDuration(session.getDurationMinutes()));
        }

        @Override
        public int getItemCount() {
            return sessions.size();
        }

        class SessionViewHolder extends RecyclerView.ViewHolder {
            final TextView dateText;
            final TextView durationText;

            SessionViewHolder(View itemView) {
                super(itemView);
                dateText = itemView.findViewById(R.id.sessionDateText);
                durationText = itemView.findViewById(R.id.sessionDurationText);
            }
        }
    }
} 