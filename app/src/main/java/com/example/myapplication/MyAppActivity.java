package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class MyAppActivity extends AppCompatActivity {
    private static final int REQUEST_STORAGE_PERMISSION = 1;
    private static final int PICK_IMAGE_REQUEST = 2;

    private TextView responseTextView;
    private Button btnSelectImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_app);

        responseTextView = findViewById(R.id.responseTextView);
        btnSelectImage = findViewById(R.id.btnSelectImage); // Khởi tạo nút

        // Kiểm tra quyền truy cập bộ nhớ
        checkPermissions();

        btnSelectImage.setOnClickListener(view -> openGallery());
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ cần quyền mới
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                        REQUEST_STORAGE_PERMISSION);
            }
        } else {
            // Android 12 trở xuống
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_STORAGE_PERMISSION);
            }
        }
    }

    // Mở thư viện ảnh
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                new UploadImageTask().execute(imageUri);
            }
        }
    }

    private class UploadImageTask extends AsyncTask<Uri, Void, String> {
        @Override
        protected String doInBackground(Uri... uris) {
            HttpURLConnection connection = null;
            DataOutputStream outputStream = null;
            InputStream inputStream = null;

            try {
                Uri imageUri = uris[0];
                inputStream = getContentResolver().openInputStream(imageUri);
                if (inputStream == null) return "Lỗi: Không thể đọc ảnh";

                String API_KEY = "kTLMAfCSw1zE9PQvxBBt";
                String MODEL_ENDPOINT = "audi_detection/1";
                String uploadURL = "https://detect.roboflow.com/" + MODEL_ENDPOINT + "?api_key=" + API_KEY;

                // **Tạo boundary ngẫu nhiên**
                String boundary = "----WebKitFormBoundary" + System.currentTimeMillis();
                URL url = new URL(uploadURL);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
                connection.setDoOutput(true);
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);

                outputStream = new DataOutputStream(connection.getOutputStream());

                // **Thêm dữ liệu ảnh vào request**
                outputStream.writeBytes("--" + boundary + "\r\n");
                outputStream.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"image.jpg\"\r\n");
                outputStream.writeBytes("Content-Type: image/jpeg\r\n\r\n");

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.writeBytes("\r\n");

                // **Kết thúc multipart request**
                outputStream.writeBytes("--" + boundary + "--\r\n");
                outputStream.flush();
                outputStream.close();

                // **Lấy phản hồi từ server**
                int responseCode = connection.getResponseCode();
                Log.d("HTTP Response", "Code: " + responseCode);

                InputStream responseStream = new BufferedInputStream(connection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(responseStream));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                return response.toString();

            } catch (Exception e) {
                return "Lỗi: " + e.getMessage();
            } finally {
                try {
                    if (inputStream != null) inputStream.close();
                    if (connection != null) connection.disconnect();
                } catch (IOException ignored) {
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonResponse = new JSONObject(result);
                JSONArray predictions = jsonResponse.getJSONArray("predictions");

                if (predictions.length() == 0) {
                    responseTextView.setText("Không tìm thấy đối tượng nào!");
                } else {
                    StringBuilder classes = new StringBuilder("Phát hiện: ");
                    for (int i = 0; i < predictions.length(); i++) {
                        JSONObject obj = predictions.getJSONObject(i);
                        String detectedClass = obj.getString("class");
                        Intent intent = new Intent(MyAppActivity.this, CarDetailActivity.class);
                        intent.putExtra("slug", detectedClass);
                        startActivity(intent);
                        finish();
                    }
                    responseTextView.setText(classes.toString());
                }
            } catch (JSONException e) {
                responseTextView.setText("Lỗi xử lý JSON: " + e.getMessage());
            }
        }
    }


    // Xử lý kết quả yêu cầu quyền
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                responseTextView.setText("Cần cấp quyền để chọn ảnh!");
            }
        }
    }
}