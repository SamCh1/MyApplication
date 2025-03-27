package com.example.myapplication.adapters;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.models.FeaturedCar;

import com.bumptech.glide.Glide;
import java.util.List;

public class FeatureAdapter extends RecyclerView.Adapter<FeatureAdapter.ViewHolder> {

    private Context context;
    private List<FeaturedCar> featuredCars;

    public FeatureAdapter(Context context, List<FeaturedCar> featuredCars){
        this.context = context;
        this.featuredCars = featuredCars;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.feature_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        holder.featureImage.setImageResource(featureImages[position]);
//        holder.featureTitle.setText(featureTitles[position]);
//        holder.featureDescription.setText(featureDescriptions[position]);
//    }
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FeaturedCar car = featuredCars.get(position);
        holder.featureTitle.setText(car.getTitle());
        holder.featureDescription.setText(car.getDescription());
        Glide.with(context).load(car.getImage()).into(holder.featureImage);
    }

    @Override
    public int getItemCount() {
        return featuredCars.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView featureImage;
        TextView featureTitle;
        TextView featureDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            featureImage = itemView.findViewById(R.id.feature_image);
            featureTitle = itemView.findViewById(R.id.feature_title);
            featureDescription = itemView.findViewById(R.id.feature_description);
        }
    }

    public void updateData(List<FeaturedCar> newFeaturedCars) {
        this.featuredCars.clear();
        this.featuredCars.addAll(newFeaturedCars);
        notifyDataSetChanged();
    }
}