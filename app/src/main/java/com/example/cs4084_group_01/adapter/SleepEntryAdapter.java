package com.example.cs4084_group_01.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cs4084_group_01.R;
import com.example.cs4084_group_01.model.SleepEntry;

public class SleepEntryAdapter extends ListAdapter<SleepEntry, SleepEntryAdapter.SleepEntryViewHolder> {

    private final OnSleepEntryClickListener listener;

    public interface OnSleepEntryClickListener {
        void onEditClick(SleepEntry entry);
        void onDeleteClick(SleepEntry entry);
    }

    public SleepEntryAdapter(OnSleepEntryClickListener listener) {
        super(new SleepEntryDiffCallback());
        this.listener = listener;
    }

    @NonNull
    @Override
    public SleepEntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sleep_entry, parent, false);
        return new SleepEntryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SleepEntryViewHolder holder, int position) {
        SleepEntry entry = getItem(position);
        holder.bind(entry, listener);
    }

    static class SleepEntryViewHolder extends RecyclerView.ViewHolder {
        private final TextView dateText;
        private final TextView timeRangeText;
        private final TextView durationText;
        private final TextView qualityValueText;
        private final TextView notesText;
        private final ImageView sleepQualityIcon;
        private final Button editButton;
        private final Button deleteButton;

        public SleepEntryViewHolder(@NonNull View itemView) {
            super(itemView);
            dateText = itemView.findViewById(R.id.dateText);
            timeRangeText = itemView.findViewById(R.id.timeRangeText);
            durationText = itemView.findViewById(R.id.durationText);
            qualityValueText = itemView.findViewById(R.id.qualityValueText);
            notesText = itemView.findViewById(R.id.notesText);
            sleepQualityIcon = itemView.findViewById(R.id.sleepQualityIcon);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }

        public void bind(final SleepEntry entry, final OnSleepEntryClickListener listener) {
            dateText.setText(entry.getFormattedDate());
            timeRangeText.setText(entry.getFormattedStartTime() + " - " + entry.getFormattedEndTime());
            durationText.setText(entry.getFormattedDuration());
            qualityValueText.setText(entry.getQuality() + "/5");

            if (entry.getNotes() != null && !entry.getNotes().isEmpty()) {
                notesText.setText(entry.getNotes());
                notesText.setVisibility(View.VISIBLE);
            } else {
                notesText.setVisibility(View.GONE);
            }

            editButton.setOnClickListener(v -> listener.onEditClick(entry));
            deleteButton.setOnClickListener(v -> listener.onDeleteClick(entry));
        }
    }

    static class SleepEntryDiffCallback extends DiffUtil.ItemCallback<SleepEntry> {
        @Override
        public boolean areItemsTheSame(@NonNull SleepEntry oldItem, @NonNull SleepEntry newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull SleepEntry oldItem, @NonNull SleepEntry newItem) {
            return oldItem.getStartTime().equals(newItem.getStartTime()) &&
                    oldItem.getEndTime().equals(newItem.getEndTime()) &&
                    oldItem.getQuality() == newItem.getQuality() &&
                    (oldItem.getNotes() == null ? newItem.getNotes() == null : 
                            oldItem.getNotes().equals(newItem.getNotes()));
        }
    }
} 