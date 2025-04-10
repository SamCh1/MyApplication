package com.example.myapplication;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.models.Order;
import com.example.myapplication.adapters.OrderAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.text.SimpleDateFormat;
import java.util.*;

public class OrderHistoryActivity extends AppCompatActivity {

    private RecyclerView orderRecyclerView;
    private OrderAdapter orderAdapter;
    private List<Order> allOrders = new ArrayList<>();
    private DatabaseReference orderRef;
    private Button btnFilterDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        // Thiết lập Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true); // Tắt tiêu đề mặc định
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        orderRecyclerView = findViewById(R.id.orderRecyclerView);
        btnFilterDate = findViewById(R.id.btn_filter_date);
        orderRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderAdapter = new OrderAdapter(allOrders);
        orderRecyclerView.setAdapter(orderAdapter);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        orderRef = FirebaseDatabase.getInstance().getReference("Orders").child(userId);

        loadAllOrders();

        btnFilterDate.setOnClickListener(v -> showDatePicker());
    }

    private void loadAllOrders() {
        orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                allOrders.clear();
                for (DataSnapshot orderSnap : snapshot.getChildren()) {
                    Order order = orderSnap.getValue(Order.class);
                    allOrders.add(order);
                }
                orderAdapter.notifyDataSetChanged();
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this,R.style.MyDatePickerDialog, (view, year, month, dayOfMonth) -> {
            Calendar selectedCal = Calendar.getInstance();
            selectedCal.set(year, month, dayOfMonth, 0, 0, 0);
            long startTimestamp = selectedCal.getTimeInMillis();

            selectedCal.set(Calendar.HOUR_OF_DAY, 23);
            selectedCal.set(Calendar.MINUTE, 59);
            selectedCal.set(Calendar.SECOND, 59);
            long endTimestamp = selectedCal.getTimeInMillis();

            filterOrdersByDate(startTimestamp, endTimestamp);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void filterOrdersByDate(long start, long end) {
        List<Order> filtered = new ArrayList<>();
        for (Order order : allOrders) {
            if (order.getTimestamp() >= start && order.getTimestamp() <= end) {
                filtered.add(order);
            }
        }
        orderAdapter.updateList(filtered);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
