package com.example.cs4084_group_01.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cs4084_group_01.R;
import com.example.cs4084_group_01.model.StepData;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class StepHistoryAdapter extends RecyclerView.Adapter<StepHistoryAdapter.ViewHolder> {
    private List<StepData> stepDataList;
    private SimpleDateFormat dateFormat;

    public StepHistoryAdapter(List<StepData> stepDataList) {
        this.stepDataList = stepDataList;
        this.dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_step_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StepData stepData = stepDataList.get(position);
        holder.dateText.setText(dateFormat.format(stepData.getDate()));
        holder.stepsText.setText(String.valueOf(stepData.getSteps()) + " steps");
        holder.goalText.setText("Goal: " + stepData.getGoal() + " steps");
        
        // Calculate completion percentage
        int percentage = (int) (((float) stepData.getSteps() / stepData.getGoal()) * 100);
        holder.completionText.setText(percentage + "% of goal");
    }

    @Override
    public int getItemCount() {
        return stepDataList.size();
    }

    public void updateData(List<StepData> newData) {
        this.stepDataList = newData;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView dateText;
        TextView stepsText;
        TextView goalText;
        TextView completionText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dateText = itemView.findViewById(R.id.dateText);
            stepsText = itemView.findViewById(R.id.stepsText);
            goalText = itemView.findViewById(R.id.goalText);
            completionText = itemView.findViewById(R.id.completionText);
        }
    }
} 