package com.example.myapplication;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public class MyAppActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int PERMISSION_REQUEST_CODE = 101;
    private ImageView imageView;
    private File photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_app);

        imageView = findViewById(R.id.imageView);
        Button btnCapture = findViewById(R.id.btnCapture);
        Button btnUpload = findViewById(R.id.btnUpload);

        btnCapture.setOnClickListener(v -> checkPermissionAndCapture());
        btnUpload.setOnClickListener(v -> uploadImageToRoboflow());
    }

    // Kiểm tra và yêu cầu quyền
    private void checkPermissionAndCapture() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        } else {
            captureImage();
        }
    }

    // Chụp ảnh bằng camera
    private void captureImage() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
        }
    }

    // Xử lý kết quả chụp ảnh
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(photo);

            // Lưu ảnh vào file
            photoFile = saveBitmapToFile(photo);
        }
    }

    // Lưu ảnh thành file
    private File saveBitmapToFile(Bitmap bitmap) {
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "photo.jpg");
        try (FileOutputStream out = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    // Gửi ảnh lên Roboflow API
    private void uploadImageToRoboflow() {
        if (photoFile == null || !photoFile.exists()) {
            Toast.makeText(this, "Chưa có ảnh để gửi!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Thiết lập Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.roboflow.com/") // URL cơ bản của Roboflow
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RoboflowApi api = retrofit.create(RoboflowApi.class);

        // Chuẩn bị file ảnh để gửi
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), photoFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", photoFile.getName(), requestFile);

        // Thay YOUR_API_KEY bằng API Key từ Roboflow
        String apiKey = "YOUR_API_KEY";
        Call<String> call = api.uploadImage(body, apiKey);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MyAppActivity.this, "Gửi ảnh thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MyAppActivity.this, "Lỗi: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(MyAppActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Xử lý kết quả yêu cầu quyền
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            captureImage();
        } else {
            Toast.makeText(this, "Quyền bị từ chối!", Toast.LENGTH_SHORT).show();
        }
    }

    // Interface cho Roboflow API
    interface RoboflowApi {
        @Multipart
        @POST("dataset/YOUR_DATASET_ID/upload") // Thay YOUR_DATASET_ID bằng ID dataset của bạn
        Call<String> uploadImage(@Part MultipartBody.Part file, @Part("api_key") String apiKey);
    }
}