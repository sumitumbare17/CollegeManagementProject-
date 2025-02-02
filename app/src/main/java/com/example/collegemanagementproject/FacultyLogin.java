package com.example.collegemanagementproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FacultyLogin extends AppCompatActivity {

    private EditText editTextFacultyID, editTextPassword;
    private Button btnLogin;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_login);

        // Initialize Firebase Database Reference
        databaseReference = FirebaseDatabase.getInstance().getReference("faculties");

        // Initialize Views
        editTextFacultyID = findViewById(R.id.login_email);
        editTextPassword = findViewById(R.id.login_password);
        btnLogin = findViewById(R.id.login_button);

        // Set Login Button Click Listener
        btnLogin.setOnClickListener(view -> {
            String facultyID = editTextFacultyID.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            if (validateFields(facultyID, password)) {
                loginFaculty(facultyID, password);
            }
        });
    }

    // Validate input fields
    private boolean validateFields(String facultyID, String password) {
        if (TextUtils.isEmpty(facultyID)) {
            editTextFacultyID.setError("Faculty ID is required!");
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Password is required!");
            return false;
        }
        return true;
    }

    // Login faculty with ID and password
    private void loginFaculty(String facultyID, String password) {
        databaseReference.child(facultyID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String dbPassword = snapshot.child("defaultPassword").getValue(String.class);

                    if (dbPassword != null && dbPassword.equals(password)) {
                        Toast.makeText(FacultyLogin.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(FacultyLogin.this, FacultyDashboard.class);
                        intent.putExtra("facultyID", facultyID);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(FacultyLogin.this, "Invalid ID or Password!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(FacultyLogin.this, "Faculty ID not found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(FacultyLogin.this, "Database Error! Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
