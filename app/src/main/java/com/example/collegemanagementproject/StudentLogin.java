package com.example.collegemanagementproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StudentLogin extends AppCompatActivity {

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_login);

        // Initialize Firebase reference
        databaseReference = FirebaseDatabase.getInstance().getReference("student");

        // Initialize UI elements
        Button button = findViewById(R.id.login_button);
        EditText studentId = findViewById(R.id.login_email);
        EditText pass = findViewById(R.id.login_password);

        button.setOnClickListener(v -> {
            String enteredStudentId = studentId.getText().toString().trim();
            String enteredPassword = pass.getText().toString().trim();

            // Validate the student ID and password fields
            if (enteredStudentId.isEmpty() || enteredPassword.isEmpty()) {
                if (enteredStudentId.isEmpty()) studentId.setError("Student ID is required");
                if (enteredPassword.isEmpty()) pass.setError("Password is required");
            } else {
                // Fetch student details from Firebase and validate
                validateStudentLogin(enteredStudentId, enteredPassword);
            }
        });

        // Faculty login redirection
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView textView = findViewById(R.id.faculty_text);
        textView.setOnClickListener(v -> {
            Intent intent = new Intent(StudentLogin.this, FacultyLogin.class);
            startActivity(intent);
            finish();
        });
    }

    private void validateStudentLogin(String enteredStudentId, String enteredPassword) {
        // Loop through all student records in the database to validate login credentials
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean isValid = false;

                // Loop through the data structure: branch -> year -> studentId
                for (DataSnapshot branchSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot yearSnapshot : branchSnapshot.getChildren()) {
                        // For each student in this branch and year
                        for (DataSnapshot studentSnapshot : yearSnapshot.getChildren()) {
                            // Fetch the student ID and password
                            String storedStudentId = studentSnapshot.getKey(); // This is the student ID
                            String storedPassword = studentSnapshot.child("password").getValue(String.class);

                            // Check if credentials match
                            if (enteredStudentId.equals(storedStudentId) && enteredPassword.equals(storedPassword)) {
                                // Credentials matched
                                isValid = true;
                                break;
                            }
                        }
                        if (isValid) break;
                    }
                    if (isValid) break;
                }

                if (isValid) {
                    // Successfully logged in, go to dashboard
                    Intent intent = new Intent(StudentLogin.this, StudentDashboard.class);
                    intent.putExtra("STUDENT_ID",enteredStudentId );
                    startActivity(intent);
                    finish();
                } else {
                    // Show error if credentials are invalid
                    Toast.makeText(StudentLogin.this, "Invalid Student ID or Password", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle database error
                Toast.makeText(StudentLogin.this, "Failed to load data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
