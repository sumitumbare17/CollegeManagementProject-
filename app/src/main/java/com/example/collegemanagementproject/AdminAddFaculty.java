package com.example.collegemanagementproject;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class AdminAddFaculty extends AppCompatActivity {
    private EditText editTextFacultyName, editTextEducation, editTextPhone, editTextEmail, editTextAddress, editTextDefaultPassword;
    private Button btnSave, btnCancel;

    private DatabaseReference databaseReference;
    private int nextId = 1; // Initial value for unique ID

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_faculty);

        // Initialize Firebase Realtime Database
        databaseReference = FirebaseDatabase.getInstance().getReference("faculties");

        // Initialize Views
        editTextFacultyName = findViewById(R.id.editTextFacultyName);
        editTextEducation = findViewById(R.id.editTextEducation);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextAddress = findViewById(R.id.editTextAddress);
        editTextDefaultPassword = findViewById(R.id.editTextDefaultPassword);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);

        // Get the next available ID
        fetchNextId();

        // Save Button Click
        btnSave.setOnClickListener(view -> {
            if (validateFields()) {
                saveFacultyData();
            }
        });

        // Cancel Button Click
        btnCancel.setOnClickListener(view -> clearFields());
    }

    // Fetch the next available ID from Firebase
    private void fetchNextId() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    nextId = (int) snapshot.getChildrenCount() + 1;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminAddFaculty.this, "Failed to fetch ID!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Validate input fields
    private boolean validateFields() {
        if (TextUtils.isEmpty(editTextFacultyName.getText().toString())) {
            editTextFacultyName.setError("Faculty Name is required!");
            return false;
        }
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
        if (TextUtils.isEmpty(editTextDefaultPassword.getText().toString()) || editTextDefaultPassword.getText().toString().length() < 6) {
            editTextDefaultPassword.setError("Password must be at least 6 characters!");
            return false;
        }
        return true;
    }

    // Save faculty data to Firebase
    private void saveFacultyData() {
        String facultyName = editTextFacultyName.getText().toString();
        String education = editTextEducation.getText().toString();
        String phone = editTextPhone.getText().toString();
        String email = editTextEmail.getText().toString();
        String address = editTextAddress.getText().toString();
        String password = editTextDefaultPassword.getText().toString();

        // Create a new faculty object
        Map<String, Object> facultyData = new HashMap<>();
        facultyData.put("facultyName", facultyName);
        facultyData.put("education", education);
        facultyData.put("phone", phone);
        facultyData.put("email", email);
        facultyData.put("address", address);
        facultyData.put("defaultPassword", password);

        // Save data under the next ID
        databaseReference.child(String.valueOf(nextId)).setValue(facultyData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                showSuccessDialog();
                clearFields();
                nextId++; // Increment ID for the next faculty
            } else {
                Toast.makeText(AdminAddFaculty.this, "Failed to save data!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Show success dialog
    private void showSuccessDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Success")
                .setMessage("Faculty added successfully!")
                .setPositiveButton("OK", null)
                .show();
    }

    // Clear all input fields
    private void clearFields() {
        editTextFacultyName.setText("");
        editTextEducation.setText("");
        editTextPhone.setText("");
        editTextEmail.setText("");
        editTextAddress.setText("");
        editTextDefaultPassword.setText("");
    }
}
