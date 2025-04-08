package com.example.myapplication;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private ImageView profileImage;
    private TextView nameTextView, emailTextView;
    private LinearLayout accountInfoLayout, logoutButton, cartItemAccount, OrderAccount;
    private Button returnUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_setting);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Find the top LinearLayout
        LinearLayout topLinearLayout = findViewById(R.id.main).findViewById(R.id.top_linear_layout);

        // Load the animation
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);

        // Apply the animation to the LinearLayout
        topLinearLayout.startAnimation(slideUp);

        initView();
        setUpClickListener();
        loadUserProfile();
    }

    private void initView(){
        // khởi tạo Firebase
        mAuth = FirebaseAuth.getInstance();

        // Khởi tạo view
        profileImage = findViewById(R.id.profileImage);
        cartItemAccount = findViewById(R.id.cartItemAccount);
        OrderAccount = findViewById(R.id.OrderAccount);
        nameTextView = findViewById(R.id.nameTextView);
        emailTextView = findViewById(R.id.emailTextView);
        accountInfoLayout = findViewById(R.id.accountInfoLayout);
        returnUser = findViewById(R.id.returnUser);
        logoutButton = findViewById(R.id.logoutButton);
    }

    private void setUpClickListener(){
        returnUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingActivity.this, MainActivity.class));
                finish();
            }
        });

        accountInfoLayout.setOnClickListener(v -> {
            //Chuyển trang đến Account Information
            Toast.makeText(this, "Thông tin cá nhân", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, EditProfileActivity.class));
        });

        cartItemAccount.setOnClickListener(v -> {
            Toast.makeText(this, "Giỏ hàng", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, CartActivity.class));
        });

        OrderAccount.setOnClickListener(v -> {
            Toast.makeText(this, "Giỏ hàng", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, OrderHistoryActivity.class));
        });

        // Logout Click
        logoutButton.setOnClickListener(v -> {
            mAuth.signOut();
            // Chuyển trang về Login
            startActivity(new Intent(this, SignInActivity.class));
            finishAffinity();
        });
    }

    private void loadUserProfile() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            Log.d("UserProfile", "User ID: " + currentUser.getUid());
            String userId = currentUser.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String username = dataSnapshot.child("username").getValue(String.class);
                        String email = dataSnapshot.child("email").getValue(String.class);
                        emailTextView.setText(email);
                        nameTextView.setText(username);
                        String avatar = dataSnapshot.child("avatar").getValue(String.class);
                        if(avatar != null && !avatar.isEmpty()){
                            Glide.with(SettingActivity.this)
                                    .load(avatar)
                                    .placeholder(R.drawable.default_profile) // Ảnh mặc định khi đang tải
                                    .error(R.drawable.default_profile) // Ảnh hiển thị nếu lỗi
                                    .into(profileImage);
                        }
                        Log.d("UserProfile", "Username: " + username);
                    } else {
                        Log.d("UserProfile", "User data not found.");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("UserProfile", "Database error: " + databaseError.getMessage());
                }
            });
        } else {
            Log.e("UserProfile", "currentUser is null. User not logged in.");
        }
    }
}