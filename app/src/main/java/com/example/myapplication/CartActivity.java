package com.example.myapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.adapters.CartAdapter;
import com.example.myapplication.models.CartItem;
import com.example.myapplication.models.Order;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {
    private RecyclerView cartRecyclerView;
    private TextView totalPriceText;
    private Button btnCheckout;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItems = new ArrayList<>();
    private DatabaseReference cartRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cartRecyclerView = findViewById(R.id.cartRecyclerView);
        totalPriceText = findViewById(R.id.total_price);
        btnCheckout = findViewById(R.id.btn_checkout);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
            return;
        }
        String userId = currentUser.getUid();
        cartRef = FirebaseDatabase.getInstance().getReference("Cart").child(userId);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        cartAdapter = new CartAdapter(this, cartItems, new CartAdapter.OnCartChangeListener() {
            @Override
            public void onQuantityChanged() {
                updateTotal();
                saveCartToFirebase();
            }

            @Override
            public void onItemRemoved(String productId) {
                cartRef.child(productId).removeValue();
                updateTotal();
            }
        });

        cartRecyclerView.setAdapter(cartAdapter);
        loadCartItems();

        btnCheckout.setOnClickListener(v -> {
            if (cartItems.isEmpty()) {
                Toast.makeText(this, "Giỏ hàng trống!", Toast.LENGTH_SHORT).show();
                return;
            }

            int total = 0;
            for (CartItem item : cartItems) {
                total += item.getPrice() * item.getQuantity();
            }

            String orderId = cartRef.push().getKey(); // Tạo ID hóa đơn
            long timestamp = System.currentTimeMillis();

            Order order = new Order(orderId, FirebaseAuth.getInstance().getCurrentUser().getUid(), new ArrayList<>(cartItems), total, timestamp);

            DatabaseReference orderRef = FirebaseDatabase.getInstance()
                    .getReference("Orders")
                    .child(order.getUserId())
                    .child(orderId);

            orderRef.setValue(order).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Tạo hóa đơn thành công!", Toast.LENGTH_SHORT).show();
                    cartRef.removeValue(); // Xóa giỏ hàng
                    cartItems.clear();
                    cartAdapter.notifyDataSetChanged();
                    updateTotal();
                } else {
                    Toast.makeText(this, "Lỗi khi tạo hóa đơn", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void loadCartItems() {
        cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                cartItems.clear();
                for (DataSnapshot itemSnap : snapshot.getChildren()) {
                    CartItem item = itemSnap.getValue(CartItem.class);
                    cartItems.add(item);
                }
                cartAdapter.notifyDataSetChanged();
                updateTotal();
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void updateTotal() {
        int total = 0;
        for (CartItem item : cartItems) {
            total += item.getPrice() * item.getQuantity();
        }
        totalPriceText.setText("Tổng: " + total + " đ");
    }

    private void saveCartToFirebase() {
        for (CartItem item : cartItems) {
            cartRef.child(item.getProductId()).setValue(item);
        }
    }
}