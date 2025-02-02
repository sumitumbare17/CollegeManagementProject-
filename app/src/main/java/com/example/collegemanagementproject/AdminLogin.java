package com.example.collegemanagementproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AdminLogin extends AppCompatActivity {

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_login);
        EditText id , pass;
        id = findViewById(R.id.adminlogin_email);
        pass = findViewById(R.id.adminlogin_password);
        Button login = findViewById(R.id.adminlogin_button);
        login.setOnClickListener(view -> {
            if(id.getText().toString().equals("admin") && pass.getText().toString().equals("admin"))
            {
                Intent intent = new Intent(AdminLogin.this, AdminDashboard.class);
                startActivity(intent);
            }else{
                id.setError("Invalid Credentials");
                pass.setError("Invalid Credentials");
                Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
            }        });

        TextView facultyPage , studentPage;


             facultyPage   = findViewById(R.id.faculty_text);
             studentPage = findViewById(R.id.student_login_text);

             facultyPage.setOnClickListener(view -> {
                 Intent intent = new Intent(AdminLogin.this, FacultyLogin.class);
                 startActivity(intent);
             });

             studentPage.setOnClickListener(view -> {
                 Intent intent = new Intent(AdminLogin.this, StudentLogin.class);
                 startActivity(intent);
             });
    }
}
