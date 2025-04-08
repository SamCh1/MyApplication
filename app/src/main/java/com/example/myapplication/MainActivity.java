package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.FragmentTransaction;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import androidx.core.widget.NestedScrollView;

import com.example.myapplication.adapters.CarImageAdapter;
import com.example.myapplication.adapters.FeatureAdapter;
import com.example.myapplication.models.FeaturedCar;
import com.example.myapplication.fragments.BottomNavigationFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 carImageViewPager;
    private ViewPager2 featureViewPager;
    private TabLayout carImageIndicator;
    private TabLayout featureIndicator;
    private Button bookTestBtn;
    private Button configureBtn;
    private Button buildYourBtn;
    private Button contactBtn;
    private BottomNavigationFragment bottomNavFragment;
    private NestedScrollView nestedScrollView;
    private FeatureAdapter featureAdapter;
    private TextView bannerTitle, bannerSubtitle, powerText, accelerationText, batteryText;
    private Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupViewPagers();
        setupButtons();
        addBottomNavigationFragment();
        displayCarSpecs();

        // Scroll bottom hidden
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                // kiểm tra scrolled đã đến bottom chưa
                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                    bottomNavFragment.setNavigationVisibility(false); // Hide khi scrolling đến bottom
                } else if (scrollY < oldScrollY) {
                    bottomNavFragment.setNavigationVisibility(true); // Show khi scrolling up
                }
            }
        });
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
        nestedScrollView = findViewById(R.id.nestedScrollView);
        bannerTitle = findViewById(R.id.banner_title);
        bannerSubtitle = findViewById(R.id.banner_subTitle);
        powerText = findViewById(R.id.power_value);
        batteryText = findViewById(R.id.battery_value);
        accelerationText = findViewById(R.id.acceleration_value);
    }

    // Bottom
    private void addBottomNavigationFragment() {
        // thêm bottom navigation fragment
        bottomNavFragment = new BottomNavigationFragment();
        bottomNavFragment.setInitialSelectedItemId(R.id.nav_home);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.bottom_navigation_container, bottomNavFragment);
        transaction.commit();
    }

    // Lấy dữ liệu từ Banner
    private void getTitleFromFireBase(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Home/banner");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String title = snapshot.child("title").getValue(String.class);
                    String subTitle = snapshot.child("subtitle").getValue(String.class);
                    String battery = snapshot.child("battery").getValue(String.class);
                    String power = snapshot.child("power").getValue(String.class);
                    String acceleration = snapshot.child("acceleration").getValue(String.class);
                    if (title != null && subTitle != null) {
                        Log.d("FirebaseData", "Title: " + title);
                        Log.d("FirebaseData", "Subtitle: " + subTitle);
                        bannerSubtitle.setText(subTitle);
                        bannerTitle.setText(title);
                        powerText.setText(power);
                        batteryText.setText(battery);
                        accelerationText.setText(acceleration);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Lỗi: " + error.getMessage());
            }
        });
    }

    // Lấy dữ liệu ảnh từ FeaturedCars
    private void fetchFeaturedCars() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Home/featuredCars");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<FeaturedCar> featuredCarsList = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    FeaturedCar car = snapshot.getValue(FeaturedCar.class);
                    if (car != null) {
                        featuredCarsList.add(car);
                        Log.d("FirebaseData", "Retrieved car: " + car.getTitle() + ", description: " + car.getDescription());
                    }
                }

                // Cập nhật ViewPager với dữ liệu từ Firebase
                featureAdapter.updateData(featuredCarsList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
                Log.e("FirebaseError", "Database error: " + databaseError.getMessage());
            }
        });
    }

    private void displayCarSpecs(){
        getTitleFromFireBase();
    }


    private void setupViewPagers() {
        // khởi tạo car images viewpager
        CarImageAdapter carImageAdapter = new CarImageAdapter(this);
        carImageViewPager.setAdapter(carImageAdapter);

        // kết nối TabLayout với ViewPager2
        new TabLayoutMediator(carImageIndicator, carImageViewPager,
                (tab, position) -> {}).attach();

        // khởi tạo feature viewpager với danh sách rỗng được khởi tạo
        List<FeaturedCar> featuredCars = new ArrayList<>();
        featureAdapter = new FeatureAdapter(this, featuredCars);
        featureViewPager.setAdapter(featureAdapter);

        // kết nối TabLayout với ViewPager2
        new TabLayoutMediator(featureIndicator, featureViewPager,
                (tab, position) -> {}).attach();

        // Fetch data từ Firebase
        fetchFeaturedCars();

        final Runnable update = new Runnable() {
            public void run() {
                int currentPage = carImageViewPager.getCurrentItem();
                int totalItems = carImageAdapter.getItemCount();
                if (totalItems > 0) {
                    if (currentPage == totalItems - 1) {
                        carImageViewPager.setCurrentItem(0);
                    } else {
                        carImageViewPager.setCurrentItem(currentPage + 1);
                    }
                }
            }
        };

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 3000);
                update.run();
            }
        }, 3000);

        carImageViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                handler.removeCallbacks(update); // Dừng tự động chuyển
                handler.postDelayed(update, 3000); // Bắt đầu lại sau 3 giây
            }
        });
    }

    private void setupButtons() {
        bookTestBtn.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "Đặt lịch thử", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, BookActivity.class));
            finish();
        });

        configureBtn.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "Configure your car", Toast.LENGTH_SHORT).show();
            // Chưa xây dựng
        });

        buildYourBtn.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "Build your own car", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, ProductListActivity.class));
        });

        contactBtn.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "Contact us", Toast.LENGTH_SHORT).show();
            // Chưa xây dựng
        });
    }

}