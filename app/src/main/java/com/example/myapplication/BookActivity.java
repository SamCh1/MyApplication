package com.example.myapplication;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.annotation.NonNull;

import com.example.myapplication.models.Book;
import com.example.myapplication.models.Car;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import android.text.TextUtils;

public class BookActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private Spinner carModelSpinner;
    private EditText fullNameEditText, phoneEditText, emailEditText, dealerEditText;
    private Button bookTestDriveButton;
    private List<Car> carList = new ArrayList<>();
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        initViews();
        setSupportActionBar(toolbar);

        fetchCarModels();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        bookTestDriveButton.setOnClickListener(v -> bookTestDrive());
    }

    private void initViews() {
        // khởi tạo Firebase
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        carModelSpinner = findViewById(R.id.carModelSpinner);
        fullNameEditText = findViewById(R.id.fullNameEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        emailEditText = findViewById(R.id.emailEditText);
        dealerEditText = findViewById(R.id.dealerEditText);
        bookTestDriveButton = findViewById(R.id.bookTestDriveButton);
        toolbar = findViewById(R.id.toolbar);
    }

    private void fetchCarModels() {
        mDatabase.child("Cars").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                carList.clear();
                for (DataSnapshot carSnapshot : snapshot.getChildren()) {
                    Car car = carSnapshot.getValue(Car.class);
                    if (car != null) {
                        car.setId(carSnapshot.getKey());
                        carList.add(car);
                    }
                }

                List<String> carTitles = carList.stream()
                        .map(Car::getTitle)
                        .collect(Collectors.toList());

                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        BookActivity.this,
                        android.R.layout.simple_spinner_item,
                        carTitles
                );
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                carModelSpinner.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(BookActivity.this,
                        "Failed to load car models: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bookTestDrive() {
        // Validate Input Fields
        if (validateInputs()) {
            String selectedCarTitle = carModelSpinner.getSelectedItem().toString();
            Car selectedCar = carList.stream()
                    .filter(car -> car.getTitle().equals(selectedCarTitle))
                    .findFirst()
                    .orElse(null);

            if (selectedCar != null && mAuth.getCurrentUser() != null) {
                // Create booking object
                Book booking = new Book(
                        mAuth.getCurrentUser().getUid(),
                        selectedCar.getId(),
                        selectedCarTitle,
                        fullNameEditText.getText().toString(),
                        phoneEditText.getText().toString(),
                        emailEditText.getText().toString(),
                        dealerEditText.getText().toString(),
                        new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date())
                );

                // Generate unique key for the booking
                String bookingId = mDatabase.child("Users")
                        .child(mAuth.getCurrentUser().getUid())
                        .child("TestDriveBookings")
                        .push().getKey();

                // Save Booking to Realtime Database
                if (bookingId != null) {
                    mDatabase.child("Users")
                            .child(mAuth.getCurrentUser().getUid())
                            .child("TestDriveBookings")
                            .child(bookingId)
                            .setValue(booking)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "Test Drive Booked Successfully!",
                                        Toast.LENGTH_SHORT).show();
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Booking Failed: " + e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            });
                }
            }
        }
    }

    private boolean validateInputs() {
        boolean isValid = true;

        if (TextUtils.isEmpty(fullNameEditText.getText())) {
            fullNameEditText.setError("Full Name is required");
            isValid = false;
        }

        if (TextUtils.isEmpty(phoneEditText.getText())) {
            phoneEditText.setError("Phone Number is required");
            isValid = false;
        }

        return isValid;
    }
}