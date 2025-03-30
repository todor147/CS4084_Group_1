package com.example.cs4084_group_01;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cs4084_group_01.model.MeditationSession;
import com.example.cs4084_group_01.repository.MeditationRepository;
import com.google.android.material.textfield.TextInputEditText;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MeditationActivity extends AppCompatActivity {
    private static final String CHANNEL_ID = "meditation_channel";
    private static final int NOTIFICATION_ID = 1;

    private MeditationRepository meditationRepository;
    private TextView timerText;
    private TextInputEditText durationInput;
    private Button startPauseButton;
    private Button resetButton;
    private RecyclerView sessionHistoryList;
    private SessionAdapter sessionAdapter;
    private CountDownTimer countDownTimer;
    private boolean isTimerRunning = false;
    private long timeLeftInMillis;
    private MeditationSession currentSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meditation);

        meditationRepository = new MeditationRepository(this);
        createNotificationChannel();

        // Setup toolbar
        setSupportActionBar(findViewById(R.id.topAppBar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Initialize views
        timerText = findViewById(R.id.timerText);
        durationInput = findViewById(R.id.durationInput);
        startPauseButton = findViewById(R.id.startPauseButton);
        resetButton = findViewById(R.id.resetButton);
        sessionHistoryList = findViewById(R.id.sessionHistoryList);

        // Setup RecyclerView
        sessionHistoryList.setLayoutManager(new LinearLayoutManager(this));
        sessionAdapter = new SessionAdapter();
        sessionHistoryList.setAdapter(sessionAdapter);

        // Load session history
        updateSessionHistory();

        // Setup button listeners
        startPauseButton.setOnClickListener(v -> toggleTimer());
        resetButton.setOnClickListener(v -> resetTimer());
    }

    private void toggleTimer() {
        if (isTimerRunning) {
            pauseTimer();
        } else {
            startTimer();
        }
    }

    private void startTimer() {
        if (!isTimerRunning) {
            if (timeLeftInMillis == 0) {
                String durationStr = durationInput.getText().toString();
                if (durationStr.isEmpty()) {
                    Toast.makeText(this, "Please enter duration", Toast.LENGTH_SHORT).show();
                    return;
                }

                int minutes = Integer.parseInt(durationStr);
                timeLeftInMillis = minutes * 60 * 1000;
                currentSession = new MeditationSession(minutes);
            }

            countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    timeLeftInMillis = millisUntilFinished;
                    updateTimerText();
                }

                @Override
                public void onFinish() {
                    completeSession();
                }
            }.start();

            isTimerRunning = true;
            startPauseButton.setText("Pause");
            durationInput.setEnabled(false);
        }
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
        timeLeftInMillis = 0;
        isTimerRunning = false;
        updateTimerText();
        startPauseButton.setText("Start");
        durationInput.setEnabled(true);
        durationInput.setText("");
        currentSession = null;
    }

    private void completeSession() {
        isTimerRunning = false;
        timeLeftInMillis = 0;
        updateTimerText();
        startPauseButton.setText("Start");
        durationInput.setEnabled(true);

        if (currentSession != null) {
            currentSession.setCompleted(true);
            meditationRepository.saveSession(currentSession);
            updateSessionHistory();
            showCompletionNotification();
        }

        currentSession = null;
    }

    private void updateTimerText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        timerText.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
    }

    private void updateSessionHistory() {
        List<MeditationSession> sessions = meditationRepository.getSessions();
        sessionAdapter.setSessions(sessions);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Meditation Timer",
                NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Meditation session notifications");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void showCompletionNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Meditation Complete")
            .setContentText("Great job! You've completed your meditation session.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true);

        NotificationManager notificationManager = 
            (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private static class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.SessionViewHolder> {
        private List<MeditationSession> sessions = new ArrayList<>();
        private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy HH:mm", Locale.getDefault());

        public void setSessions(List<MeditationSession> sessions) {
            this.sessions = new ArrayList<>(sessions);
            notifyDataSetChanged();
        }

        @Override
        public SessionViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            android.view.View view = android.view.LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_meditation_session, parent, false);
            return new SessionViewHolder(view);
        }

        @Override
        public void onBindViewHolder(SessionViewHolder holder, int position) {
            MeditationSession session = sessions.get(sessions.size() - 1 - position); // Show newest first
            holder.dateText.setText(dateFormat.format(session.getDate()));
            holder.durationText.setText(session.getDurationMinutes() + " minutes");
            holder.completedIcon.setVisibility(session.isCompleted() ? 
                android.view.View.VISIBLE : android.view.View.GONE);
        }

        @Override
        public int getItemCount() {
            return sessions.size();
        }

        static class SessionViewHolder extends RecyclerView.ViewHolder {
            final TextView dateText;
            final TextView durationText;
            final android.widget.ImageView completedIcon;

            SessionViewHolder(android.view.View itemView) {
                super(itemView);
                dateText = itemView.findViewById(R.id.dateText);
                durationText = itemView.findViewById(R.id.durationText);
                completedIcon = itemView.findViewById(R.id.completedIcon);
            }
        }
    }
} 