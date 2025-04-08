package com.example.myapplication;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.adapters.ProductAdapter;
import com.example.myapplication.adapters.CategoryAdapter;
import com.example.myapplication.models.CartItem;
import com.example.myapplication.models.Product;
import com.example.myapplication.models.Category;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProductListActivity extends AppCompatActivity {

    private RecyclerView productRecyclerView;
    private EditText searchBar;
    private ProductAdapter productAdapter;
    private List<Product> productList = new ArrayList<>();
    private List<Product> allProducts = new ArrayList<>();

    private RecyclerView categoryRecyclerView;
    private CategoryAdapter categoryAdapter;
    private List<Category> categoryList = new ArrayList<>();

    private String selectedCategory = "All";
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        productRecyclerView = findViewById(R.id.productRecyclerView);
        searchBar = findViewById(R.id.search_bar);

        productAdapter = new ProductAdapter(this, productList, product -> {
            addToCart(product); // Thêm vào giỏ
            Toast.makeText(this, product.getName() + " đã thêm vào giỏ", Toast.LENGTH_SHORT).show();
            // TODO: Thêm vào giỏ hàng trong Firebase
        });

        productRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        productRecyclerView.setAdapter(productAdapter);

        dbRef = FirebaseDatabase.getInstance().getReference("Products");
        loadProductsFromFirebase();

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts(s.toString().trim(), selectedCategory);
            }
        });

        categoryRecyclerView = findViewById(R.id.categoryRecyclerView);
        categoryList = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(this, categoryList, this::onCategoryClick);

        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        categoryRecyclerView.setAdapter(categoryAdapter);

        loadCategories();
    }

    //------------------------------------------------------San Pham
    private void loadProductsFromFirebase() {
        dbRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                allProducts.clear();
                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    Product product = snapshot.getValue(Product.class);
                    if (product != null) {
                        product.setId(snapshot.getKey());
                        allProducts.add(product);
                    }
                }
                filterProducts("", selectedCategory); // Hiển thị ban đầu
            } else {
                Toast.makeText(this, "Lỗi tải sản phẩm", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterProducts(String keyword, String category) {
        List<Product> filteredList = new ArrayList<>();
        for (Product product : allProducts) {
            boolean matchKeyword = product.getName().toLowerCase().contains(keyword.toLowerCase());
            boolean matchCategory = category.equals("All") || product.getCategory().equalsIgnoreCase(category);
            if (matchKeyword && matchCategory) {
                filteredList.add(product);
            }
        }
        productAdapter.updateList(filteredList);
    }


    //------------------------------------------------------SDanh muc
    public void onCategoryClick(String categoryName) {
        selectedCategory = categoryName;
        filterProducts(searchBar.getText().toString(), categoryName);
    }

    private void loadCategories() {
        DatabaseReference catRef = FirebaseDatabase.getInstance().getReference("Categories");
        catRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryList.clear();
                categoryList.add(new Category("All", "")); // Cho phép chọn tất cả

                for (DataSnapshot child : snapshot.getChildren()) {
                    Category category = child.getValue(Category.class);
                    Log.d("FirebaseCategory", "Loaded category: " + category.getName());
                    categoryList.add(category);
                }
                categoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProductListActivity.this, "Lỗi tải danh mục", Toast.LENGTH_SHORT).show();
            }
        });
    }


    //------------------------------------------------------SGio hang
    private void addToCart(Product product) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        DatabaseReference cartRef = FirebaseDatabase.getInstance()
                .getReference("Cart")
                .child(userId)
                .child(product.getId());

        cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Long currentQty = snapshot.child("quantity").getValue(Long.class);
                    if (currentQty == null) currentQty = 0L;

                    cartRef.child("quantity").setValue(currentQty + 1);
                } else {
                    CartItem newItem = new CartItem(
                            product.getId(),
                            product.getName(),
                            product.getImageUrl(),
                            product.getPrice(),
                            1
                    );
                    cartRef.setValue(newItem);
                    Log.d("AddToCart", "Added new product to cart");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProductListActivity.this, "Lỗi khi thêm vào giỏ: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
