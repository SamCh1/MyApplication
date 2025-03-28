package com.example.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
    private static final String TAG = "MyAppActivity";
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int PERMISSION_REQUEST_CODE = 101;

    // Roboflow configuration
    private static final String ROBOFLOW_BASE_URL = "https://api.roboflow.com/";
    private static final String ROBOFLOW_API_KEY = "kTLMAfCSw1zE9PQvxBBt"; // Replace with your actual key
    private static final String ROBOFLOW_DATASET = "audi_detection"; // Your dataset name

    private ImageView imageView;
    private File photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_app);

        // Initialize UI components
        imageView = findViewById(R.id.imageView);
        Button btnCapture = findViewById(R.id.btnCapture);
        Button btnUpload = findViewById(R.id.btnUpload);

        // Set click listeners
        btnCapture.setOnClickListener(v -> checkPermissionAndCapture());
        btnUpload.setOnClickListener(v -> uploadImageToRoboflow());
    }

    // Check and request camera and storage permissions
    private void checkPermissionAndCapture() {
        String[] permissions = {
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        boolean allPermissionsGranted = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                allPermissionsGranted = false;
                break;
            }
        }

        if (allPermissionsGranted) {
            captureImage();
        } else {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
        }
    }

    // Launch camera intent to capture image
    private void captureImage() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
        } else {
            Toast.makeText(this, "Không tìm thấy ứng dụng Camera", Toast.LENGTH_SHORT).show();
        }
    }

    // Handle image capture result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null && data.getExtras() != null) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                if (photo != null) {
                    imageView.setImageBitmap(photo);
                    photoFile = saveBitmapToFile(photo);
                }
            }
        }
    }

    // Save captured bitmap to file
    private File saveBitmapToFile(Bitmap bitmap) {
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "captured_image.jpg");
        try (FileOutputStream out = new FileOutputStream(file)) {
            // Compress bitmap to reduce file s
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
            out.flush();
            Log.d(TAG, "Image saved: " + file.getAbsolutePath());
        } catch (IOException e) {
            Log.e(TAG, "Error saving image", e);
            Toast.makeText(this, "Lỗi lưu ảnh", Toast.LENGTH_SHORT).show();
        }
        return file;
    }

    // Upload image to Roboflow
    private void uploadImageToRoboflow() {
        // Check if image exists
        if (photoFile == null || !photoFile.exists()) {
            Toast.makeText(this, "Chưa có ảnh để tải lên!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check network connectivity
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "Không có kết nối internet!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create Retrofit instance
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ROBOFLOW_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Create API service
        RoboflowApi roboflowApi = retrofit.create(RoboflowApi.class);

        // Prepare file for upload
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), photoFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", photoFile.getName(), requestFile);
        RequestBody apiKey = RequestBody.create(MediaType.parse("text/plain"), ROBOFLOW_API_KEY);

        // Make API call
        roboflowApi.uploadImage(body, apiKey).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Upload successful: " + response.body());
                    Toast.makeText(MyAppActivity.this, "Tải lên thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "Upload failed: " + response.message());
                    Toast.makeText(MyAppActivity.this, "Lỗi tải lên: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Log.e(TAG, "Network error", t);
                Toast.makeText(MyAppActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Check network availability
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    // Handle permission request results
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allPermissionsGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }

            if (allPermissionsGranted) {
                captureImage();
            } else {
                Toast.makeText(this, "Quyền bị từ chối!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Roboflow API interface
    interface RoboflowApi {
        @Multipart
        @POST("dataset/" + ROBOFLOW_DATASET + "/upload")
        Call<String> uploadImage(
                @Part MultipartBody.Part file,
                @Part("api_key") RequestBody apiKey
        );
    }
}