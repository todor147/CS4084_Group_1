package com.example.cs4084_group_01.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cs4084_group_01.R;
import com.example.cs4084_group_01.model.Feature;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class FeaturesAdapter extends RecyclerView.Adapter<FeaturesAdapter.FeatureViewHolder> {
    private final List<Feature> features;
    private final Context context;

    public FeaturesAdapter(List<Feature> features, Context context) {
        this.features = features;
        this.context = context;
    }

    @NonNull
    @Override
    public FeatureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_feature, parent, false);
        return new FeatureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeatureViewHolder holder, int position) {
        Feature feature = features.get(position);
        holder.bind(feature, context);
    }

    @Override
    public int getItemCount() {
        return features.size();
    }

    static class FeatureViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView cardView;
        private final ImageView iconView;
        private final TextView titleView;

        public FeatureViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (MaterialCardView) itemView;
            iconView = itemView.findViewById(R.id.featureIcon);
            titleView = itemView.findViewById(R.id.featureTitle);
        }

        public void bind(Feature feature, Context context) {
            iconView.setImageResource(feature.getIconResId());
            titleView.setText(feature.getTitle());
            cardView.setOnClickListener(v -> {
                Intent intent = new Intent(context, feature.getActivityClass());
                context.startActivity(intent);
            });
        }
    }
} 