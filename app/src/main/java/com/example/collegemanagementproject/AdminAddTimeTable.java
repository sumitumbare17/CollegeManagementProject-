package com.example.collegemanagementproject;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AdminAddTimeTable extends AppCompatActivity {

    private Spinner spinnerDepartment, spinnerYear;
    private Button btnPickImage, btnUpload;
    private ImageView imgPreview;
    private Uri imageUri; // URI of selected image
    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    private static final int PICK_IMAGE_REQUEST = 1; // Request code for image selection

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_time_table);

        // Initialize UI elements
        spinnerDepartment = findViewById(R.id.spinnerDepartment);
        spinnerYear = findViewById(R.id.spinnerYear);
        btnPickImage = findViewById(R.id.btnPickImage);
        btnUpload = findViewById(R.id.btnUpload);
        imgPreview = findViewById(R.id.imgPreview);

        // Initialize Firebase references
        storageReference = FirebaseStorage.getInstance().getReference("timetables");
        databaseReference = FirebaseDatabase.getInstance().getReference("timetables");

        // Populate Spinners
        setupSpinners();

        // Button to pick image
        btnPickImage.setOnClickListener(v -> openFileChooser());

        // Button to upload image
        btnUpload.setOnClickListener(v -> uploadImage());
    }

    private void setupSpinners() {
        // Departments
        String[] departments = {"Select Department", "MECH", "CSE", "CIVIL", "ECE"};
        ArrayAdapter<String> deptAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, departments);
        deptAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDepartment.setAdapter(deptAdapter);

        // Years
        String[] years = {"Select Class", "FY", "SY", "TY", "B.Tech"};
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(yearAdapter);
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imgPreview.setVisibility(View.VISIBLE);
            imgPreview.setImageURI(imageUri);
        }
    }

    private void uploadImage() {
        if (imageUri == null) {
            Toast.makeText(this, "Please select an image!", Toast.LENGTH_SHORT).show();
            return;
        }

        String department = spinnerDepartment.getSelectedItem().toString();
        String year = spinnerYear.getSelectedItem().toString();
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "img_" + timestamp + ".jpg";

        // Create proper directory structure in Firebase Storage
        StorageReference fileReference = storageReference.child(department + "/" + year + "/" + fileName);

        fileReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();

                    // Storing the image URL under the appropriate department and class
                    DatabaseReference deptRef = databaseReference.child(department).child(year);
                    deptRef.child("imageUrl").setValue(imageUrl);

                    Toast.makeText(this, "Upload successful!", Toast.LENGTH_SHORT).show();
                }))
                .addOnFailureListener(e -> Toast.makeText(this, "Upload failed!", Toast.LENGTH_SHORT).show());
    }

}
