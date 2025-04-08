package com.example.myapplication;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.adapters.OrderItemAdapter;
import com.example.myapplication.models.CartItem;
import com.example.myapplication.models.Order;
import com.example.myapplication.models.Product;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderDetailActivity extends AppCompatActivity {

    private TextView orderIdText, orderDateText, orderTotalText;
    private RecyclerView itemsRecyclerView;
    private OrderItemAdapter itemAdapter;
    private List<CartItem> orderItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        // Thiết lập Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false); // Tắt tiêu đề mặc định
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        initViews();

        // Nhận dữ liệu từ Intent
        Order order = (Order) getIntent().getSerializableExtra("order");

        if (order != null) {
            // Hiển thị thông tin đơn hàng
            orderIdText.setText("Mã đơn: " + order.getOrderId());
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            orderDateText.setText("Ngày đặt: " + dateFormat.format(new Date(order.getTimestamp())));
            orderTotalText.setText("Tổng tiền: " + String.format("%,d VNĐ", order.getTotalAmount()));

            // Hiển thị danh sách sản phẩm trong đơn hàng
            orderItems = order.getItems() != null ? order.getItems() : new ArrayList<>();
            itemAdapter = new OrderItemAdapter(this, orderItems);
            itemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            itemsRecyclerView.setAdapter(itemAdapter);
        }
    }

    private void initViews(){
        orderIdText = findViewById(R.id.order_detail_id);
        orderDateText = findViewById(R.id.order_detail_date);
        orderTotalText = findViewById(R.id.order_detail_total);
        itemsRecyclerView = findViewById(R.id.order_items_recycler_view);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}