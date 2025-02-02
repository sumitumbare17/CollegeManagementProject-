package com.example.collegemanagementproject;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class StudentProfileActivity extends AppCompatActivity {
    private EditText etStudentId, etName, etDepartment, etClass, etDob, etPhone, etAddress, etPassword;
    private ToggleButton toggleFreeze;
    private DatabaseReference databaseReference;
    private String studentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);

        // Initialize UI elements
        etStudentId = findViewById(R.id.et_student_id);
        etName = findViewById(R.id.et_name);
        etDepartment = findViewById(R.id.et_department);
        etClass = findViewById(R.id.et_class);
        etDob = findViewById(R.id.et_dob);
        etPhone = findViewById(R.id.et_phone);
        etAddress = findViewById(R.id.et_address);
        etPassword = findViewById(R.id.et_password);
        toggleFreeze = findViewById(R.id.toggle_freeze);

        // Get studentId from Intent
        studentId = getIntent().getStringExtra("studentId");

        // Firebase Reference
        databaseReference = FirebaseDatabase.getInstance().getReference("student");


        // Fetch and display student details
        fetchStudentDetails();

        // Toggle button logic

// Inside onCreate()
        toggleFreeze.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                enableEditing(true);
                toggleFreeze.setBackground(new ColorDrawable(Color.GREEN)); // Change background color
                toggleFreeze.setText("Unfreeze");
            } else {
                updateStudentDetails();
                enableEditing(false);
                toggleFreeze.setBackground(new ColorDrawable(Color.RED)); // Change background color
                toggleFreeze.setText("Freeze");
            }
        });
    }

    private void fetchStudentDetails() {
        databaseReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                for (DataSnapshot deptSnapshot : task.getResult().getChildren()) {
                    for (DataSnapshot classSnapshot : deptSnapshot.getChildren()) {
                        if (classSnapshot.hasChild(studentId)) {
                            DataSnapshot studentSnapshot = classSnapshot.child(studentId);

                            // Fetching details
                            etStudentId.setText(studentId);
                            etName.setText(studentSnapshot.child("name").getValue(String.class));
                            etDepartment.setText(studentSnapshot.child("department").getValue(String.class));
                            etClass.setText(studentSnapshot.child("class").getValue(String.class));
                            etDob.setText(studentSnapshot.child("dob").getValue(String.class));
                            etPhone.setText(studentSnapshot.child("phone").getValue(String.class));
                            etAddress.setText(studentSnapshot.child("address").getValue(String.class));
                            etPassword.setText(studentSnapshot.child("password").getValue(String.class));

                            return; // Exit loop after finding student
                        }
                    }
                }
                Toast.makeText(this, "Student not found!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error fetching data!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void enableEditing(boolean enable) {
        etDob.setEnabled(enable);
        etPhone.setEnabled(enable);
        etAddress.setEnabled(enable);
        etPassword.setEnabled(enable);
    }

    private void updateStudentDetails() {
        String dob = etDob.getText().toString();
        String phone = etPhone.getText().toString();
        String address = etAddress.getText().toString();
        String password = etPassword.getText().toString();

        if (dob.isEmpty() || phone.isEmpty() || address.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "All fields must be filled!", Toast.LENGTH_SHORT).show();
            return;
        }

        databaseReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                for (DataSnapshot deptSnapshot : task.getResult().getChildren()) {
                    for (DataSnapshot classSnapshot : deptSnapshot.getChildren()) {
                        if (classSnapshot.hasChild(studentId)) {
                            DatabaseReference studentRef = classSnapshot.child(studentId).getRef();

                            // Update only allowed fields
                            Map<String, Object> updates = new HashMap<>();
                            updates.put("dob", dob);
                            updates.put("phone", phone);
                            updates.put("address", address);
                            updates.put("password", password);

                            studentRef.updateChildren(updates).addOnSuccessListener(unused ->
                                    Toast.makeText(this, "Profile updated & frozen!", Toast.LENGTH_SHORT).show()
                            ).addOnFailureListener(e ->
                                    Toast.makeText(this, "Update failed!", Toast.LENGTH_SHORT).show()
                            );

                            return;
                        }
                    }
                }
            }
        });
    }
}
