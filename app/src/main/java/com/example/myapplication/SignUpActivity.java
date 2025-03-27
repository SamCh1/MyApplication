package com.example.myapplication;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    private EditText etEmail, etUsername,etPassword,etConfirmPassword;
    private Button btnRegister;
    private TextView tvSignIn;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //khởi tạo auth firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //khởi tạo các view và button
        etEmail = findViewById(R.id.etEmail);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvSignIn = findViewById(R.id.tvSignIn);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });

        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                finish();
            }
        });
    }

    private void registerUser(){
        final String email = etEmail.getText().toString().trim();
        final String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // validate dữ liệu
        if(TextUtils.isEmpty(email)) {
            etEmail.setError("Vui lòng nhập email!");
            return;
        }

        if(TextUtils.isEmpty(username)) {
            etUsername.setError("Vui lòng nhập họ tên!");
            return;
        }

        if(TextUtils.isEmpty(password)){
            etPassword.setError("Vui lòng nhập mật khẩu!");
            return;
        }

        if(password.length() < 8){
            etPassword.setError("Yêu cầu mật khẩu dài 8 ký tự!");
            return;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Xác nhận mật khẩu không khớp với mật khẩu");
            return;
        }

        // Tạo tài khoản user mới trên firebase
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            String userId = mAuth.getCurrentUser().getUid(); //Lưu thông tin vào database

                            //Tạo object user mới
                            HashMap<String, Object> userMap = new HashMap<>();
                            userMap.put("userId", userId);
                            userMap.put("username", username);
                            userMap.put("email", email);

                            // Lưu vào database
                            mDatabase.child("Users").child(userId).setValue(userMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(SignUpActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                                                finish();
                                            } else {
                                                Toast.makeText(SignUpActivity.this, "Đăng ký thất bại, Không thể lưu vào database!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }else{
                            Toast.makeText(SignUpActivity.this,"Đăng ký thất bại, vui lòng thử lại sau!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}