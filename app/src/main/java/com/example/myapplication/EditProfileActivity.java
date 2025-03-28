package com.example.myapplication;

import android.os.Bundle;
import android.net.Uri;
import android.content.Intent;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.database.ValueEventListener;
import com.bumptech.glide.Glide;

import com.example.myapplication.models.User;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
public class EditProfileActivity extends AppCompatActivity {

    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private User users;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;

    private ImageView profileImage;
    private EditText nameEditText, emailEditText, phoneEditText, addressEditText;
    private ImageButton btnBack,btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        initViews();
        loadUserInfo();
        setUpClickListener();
    }

    private void initViews(){
        btnBack = findViewById(R.id.btnBack);
        btnSave = findViewById(R.id.btnSave);
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        addressEditText = findViewById(R.id.addressEditText);
        profileImage = findViewById(R.id.profileImage);
    }

    private void setUpClickListener(){

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserInfo();
                uploadAvatar();
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Glide.with(this).load(imageUri).into(profileImage);
        }
    }

    private void loadUserInfo(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        // console log
        if (currentUser != null) {
            Log.d("FirebaseUser", "User ID: " + currentUser.getUid());
            Log.d("FirebaseUser", "Email: " + currentUser.getEmail());
            Log.d("FirebaseUser", "Display Name: " + currentUser.getDisplayName());
        } else {
            Log.d("FirebaseUser", "Không có user đăng nhập!");
        }
        //
        if(currentUser == null){
            Toast.makeText(EditProfileActivity.this, "Vui lòng đăng nhập!", Toast.LENGTH_SHORT).show();
            return;
        }
        String userId = currentUser.getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Log.d("FirebaseData", "Dữ liệu trả về từ TestDriveBookings:");
                    users = snapshot.getValue(User.class);
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        Log.d("FirebaseData", "Key: " + childSnapshot.getKey() + ", Value: " + childSnapshot.getValue());
                    }
                    if(users != null){
                        nameEditText.setText(users.getUsername() != null ? users.getUsername() : "");
                        emailEditText.setText(users.getEmail() != null ? users.getEmail() : "");
                        phoneEditText.setText(users.getPhone() != null ? users.getPhone() : "");
                        addressEditText.setText(users.getAddress() != null ? users.getAddress() : "");
                        String userAvatar = users.getAvatar();
                        if (userAvatar != null && !userAvatar.isEmpty()) {
                            Glide.with(EditProfileActivity.this)
                                    .load(userAvatar)
                                    .placeholder(R.drawable.default_profile)
                                    .error(R.drawable.default_profile)
                                    .into(profileImage);
                        }
                    }   
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditProfileActivity.this, "Lỗi tải dữ liệu: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUserInfo(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(EditProfileActivity.this, "Vui lòng đăng nhập!", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

        String newUsername = nameEditText.getText().toString().trim();
        String newEmail = emailEditText.getText().toString().trim();
        String newPhone = phoneEditText.getText().toString().trim();
        String newAddress = addressEditText.getText().toString().trim();

        //validate
        if (newUsername.isEmpty() || newEmail.isEmpty() || newPhone.isEmpty() || newAddress.isEmpty()) {
            Toast.makeText(EditProfileActivity.this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        if(newPhone.length() < 10 || newPhone.length() > 11){
            Toast.makeText(EditProfileActivity.this, "Số điện thoại phải từ 10 - 11 số", Toast.LENGTH_SHORT).show();
            return;
        }


        // Tạo HashMap để cập nhật dữ liệu
        HashMap<String, Object> userUpdates = new HashMap<>();
        userUpdates.put("username", newUsername);
        userUpdates.put("email", newEmail);
        userUpdates.put("phone", newPhone);
        userUpdates.put("address", newAddress);

        databaseReference.updateChildren(userUpdates)
                .addOnCompleteListener(unused -> {
                    Toast.makeText(EditProfileActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(EditProfileActivity.this, "Lỗi khi cập nhật: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void uploadAvatar() {
        if (imageUri != null) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference("avatars/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + ".jpg");

            storageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            updateAvatarUrl(uri.toString());
                        });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(EditProfileActivity.this, "Lỗi khi upload ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(EditProfileActivity.this, "Vui lòng chọn ảnh!", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateAvatarUrl(String imageUrl) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;

        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
        databaseRef.child("avatar").setValue(imageUrl)
                .addOnSuccessListener(unused -> Toast.makeText(EditProfileActivity.this, "Ảnh đại diện cập nhật thành công!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(EditProfileActivity.this, "Lỗi khi cập nhật ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}