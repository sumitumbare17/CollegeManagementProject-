package com.example.collegemanagementproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class FacultyProfileActivity extends AppCompatActivity {
    private EditText editTextFacultyName, editTextEducation, editTextPhone, editTextEmail, editTextAddress;
    private ToggleButton toggleFreeze;
    private Button btnSave;

    private DatabaseReference databaseReference;
    private String facultyId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_profile);

        // Get facultyId from Intent
        Intent intent = getIntent();
        facultyId = intent.getStringExtra("facultyId");

        if (facultyId == null) {
            Toast.makeText(this, "Invalid Faculty ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize Firebase reference
        databaseReference = FirebaseDatabase.getInstance().getReference("faculties").child(facultyId);

        // Initialize Views
        editTextFacultyName = findViewById(R.id.editTextFacultyName);
        editTextEducation = findViewById(R.id.editTextEducation);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextAddress = findViewById(R.id.editTextAddress);
        toggleFreeze = findViewById(R.id.toggleFreeze);
        btnSave = findViewById(R.id.btnSave);

        // Fetch faculty details
        fetchFacultyDetails();

        // Toggle Freeze/Unfreeze button
        toggleFreeze.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                enableEditing(true); // Unfreeze
                toggleFreeze.setBackgroundColor(Color.GREEN);
                toggleFreeze.setText("Unfreeze");
            } else {
                updateFacultyDetails(); // Save & Freeze
                enableEditing(false);
                toggleFreeze.setBackgroundColor(Color.RED);
                toggleFreeze.setText("Freeze");
            }
        });

        // Save button action
        btnSave.setOnClickListener(view -> {
            if (toggleFreeze.isChecked()) {
                updateFacultyDetails();
            } else {
                Toast.makeText(this, "Unfreeze to edit details!", Toast.LENGTH_SHORT).show();
            }
        });

        // Initially freeze editing
        enableEditing(false);
    }

    // Fetch faculty details from Firebase
    private void fetchFacultyDetails() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    editTextFacultyName.setText(snapshot.child("facultyName").getValue(String.class));
                    editTextEducation.setText(snapshot.child("education").getValue(String.class));
                    editTextPhone.setText(snapshot.child("phone").getValue(String.class));
                    editTextEmail.setText(snapshot.child("email").getValue(String.class));
                    editTextAddress.setText(snapshot.child("address").getValue(String.class));
                } else {
                    Toast.makeText(FacultyProfileActivity.this, "Faculty not found!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(FacultyProfileActivity.this, "Failed to fetch data!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Enable or disable editing
    private void enableEditing(boolean enable) {
        editTextEducation.setEnabled(enable);
        editTextPhone.setEnabled(enable);
        editTextEmail.setEnabled(enable);
        editTextAddress.setEnabled(enable);

        // Faculty name and ID should always remain uneditable
        editTextFacultyName.setEnabled(false);
    }

    // Update faculty details in Firebase
    private void updateFacultyDetails() {
        if (!validateFields()) return;

        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("education", editTextEducation.getText().toString());
        updatedData.put("phone", editTextPhone.getText().toString());
        updatedData.put("email", editTextEmail.getText().toString());
        updatedData.put("address", editTextAddress.getText().toString());

        databaseReference.updateChildren(updatedData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to update profile!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Validate input fields
    private boolean validateFields() {
        if (TextUtils.isEmpty(editTextEducation.getText().toString())) {
            editTextEducation.setError("Education is required!");
            return false;
        }
        if (TextUtils.isEmpty(editTextPhone.getText().toString()) || editTextPhone.getText().toString().length() != 10) {
            editTextPhone.setError("Valid Phone Number is required!");
            return false;
        }
        if (TextUtils.isEmpty(editTextEmail.getText().toString()) || !android.util.Patterns.EMAIL_ADDRESS.matcher(editTextEmail.getText().toString()).matches()) {
            editTextEmail.setError("Valid Email Address is required!");
            return false;
        }
        if (TextUtils.isEmpty(editTextAddress.getText().toString())) {
            editTextAddress.setError("Address is required!");
            return false;
        }
        return true;
    }
}
