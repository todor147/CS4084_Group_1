package com.example.cs4084_group_01.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cs4084_group_01.R;
import com.example.cs4084_group_01.model.Goal;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class GoalAdapter extends RecyclerView.Adapter<GoalAdapter.GoalViewHolder> {
    private final Context context;
    private List<Goal> goals;
    private final OnGoalActionListener listener;
    private final SimpleDateFormat dateFormat;
    private final boolean isCompletedList;

    public interface OnGoalActionListener {
        void onUpdateProgressClick(Goal goal);
        void onMarkCompleteClick(Goal goal);
        void onGoalClick(Goal goal);
    }

    public GoalAdapter(Context context, List<Goal> goals, OnGoalActionListener listener, boolean isCompletedList) {
        this.context = context;
        this.goals = goals;
        this.listener = listener;
        this.dateFormat = new SimpleDateFormat("MMM d", Locale.getDefault());
        this.isCompletedList = isCompletedList;
    }

    @NonNull
    @Override
    public GoalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_goal, parent, false);
        return new GoalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GoalViewHolder holder, int position) {
        Goal goal = goals.get(position);
        
        // Set goal title and type
        holder.goalTitleText.setText(goal.getTitle());
        holder.goalTypeText.setText(goal.getType().getDisplayName());
        
        // Set progress
        holder.goalProgressBar.setProgress(goal.getProgressPercentage());
        holder.goalProgressText.setText(goal.getProgressString());
        
        // Set target date
        if (goal.getTargetDate() != null) {
            holder.targetDateText.setText("Target: " + dateFormat.format(goal.getTargetDate()));
        } else {
            holder.targetDateText.setText("No target date");
        }
        
        // Set status chip
        if (goal.isCompleted()) {
            holder.statusChip.setText("Completed");
            holder.statusChip.setChipBackgroundColorResource(R.color.green_500);
            holder.targetDateText.setText("Completed: " + 
                    (goal.getCompletedDate() != null ? dateFormat.format(goal.getCompletedDate()) : ""));
        } else if (goal.isOverdue()) {
            holder.statusChip.setText("Overdue");
            holder.statusChip.setChipBackgroundColorResource(R.color.red_500);
        } else {
            holder.statusChip.setText("Active");
            holder.statusChip.setChipBackgroundColorResource(R.color.colorPrimary);
        }
        
        // Hide/show action buttons based on completion status
        if (isCompletedList || goal.isCompleted()) {
            holder.goalActionsContainer.setVisibility(View.GONE);
        } else {
            holder.goalActionsContainer.setVisibility(View.VISIBLE);
            
            // Set button click listeners
            holder.updateProgressButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onUpdateProgressClick(goal);
                }
            });
            
            holder.markCompleteButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onMarkCompleteClick(goal);
                }
            });
        }
        
        // Set item click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onGoalClick(goal);
            }
        });
    }

    @Override
    public int getItemCount() {
        return goals != null ? goals.size() : 0;
    }

    public void updateGoals(List<Goal> goals) {
        this.goals = goals;
        notifyDataSetChanged();
    }

    static class GoalViewHolder extends RecyclerView.ViewHolder {
        TextView goalTitleText;
        TextView goalTypeText;
        Chip statusChip;
        LinearProgressIndicator goalProgressBar;
        TextView goalProgressText;
        TextView targetDateText;
        LinearLayout goalActionsContainer;
        MaterialButton updateProgressButton;
        MaterialButton markCompleteButton;

        public GoalViewHolder(@NonNull View itemView) {
            super(itemView);
            goalTitleText = itemView.findViewById(R.id.goalTitleText);
            goalTypeText = itemView.findViewById(R.id.goalTypeText);
            statusChip = itemView.findViewById(R.id.statusChip);
            goalProgressBar = itemView.findViewById(R.id.goalProgressBar);
            goalProgressText = itemView.findViewById(R.id.goalProgressText);
            targetDateText = itemView.findViewById(R.id.targetDateText);
            goalActionsContainer = itemView.findViewById(R.id.goalActionsContainer);
            updateProgressButton = itemView.findViewById(R.id.updateProgressButton);
            markCompleteButton = itemView.findViewById(R.id.markCompleteButton);
        }
    }
}