package com.example.collegemanagementproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RelativeLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AdminDashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_dashboard);
        RelativeLayout addstud = findViewById(R.id.addStudentLayout);
        addstud.setOnClickListener(view -> {
            Intent intent = new Intent(AdminDashboard.this, AdminAddStudent.class);
            startActivity(intent);
        });

        RelativeLayout addfac = findViewById(R.id.addFacultyLayout);
        addfac.setOnClickListener(view -> {
            Intent intent = new Intent(AdminDashboard.this, AdminAddFaculty.class);
            startActivity(intent);
        });
        RelativeLayout addclass = findViewById(R.id.AdminAssignClassteacher);
        addclass.setOnClickListener(view -> {
            Intent intent = new Intent(AdminDashboard.this, AdminAssignClassteacher.class);
            startActivity(intent);
        });

        RelativeLayout addtime = findViewById(R.id.addTimeTableLayout);
        addtime.setOnClickListener(view -> {
            Intent intent = new Intent(AdminDashboard.this, AdminAddTimeTable.class);
            startActivity(intent);
        });

    }
}