package com.example.collegemanagementproject;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AdminAddStudent extends AppCompatActivity {

    private EditText editTextName, editTextAge, editTextPhone, editTextAddress, editTextPassword;
    private Spinner spinnerClass, spinnerDept;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_student);

        // Initialize Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("student");

        // Initialize UI elements
        editTextName = findViewById(R.id.editTextName);
        editTextAge = findViewById(R.id.editTextAge);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextAddress = findViewById(R.id.editTextAddress);
        editTextPassword = findViewById(R.id.editTextPassword);
        spinnerClass = findViewById(R.id.spinnerTextClass);
        spinnerDept = findViewById(R.id.spinnerDept);

        // Set up class dropdown
        String[] classes = new String[]{"Select Class", "FY", "SY", "TY", "B.Tech"};
        ArrayAdapter<String> classAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, classes);
        spinnerClass.setAdapter(classAdapter);

        // Set up department dropdown
        String[] departments = new String[]{"Select Department", "CSE", "ECE", "MECH", "CIVIL"};
        ArrayAdapter<String> deptAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, departments);
        spinnerDept.setAdapter(deptAdapter);

        // Date picker for DOB
        editTextAge.setOnClickListener(view -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    AdminAddStudent.this,
                    (view1, selectedYear, selectedMonth, selectedDay) -> {
                        String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        editTextAge.setText(selectedDate);
                    },
                    year,
                    month,
                    day
            );

            datePickerDialog.show();
        });

        // Save button functionality
        Button btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(v -> {
            if (validateFields()) {
                saveStudentDetails();
            }
        });
    }

    private boolean validateFields() {
        String name = editTextName.getText().toString().trim();
        String dob = editTextAge.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String address = editTextAddress.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String selectedClass = spinnerClass.getSelectedItem().toString();
        String selectedDept = spinnerDept.getSelectedItem().toString();

        if (name.isEmpty()) {
            editTextName.setError("Name is required!");
            return false;
        }

        if (dob.isEmpty()) {
            editTextAge.setError("Date of Birth is required!");
            return false;
        }

        if (phone.isEmpty() || phone.length() != 10 || !phone.matches("\\d+")) {
            editTextPhone.setError("Valid phone number is required!");
            return false;
        }

        if (address.isEmpty()) {
            editTextAddress.setError("Address is required!");
            return false;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Password is required!");
            return false;
        }

        if (selectedClass.equals("Select Class")) {
            Toast.makeText(this, "Please select a class", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (selectedDept.equals("Select Department")) {
            Toast.makeText(this, "Please select a department", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void saveStudentDetails() {
        String name = editTextName.getText().toString().trim();
        String dob = editTextAge.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String address = editTextAddress.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String selectedClass = spinnerClass.getSelectedItem().toString();
        String selectedDept = spinnerDept.getSelectedItem().toString();

        // Generate unique student ID
        String studentId = generateStudentId();

        if (studentId != null) {
            Map<String, String> studentDetails = new HashMap<>();
            studentDetails.put("name", name);
            studentDetails.put("dob", dob);
            studentDetails.put("phone", phone);
            studentDetails.put("address", address);
            studentDetails.put("password", password);
            studentDetails.put("class", selectedClass);
            studentDetails.put("department", selectedDept);

            // Construct Firebase path with branch, year, and student ID
            DatabaseReference branchRef = databaseReference.child(selectedDept) // Branch
                    .child(selectedClass) // Year
                    .child(studentId);    // Student ID

            // Save the student details in the constructed path
            branchRef.setValue(studentDetails)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(AdminAddStudent.this, "Student added successfully!", Toast.LENGTH_SHORT).show();
                        finish(); // Close the activity
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(AdminAddStudent.this, "Failed to add student: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private String generateStudentId() {
        // Generate the ID based on the current time to ensure uniqueness
        long currentTime = System.currentTimeMillis(); // Get the current timestamp
        String studentId = "S" + currentTime; // Prefix "S" with the timestamp as numeric ID

        // Check if the generated student ID is numeric and follows a certain pattern
        if (studentId.matches("S\\d+")) {
            return studentId;
        } else {
            return null; // Return null if the ID does not match the expected pattern
        }
    }


}

