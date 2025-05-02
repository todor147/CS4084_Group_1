package com.example.cs4084_group_01.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.cs4084_group_01.R;
import com.example.cs4084_group_01.SleepTrackingActivity;
import com.example.cs4084_group_01.adapter.SleepEntryAdapter;
import com.example.cs4084_group_01.model.SleepEntry;
import com.example.cs4084_group_01.viewmodel.SleepViewModel;

import java.util.Locale;
import java.util.Map;

public class SleepHistoryFragment extends Fragment implements SleepEntryAdapter.OnSleepEntryClickListener {
    private static final String TAG = "SleepHistoryFragment";

    private SleepViewModel viewModel;
    private SleepEntryAdapter adapter;
    private RecyclerView recyclerView;
    private TextView emptySleepHistoryText;
    private TextView avgDurationValue;
    private TextView avgQualityValue;
    private TextView totalTimeValue;
    private TextView entriesCountValue;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView called");
        return inflater.inflate(R.layout.fragment_sleep_history, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated called");
        
        // Initialize ViewModel
        viewModel = new ViewModelProvider(requireActivity()).get(SleepViewModel.class);
        
        // Initialize UI elements
        recyclerView = view.findViewById(R.id.sleepEntriesRecyclerView);
        emptySleepHistoryText = view.findViewById(R.id.emptySleepHistoryText);
        avgDurationValue = view.findViewById(R.id.avgDurationValue);
        avgQualityValue = view.findViewById(R.id.avgQualityValue);
        totalTimeValue = view.findViewById(R.id.totalTimeValue);
        entriesCountValue = view.findViewById(R.id.entriesCountValue);
        
        // Set up RecyclerView
        setupRecyclerView();
        
        // Observe sleep entries LiveData
        observeSleepEntries();
        
        // Observe weekly statistics
        observeWeeklyStatistics();
        
        Log.d(TAG, "Fragment setup complete");
    }
    
    private void setupRecyclerView() {
        adapter = new SleepEntryAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
    }
    
    private void observeSleepEntries() {
        viewModel.getAllSleepEntries().observe(getViewLifecycleOwner(), sleepEntries -> {
            Log.d(TAG, "Received " + sleepEntries.size() + " sleep entries");
            adapter.submitList(sleepEntries);
            
            // Show empty state if no entries
            if (sleepEntries.isEmpty()) {
                emptySleepHistoryText.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                emptySleepHistoryText.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        });
    }
    
    private void observeWeeklyStatistics() {
        viewModel.getWeeklyStatistics().observe(getViewLifecycleOwner(), this::updateStatisticsUI);
    }
    
    private void updateStatisticsUI(Map<String, Double> statistics) {
        double avgDuration = statistics.getOrDefault("averageDuration", 0.0);
        double avgQuality = statistics.getOrDefault("averageQuality", 0.0);
        double totalTime = statistics.getOrDefault("totalSleepTime", 0.0);
        double entriesCount = statistics.getOrDefault("entriesCount", 0.0);
        
        // Format and set values
        avgDurationValue.setText(String.format(Locale.getDefault(), "%.1f hours", avgDuration / 60));
        avgQualityValue.setText(String.format(Locale.getDefault(), "%.1f/5", avgQuality));
        totalTimeValue.setText(String.format(Locale.getDefault(), "%.1f hours", totalTime / 60));
        entriesCountValue.setText(String.format(Locale.getDefault(), "%d", (int)entriesCount));
    }
    
    @Override
    public void onEditClick(SleepEntry entry) {
        Log.d(TAG, "Edit clicked for entry: " + entry.getId());
        ViewPager2 viewPager = requireActivity().findViewById(R.id.viewPager);
        if (viewPager != null) {
            // Switch to add sleep tab
            viewPager.setCurrentItem(0);
            
            // Try to find the AddSleepFragment through the activity
            if (requireActivity() instanceof SleepTrackingActivity) {
                SleepTrackingActivity activity = (SleepTrackingActivity) requireActivity();
                activity.editSleepEntry(entry);
            }
        }
    }
    
    @Override
    public void onDeleteClick(SleepEntry entry) {
        Log.d(TAG, "Delete clicked for entry: " + entry.getId());
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Sleep Entry")
                .setMessage("Are you sure you want to delete this sleep entry?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    viewModel.deleteSleepEntry(entry.getId());
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
} 