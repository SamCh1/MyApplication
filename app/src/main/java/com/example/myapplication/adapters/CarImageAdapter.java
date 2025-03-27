package com.example.myapplication.adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

public class CarImageAdapter extends RecyclerView.Adapter<CarImageAdapter.ViewHolder> {

    private Context context;
    private List<String> carImages = new ArrayList<>();
    private static final String TAG = "CarImageAdapter";
    //    private int[] carImages = {
//            R.drawable.etron_gt_1,
//            R.drawable.etron_gt_1,
//            R.drawable.etron_gt_1,
//            R.drawable.etron_gt_1,
//            R.drawable.etron_gt_1
//    };

    public CarImageAdapter(Context context) {
        this.context = context;
        loadBannerImagesFromFirebase();
    }

    private void loadBannerImagesFromFirebase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Home/banner/image");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                carImages.clear();

                for(DataSnapshot snapshot : datasnapshot.getChildren()){
                    String image = snapshot.getValue(String.class);
                    if(image != null && !image.isEmpty()){
                        if(image.startsWith("\"") && image.endsWith("\"")){
                            image =  image.substring(1, image.length() - 1);
                        }
                        carImages.add(image);
                        Log.d(TAG, "Loaded image URL: " + image);
                    }
                }
                notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to load banner images: " + error.getMessage());
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.car_image_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(position < carImages.size() && context != null) {
            String image = carImages.get(position);
            Glide.with(context)
                    .load(image)
                    .centerCrop()
                    .into(holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return carImages.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.car_image);
        }
    }
}
