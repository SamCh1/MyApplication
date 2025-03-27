package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.myapplication.adapters.CarImageSliderAdapter;
import com.example.myapplication.models.Car;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CarDetailActivity extends AppCompatActivity {

    private ViewPager2 imageViewPager;
    private TabLayout indicatorTabLayout;
    private ImageView favoriteButton;
    private TextView carTitle, carPrice, carHorsepower, carSeats, carTopSpeed, carConsumption, carDescription;
    private RatingBar carRating;
    private Button bookTestDriveButton;
    private String carId;
    private Car carData;
    private DatabaseReference databaseReference;
    private CarImageSliderAdapter imageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_detail);

        // lấy dữ id car từ intent
        carId = getIntent().getStringExtra("CAR_ID");
        if (carId == null) {
            Toast.makeText(this, "Error: Không thấy dữ liệu xe", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupToolbar();
        setupImageSlider();
        loadCarDataFromFirebase();
        setupListeners();
    }

    private void initViews() {

        imageViewPager = findViewById(R.id.imageViewPager);
        indicatorTabLayout = findViewById(R.id.indicatorTabLayout);
        favoriteButton = findViewById(R.id.favoriteButton);
        carTitle = findViewById(R.id.carTitle);
        carPrice = findViewById(R.id.carPrice);
        carHorsepower = findViewById(R.id.carHorsepower);
        carSeats = findViewById(R.id.carSeats);
        carTopSpeed = findViewById(R.id.carTopSpeed);
        carConsumption = findViewById(R.id.carConsumption);
        carDescription = findViewById(R.id.carDescription);
        carRating = findViewById(R.id.carRating);
        bookTestDriveButton = findViewById(R.id.bookTestDriveButton);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.getNavigationIcon().setTint(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Xử lý sự kiện quay lại trang
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void setupImageSlider() {
        // Khởi tạo danh sách rỗng, sẽ cập nhật lại khi dữ liệu được load lên
        imageAdapter = new CarImageSliderAdapter(this, new ArrayList<>());
        imageViewPager.setAdapter(imageAdapter);
    }

    private void loadCarDataFromFirebase() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Cars").child(carId);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                carData = dataSnapshot.getValue(Car.class);
                if (carData != null) {
                    displayCarData();
                } else {
                    Toast.makeText(CarDetailActivity.this, "Car data not found", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CarDetailActivity.this, "Error loading car details: " + databaseError.getMessage(),
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void displayCarData() {
        // Set car details to views
        carTitle.setText(carData.getTitle());
        carPrice.setText("$" + carData.getPrice());
        carHorsepower.setText(carData.getHorsePower() + " HP");
        carSeats.setText("0" + carData.getSeat() + " seats");
        carTopSpeed.setText(carData.getSpeed() + " KM/H");

        // Set consumption if available
        if (carData.getConsume() != null && !carData.getConsume().isEmpty()) {
            carConsumption.setText(carData.getConsume());
        } else {
            carConsumption.setText("N/A");
        }

        // Set description if available
        if (carData.getDescription() != null && !carData.getDescription().isEmpty()) {
            carDescription.setText(carData.getDescription());
        } else {
            carDescription.setText("No description available.");
        }

        // Set rating if available (assuming rating is between 0-5)
        float rating = 0;
        try {
            rating = Float.parseFloat(carData.getRatting());
        } catch (Exception e) {
            rating = 0;
        }
        carRating.setRating(rating);

        // Update image slider
        List<String> imageUrls = new ArrayList<>();

        // Add images from the images list if available
        if (carData.getImages() != null && !carData.getImages().isEmpty()) {
            imageUrls.addAll(carData.getImages());
        }
        // Otherwise use the single image as fallback
        else if (carData.getImage() != null && !carData.getImage().isEmpty()) {
            imageUrls.add(carData.getImage());
        }

        // Update adapter with images
        imageAdapter.updateImages(imageUrls);

        // Setup indicator
        if (imageUrls.size() > 1) {
            new TabLayoutMediator(indicatorTabLayout, imageViewPager,
                    (tab, position) -> {
                        // No text for the tab
                    }
            ).attach();
            indicatorTabLayout.setVisibility(View.VISIBLE);
        } else {
            indicatorTabLayout.setVisibility(View.GONE);
        }

        // Set favorite button state
        updateFavoriteButton();
    }

    private void updateFavoriteButton() {
        if (carData.isFavorite()) {
            favoriteButton.setImageResource(R.drawable.ic_favorite_border);
        } else {
            favoriteButton.setImageResource(R.drawable.ic_favorite);
        }
    }

    private void setupListeners() {

        //Sự kiện cho nút favorite
        favoriteButton.setOnClickListener(v -> {
            carData.setFavorite(!carData.isFavorite());
            // Cập nhật lại trạng thái favorite
            databaseReference.child("favorite").setValue(carData.isFavorite());
            updateFavoriteButton();
        });

        // Sự kiện cho nút Book driver
        bookTestDriveButton.setOnClickListener(v -> {
            Toast.makeText(this, "Đặt lịch trải nghiệm cho " + carData.getTitle(), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(CarDetailActivity.this, BookActivity.class));
            finish();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Xóa các giá trị để tránh rò rỉ bộ nhớ
        if (databaseReference != null) {
            databaseReference.removeEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {}

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        }
    }
}