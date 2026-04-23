package com.prescription.app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.prescription.app.model.PrescriptionResponse;
import com.prescription.app.network.RetrofitClient;
import com.prescription.app.utils.FileUtils;

import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ImageView imagePreview;
    private Button btnSelectImage, btnUpload;
    private ProgressBar progressBar;
    private TextView tvResult;
    private Uri selectedImageUri;

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    imagePreview.setImageURI(selectedImageUri);
                    btnUpload.setEnabled(true);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imagePreview = findViewById(R.id.imagePreview);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnUpload = findViewById(R.id.btnUpload);
        progressBar = findViewById(R.id.progressBar);
        tvResult = findViewById(R.id.tvResult);

        btnSelectImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        });

        btnUpload.setOnClickListener(v -> {
            if (selectedImageUri != null) {
                uploadImage();
            }
        });
    }

    private void uploadImage() {
        progressBar.setVisibility(View.VISIBLE);
        btnUpload.setEnabled(false);
        tvResult.setText("");

        try {
            byte[] imageBytes = FileUtils.getBytesFromUri(this, selectedImageUri);
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), imageBytes);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", "prescription.jpg", requestFile);

            RetrofitClient.getApi().uploadPrescription(body).enqueue(new Callback<PrescriptionResponse>() {
                @Override
                public void onResponse(Call<PrescriptionResponse> call, Response<PrescriptionResponse> response) {
                    progressBar.setVisibility(View.GONE);
                    btnUpload.setEnabled(true);

                    if (response.isSuccessful() && response.body() != null) {
                        tvResult.setText(response.body().getRecommendations());
                    } else {
                        try {
                            String errorBody = response.errorBody() != null ? response.errorBody().string() : "{}";
                            JSONObject jsonObject = new JSONObject(errorBody);
                            String errorMessage = jsonObject.optString("error", "Failed to get recommendations");
                            Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                            tvResult.setText("Error: " + errorMessage);
                        } catch (Exception e) {
                            tvResult.setText("Upload failed: " + response.code());
                        }
                    }
                }

                @Override
                public void onFailure(Call<PrescriptionResponse> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    btnUpload.setEnabled(true);
                    Toast.makeText(MainActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    tvResult.setText("Network error. Please try again.");
                }
            });

        } catch (Exception e) {
            progressBar.setVisibility(View.GONE);
            btnUpload.setEnabled(true);
            Toast.makeText(this, "Failed to read image", Toast.LENGTH_SHORT).show();
        }
    }
}
