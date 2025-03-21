package com.example.cs4084_group_01.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cs4084_group_01.R;
import com.example.cs4084_group_01.model.WaterIntake;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WaterIntakeHistoryAdapter extends RecyclerView.Adapter<WaterIntakeHistoryAdapter.ViewHolder> {
    private List<WaterIntake> history = new ArrayList<>();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());

    public void setHistory(List<WaterIntake> history) {
        this.history = new ArrayList<>(history);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_water_intake_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WaterIntake item = history.get(position);
        holder.dateText.setText(dateFormat.format(item.getDate()));
        holder.intakeText.setText(item.getFormattedIntake());
        holder.progressBar.setProgress(Math.round(item.getProgress()));
    }

    @Override
    public int getItemCount() {
        return history.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView dateText;
        final TextView intakeText;
        final LinearProgressIndicator progressBar;

        ViewHolder(View view) {
            super(view);
            dateText = view.findViewById(R.id.dateText);
            intakeText = view.findViewById(R.id.intakeText);
            progressBar = view.findViewById(R.id.progressBar);
        }
    }
} 