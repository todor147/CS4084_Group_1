package com.example.cs4084_group_01.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cs4084_group_01.R;
import com.example.cs4084_group_01.model.MealEntry;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Adapter for displaying meal history in a RecyclerView.
 */
public class MealHistoryAdapter extends RecyclerView.Adapter<MealHistoryAdapter.MealViewHolder> {
    private List<MealEntry> mealEntries;
    private final Context context;
    private final SimpleDateFormat timeFormat;
    private OnMealClickListener clickListener;

    public interface OnMealClickListener {
        void onMealClick(MealEntry mealEntry);
    }

    public MealHistoryAdapter(Context context, List<MealEntry> mealEntries) {
        this.context = context;
        this.mealEntries = mealEntries;
        this.timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    }

    public void setOnMealClickListener(OnMealClickListener listener) {
        this.clickListener = listener;
    }

    public void updateData(List<MealEntry> newMealEntries) {
        this.mealEntries = newMealEntries;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_meal_entry, parent, false);
        return new MealViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MealViewHolder holder, int position) {
        MealEntry mealEntry = mealEntries.get(position);
        holder.bind(mealEntry);
    }

    @Override
    public int getItemCount() {
        return mealEntries.size();
    }

    class MealViewHolder extends RecyclerView.ViewHolder {
        private final TextView mealTypeText;
        private final TextView timeText;
        private final TextView descriptionText;
        private final TextView caloriesText;
        private final TextView notesText;
        private final ChipGroup foodGroupsChipGroup;

        public MealViewHolder(@NonNull View itemView) {
            super(itemView);
            mealTypeText = itemView.findViewById(R.id.mealTypeText);
            timeText = itemView.findViewById(R.id.timeText);
            descriptionText = itemView.findViewById(R.id.descriptionText);
            caloriesText = itemView.findViewById(R.id.caloriesText);
            notesText = itemView.findViewById(R.id.notesText);
            foodGroupsChipGroup = itemView.findViewById(R.id.foodGroupsChipGroup);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && clickListener != null) {
                    clickListener.onMealClick(mealEntries.get(position));
                }
            });
        }

        public void bind(MealEntry mealEntry) {
            mealTypeText.setText(mealEntry.getMealType().getDisplayName());
            timeText.setText(timeFormat.format(mealEntry.getTimestamp()));
            descriptionText.setText(mealEntry.getDescription());
            caloriesText.setText(String.format(Locale.getDefault(), "%d calories", mealEntry.getCalories()));
            
            if (mealEntry.getNotes() != null && !mealEntry.getNotes().isEmpty()) {
                notesText.setText(mealEntry.getNotes());
                notesText.setVisibility(View.VISIBLE);
            } else {
                notesText.setVisibility(View.GONE);
            }
            
            // Clear previous chips
            foodGroupsChipGroup.removeAllViews();
            
            // Add food group chips
            for (int i = 0; i < mealEntry.getFoodGroups().size(); i++) {
                Chip chip = new Chip(context);
                chip.setText(mealEntry.getFoodGroups().get(i).getDisplayName());
                chip.setClickable(false);
                chip.setCheckable(false);
                foodGroupsChipGroup.addView(chip);
            }
            
            if (mealEntry.getFoodGroups().isEmpty()) {
                foodGroupsChipGroup.setVisibility(View.GONE);
            } else {
                foodGroupsChipGroup.setVisibility(View.VISIBLE);
            }
        }
    }
} 