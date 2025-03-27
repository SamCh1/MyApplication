package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.text.Editable;
import android.text.TextWatcher;
import androidx.fragment.app.FragmentTransaction;

import com.example.myapplication.adapters.CarListAdapter;
import com.example.myapplication.models.CarList;
import com.example.myapplication.fragments.BottomNavigationFragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CarListActivity extends AppCompatActivity implements CarListAdapter.OnFavoriteClickListener {

    private RecyclerView recyclerView;
    private CarListAdapter carListAdapter;
    private List<CarList> carList;
    private List<CarList> filteredList;
    private CardView searchView; // Changed to CardView to match the layout
    private EditText searchEditText;
    private ImageButton searchButton;
    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;
    private View progressBar;
    private NestedScrollView nestedScrollView;
    private BottomNavigationFragment bottomNavFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_list);

        initViews();
        addBottomNavigationFragment();

        // Khởi tạo views
        recyclerView = findViewById(R.id.recyclerView);
        searchView = findViewById(R.id.searchView); // Now correctly finds the CardView
        progressBar = findViewById(R.id.progressBar);

        carList = new ArrayList<>();
        filteredList = new ArrayList<>();

        // Khởi tạo RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        carListAdapter = new CarListAdapter(this, filteredList, this);
        recyclerView.setAdapter(carListAdapter);

        // Khởi tạo Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("Cars");

        // Khởi tạo search functionality
        setupSearch();

        // Load dữ liệu từ Firebase
        loadCarsFromFirebase();

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
        nestedScrollView = findViewById(R.id.nestedScrollView);
        recyclerView = findViewById(R.id.recyclerView);
        searchView = findViewById(R.id.searchView);
        progressBar = findViewById(R.id.progressBar);

        searchEditText = findViewById(R.id.searchEditText);
        searchButton = findViewById(R.id.searchButton);
    }

    private void addBottomNavigationFragment() {
        // thêm bottom navigation fragment
        bottomNavFragment = new BottomNavigationFragment();
        bottomNavFragment.setInitialSelectedItemId(R.id.nav_menucar);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.bottom_navigation_container, bottomNavFragment);
        transaction.commit();
    }

    private void setupSearch() {
        // Xử lý khi người dùng nhập vào ô tìm kiếm
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Không cần xử lý
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Lọc danh sách dựa trên dữ liệu đã tải về (tìm kiếm cục bộ)
                filterLocalCars(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Không cần xử lý
            }
        });

        // Xử lý khi nút tìm kiếm được nhấn (tìm kiếm trên Firebase)
        searchButton.setOnClickListener(v -> {
            String query = searchEditText.getText().toString().trim();
            if (!query.isEmpty()) {
                searchCarsFromFirebase(query);
            } else {
                // Nếu query rỗng, hiển thị tất cả xe
                loadCarsFromFirebase();
            }
        });

        // Khi nhấn vào CardView tìm kiếm, focus vào EditText
        searchView.setOnClickListener(v -> {
            searchEditText.requestFocus();
        });
    }

    private void filterLocalCars(String query) {
        filteredList.clear();
        if (query == null || query.isEmpty()) {
            filteredList.addAll(carList);
        } else {
            String lowercaseQuery = query.toLowerCase();
            for (CarList car : carList) {
                if (car.getTitle().toLowerCase().contains(lowercaseQuery)) {
                    filteredList.add(car);
                }
            }
        }
        carListAdapter.notifyDataSetChanged();
    }

    private void searchCarsFromFirebase(String query) {
        progressBar.setVisibility(View.VISIBLE);

        // Dừng lắng nghe sự kiện trước đó nếu có
        if (valueEventListener != null) {
            databaseReference.removeEventListener(valueEventListener);
        }

        // Tạo query Firebase để tìm kiếm các xe có title chứa query
        Query searchQuery = databaseReference.orderByChild("title")
                .startAt(query)
                .endAt(query + "\uf8ff"); // Kí tự đặc biệt để bao gồm tất cả các giá trị bắt đầu với query

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                carList.clear();
                filteredList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    CarList car = snapshot.getValue(CarList.class);
                    if (car != null) {
                        carList.add(car);
                        filteredList.add(car);
                    }
                }

                carListAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);

                if (filteredList.isEmpty()) {
                    Toast.makeText(CarListActivity.this, "Không tìm thấy xe nào phù hợp", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(CarListActivity.this, "Lỗi khi tìm kiếm: " + databaseError.getMessage(),
                        Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        };

        searchQuery.addValueEventListener(valueEventListener);
    }

    private void loadCarsFromFirebase() {
        progressBar.setVisibility(View.VISIBLE);

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                carList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    CarList car = snapshot.getValue(CarList.class);
                    if (car != null) {
                        carList.add(car);
                    }
                }

                // khởi tạo các item hiển thị các xe
                filteredList.clear();
                filteredList.addAll(carList);
                carListAdapter.notifyDataSetChanged();

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(CarListActivity.this, "Error loading cars: " + databaseError.getMessage(),
                        Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        };

        databaseReference.addValueEventListener(valueEventListener);
    }

    @Override
    public void onFavoriteClick(CarList car, int position) {
        // Cập nhật trạng thái favorite trên firebase
        String carId = car.getId();
        databaseReference.child(carId).child("favorite").setValue(car.isFavorite());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Xóa bỏ các dữ liệu để tránh rò rỉ bộ nhớ
        if (databaseReference != null && valueEventListener != null) {
            databaseReference.removeEventListener(valueEventListener);
        }
    }
}