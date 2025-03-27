package com.example.myapplication;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.example.myapplication.adapters.CarImageAdapter;
import com.example.myapplication.adapters.FeatureAdapter;
import com.example.myapplication.models.FeaturedCar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class BaseActivity extends AppCompatActivity {

    private ViewPager2 carImageViewPager;
    private ViewPager2 featureViewPager;
    private TabLayout carImageIndicator;
    private TabLayout featureIndicator;
    private Button bookTestBtn;
    private Button configureBtn;
    private Button buildYourBtn;
    private Button contactBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupViewPagers();
        setupButtons();
        displayCarSpecs();
    }

    private void initViews() {
        carImageViewPager = findViewById(R.id.car_image_viewpager);
        featureViewPager = findViewById(R.id.feature_viewpager);
        carImageIndicator = findViewById(R.id.car_image_indicator);
        featureIndicator = findViewById(R.id.feature_indicator);
        bookTestBtn = findViewById(R.id.book_test_btn);
        configureBtn = findViewById(R.id.configure_btn);
        buildYourBtn = findViewById(R.id.build_your_btn);
        contactBtn = findViewById(R.id.contact_btn);
    }

    private void setupViewPagers() {
        // Setup car images viewpager
        CarImageAdapter carImageAdapter = new CarImageAdapter(this);
        carImageViewPager.setAdapter(carImageAdapter);

        // Connect TabLayout with ViewPager2
        new TabLayoutMediator(carImageIndicator, carImageViewPager,
                (tab, position) -> {}).attach();

        // Setup feature viewpager
//        FeatureAdapter featureAdapter = new FeatureAdapter(this);
//        featureViewPager.setAdapter(featureAdapter);

        // Thêm danh sách rỗng tạm thời
        List<FeaturedCar> featuredCars = new ArrayList<>();

        FeatureAdapter featureAdapter = new FeatureAdapter(this, featuredCars);
        featureViewPager.setAdapter(featureAdapter);

        // Connect feature TabLayout with ViewPager2
        new TabLayoutMediator(featureIndicator, featureViewPager,
                (tab, position) -> {}).attach();
    }

    private void setupButtons() {
        bookTestBtn.setOnClickListener(v -> {
            // Implementation for booking a test
        });

        configureBtn.setOnClickListener(v -> {
            // Implementation for configuration
        });

        buildYourBtn.setOnClickListener(v -> {
            // Implementation for building your own
        });

        contactBtn.setOnClickListener(v -> {
            // Implementation for contact
        });
    }

    private void displayCarSpecs() {
        TextView powerText = findViewById(R.id.power_value);
        TextView batteryText = findViewById(R.id.battery_value);
        TextView accelerationText = findViewById(R.id.acceleration_value);

        powerText.setText("637 HP");
        batteryText.setText("93 kWh");
        accelerationText.setText("3.1 sec");
    }
}