package com.example.myapplication.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.CarDetailActivity;
import com.example.myapplication.R;
import com.example.myapplication.models.CarList;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class CarListAdapter extends RecyclerView.Adapter<CarListAdapter.CarViewHolder> {
    private Context context;
    private List<CarList> carList;
    private OnFavoriteClickListener favoriteClickListener;

    public interface OnFavoriteClickListener {
        void onFavoriteClick(CarList car, int position);
    }

    public CarListAdapter(Context context, List<CarList> carList, OnFavoriteClickListener listener) {
        this.context = context;
        this.carList = carList;
        this.favoriteClickListener = listener;
    }

    @NonNull
    @Override
    public CarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new layout file called car_item.xml with the content from paste-3.txt
        View view = LayoutInflater.from(context).inflate(R.layout.item_car, parent, false);
        return new CarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarViewHolder holder, int position) {
        CarList car = carList.get(position);

        holder.carName.setText(car.getTitle());
        holder.topSpeed.setText(car.getSpeed() + "mph");
        holder.seats.setText("0" + car.getSeat() + " Seats");
        holder.horsepower.setText(car.getHorsePower() + " HP");

        // Load image using Glide
        Glide.with(context)
                .load(car.getImage())
                .centerCrop()
                .into(holder.carImage);

        // Set favorite icon - you need to create two different drawable resources
        if (car.isFavorite()) {
            holder.favoriteBtn.setImageResource(R.drawable.ic_favorite_border);
        } else {
            holder.favoriteBtn.setImageResource(R.drawable.ic_favorite);
        }

        // Set click listener for favorite button
        holder.favoriteBtn.setOnClickListener(v -> {
            car.setFavorite(!car.isFavorite());

            // Update the favorite icon
            if (car.isFavorite()) {
                holder.favoriteBtn.setImageResource(R.drawable.ic_favorite);
            } else {
                holder.favoriteBtn.setImageResource(R.drawable.ic_favorite_border);
            }

            if (favoriteClickListener != null) {
                favoriteClickListener.onFavoriteClick(car, position);
            }
        });

        // Them click listener den card view
        holder.cardView.setOnClickListener(v -> {
            Intent intent = new Intent(context, CarDetailActivity.class);
            // Pass the car ID to fetch detailed data from Firebase in the detail activity
            intent.putExtra("CAR_ID", car.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return carList.size();
    }

    // Cap nhat lai danh sach
    public void updateList(List<CarList> newList) {
        this.carList = newList;
        notifyDataSetChanged();
    }

    public static class CarViewHolder extends RecyclerView.ViewHolder {
        ImageView carImage;
        TextView carName, topSpeed, seats, horsepower;
        FloatingActionButton favoriteBtn;
        CardView cardView;

        public CarViewHolder(@NonNull View itemView) {
            super(itemView);
            carImage = itemView.findViewById(R.id.car_image);
            carName = itemView.findViewById(R.id.car_title);
            topSpeed = itemView.findViewById(R.id.car_speed);
            seats = itemView.findViewById(R.id.car_seat);
            horsepower = itemView.findViewById(R.id.car_horsepower);
            favoriteBtn = itemView.findViewById(R.id.car_favorite);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}