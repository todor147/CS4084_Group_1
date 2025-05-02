package com.example.cs4084_group_01;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cs4084_group_01.adapter.GoalAdapter;
import com.example.cs4084_group_01.model.Goal;
import com.example.cs4084_group_01.model.GoalType;
import com.example.cs4084_group_01.viewmodel.GoalViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class GoalsListFragment extends Fragment implements GoalAdapter.OnGoalActionListener {
    private static final String ARG_SHOW_COMPLETED = "show_completed";
    
    private GoalViewModel viewModel;
    private RecyclerView goalsRecyclerView;
    private LinearLayout emptyStateContainer;
    private TextView emptyStateText;
    private TextView emptyStateSubText;
    private GoalAdapter adapter;
    private boolean showCompleted;
    private SimpleDateFormat dateFormat;

    public static GoalsListFragment newInstance(boolean showCompleted) {
        GoalsListFragment fragment = new GoalsListFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_SHOW_COMPLETED, showCompleted);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            showCompleted = getArguments().getBoolean(ARG_SHOW_COMPLETED);
        }
        
        viewModel = new ViewModelProvider(requireActivity()).get(GoalViewModel.class);
        dateFormat = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_goals_list, container, false);
        
        goalsRecyclerView = view.findViewById(R.id.goalsRecyclerView);
        emptyStateContainer = view.findViewById(R.id.emptyStateContainer);
        emptyStateText = view.findViewById(R.id.emptyStateText);
        emptyStateSubText = view.findViewById(R.id.emptyStateSubText);
        
        // Set up RecyclerView
        goalsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new GoalAdapter(requireContext(), new ArrayList<>(), this, showCompleted);
        goalsRecyclerView.setAdapter(adapter);
        
        // Update empty state text based on the list type
        if (showCompleted) {
            emptyStateText.setText("No completed goals");
            emptyStateSubText.setText("Complete goals to see them here");
        } else {
            emptyStateText.setText("No active goals");
            emptyStateSubText.setText("Tap + to create a new health goal");
        }
        
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Observe the appropriate goal list
        if (showCompleted) {
            viewModel.getCompletedGoals().observe(getViewLifecycleOwner(), this::updateGoalsList);
        } else {
            viewModel.getActiveGoals().observe(getViewLifecycleOwner(), this::updateGoalsList);
        }
    }

    private void updateGoalsList(List<Goal> goals) {
        if (goals != null && !goals.isEmpty()) {
            adapter.updateGoals(goals);
            goalsRecyclerView.setVisibility(View.VISIBLE);
            emptyStateContainer.setVisibility(View.GONE);
        } else {
            goalsRecyclerView.setVisibility(View.GONE);
            emptyStateContainer.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onUpdateProgressClick(Goal goal) {
        // Show dialog to update goal progress
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_update_progress, null);
        
        TextView goalTitleText = dialogView.findViewById(R.id.goalTitleText);
        TextView currentProgressText = dialogView.findViewById(R.id.currentProgressText);
        RadioButton setValueRadio = dialogView.findViewById(R.id.setValueRadio);
        RadioButton addValueRadio = dialogView.findViewById(R.id.addValueRadio);
        TextInputEditText progressValueInput = dialogView.findViewById(R.id.progressValueInput);
        TextView unitText = dialogView.findViewById(R.id.unitText);
        MaterialButton cancelButton = dialogView.findViewById(R.id.cancelButton);
        MaterialButton updateButton = dialogView.findViewById(R.id.updateButton);
        
        // Set initial values
        goalTitleText.setText(goal.getTitle());
        currentProgressText.setText("Current progress: " + goal.getProgressString());
        unitText.setText(goal.getType().getUnit());
        
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create();
        
        cancelButton.setOnClickListener(v -> dialog.dismiss());
        
        updateButton.setOnClickListener(v -> {
            String valueStr = progressValueInput.getText().toString().trim();
            if (valueStr.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter a value", Toast.LENGTH_SHORT).show();
                return;
            }
            
            try {
                double value = Double.parseDouble(valueStr);
                if (addValueRadio.isChecked()) {
                    viewModel.incrementGoalProgress(goal.getId(), value);
                } else {
                    viewModel.updateGoalProgress(goal.getId(), value);
                }
                dialog.dismiss();
            } catch (NumberFormatException e) {
                Toast.makeText(requireContext(), "Please enter a valid number", Toast.LENGTH_SHORT).show();
            }
        });
        
        dialog.show();
    }

    @Override
    public void onMarkCompleteClick(Goal goal) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Mark Goal as Complete")
                .setMessage("Are you sure you want to mark this goal as complete?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    viewModel.markGoalCompleted(goal.getId());
                    Toast.makeText(requireContext(), "Goal marked as complete", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public void onGoalClick(Goal goal) {
        // Show dialog with goal details
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(goal.getTitle());
        
        // Build message with goal details
        StringBuilder message = new StringBuilder();
        message.append("Type: ").append(goal.getType().getDisplayName()).append("\n\n");
        message.append("Progress: ").append(goal.getProgressString()).append("\n\n");
        
        if (goal.getTargetDate() != null) {
            message.append("Target Date: ").append(dateFormat.format(goal.getTargetDate())).append("\n\n");
        }
        
        if (goal.isCompleted() && goal.getCompletedDate() != null) {
            message.append("Completed: ").append(dateFormat.format(goal.getCompletedDate())).append("\n\n");
        }
        
        if (goal.getNotes() != null && !goal.getNotes().isEmpty()) {
            message.append("Notes: ").append(goal.getNotes());
        }
        
        builder.setMessage(message.toString());
        builder.setPositiveButton("Close", null);
        
        // Add delete button
        builder.setNegativeButton("Delete", (dialog, which) -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Delete Goal")
                    .setMessage("Are you sure you want to delete this goal?")
                    .setPositiveButton("Yes", (innerDialog, innerWhich) -> {
                        viewModel.deleteGoal(goal.getId());
                        Toast.makeText(requireContext(), "Goal deleted", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
        
        builder.show();
    }
} 