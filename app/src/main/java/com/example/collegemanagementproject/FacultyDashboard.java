package com.example.collegemanagementproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FacultyDashboard extends AppCompatActivity {
    String department ,className,facultyName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_faculty_dashboard);
        String facultyID = getIntent().getStringExtra("facultyID");


        RelativeLayout viewStudentLayout = findViewById(R.id.viewStudentLayout);
        viewStudentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FacultyDashboard.this, DisplayStudentsActivity.class);
                startActivity(intent);
            }
        });

        RelativeLayout viewQueriesLayout = findViewById(R.id.viewQueriesLayout);
        viewQueriesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FacultyDashboard.this, FacultyViewQueriesActivity.class);
                intent.putExtra("facultyID", facultyID);
                startActivity(intent);
            }
        });

        RelativeLayout viewTimeTable = findViewById(R.id.viewTimetableLayout);
        viewTimeTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchAssignedClass(facultyID);

                if(department!= null && className!=null && facultyName!=null) {
                    Intent intent = new Intent(FacultyDashboard.this, ViewTimeTable.class);
                    intent.putExtra("department", department);
                    intent.putExtra("className", className);
                    intent.putExtra("facultyName", facultyName);
                    startActivity(intent);
                }

            }
        });
        RelativeLayout viewProfile = findViewById(R.id.viewProfileLayout);
        viewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FacultyDashboard.this, FacultyProfileActivity.class);
                i.putExtra("facultyId", facultyID);
                startActivity(i);
            }
        });

    }

    private void fetchAssignedClass(String facultyId) {
        DatabaseReference classTeacherRef = FirebaseDatabase.getInstance().getReference("classteacher");

        classTeacherRef.child(facultyId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                     department = snapshot.child("department").getValue(String.class);
                     className = snapshot.child("class").getValue(String.class);
                     facultyName = snapshot.child("facultyName").getValue(String.class);

                    // Display the fetched details
                    Toast.makeText(FacultyDashboard.this,
                            "Assigned Class: " + className + "\nDepartment: " + department,
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(FacultyDashboard.this,
                            "No class assigned to this faculty!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(FacultyDashboard.this,
                        "Failed to fetch assigned class!", Toast.LENGTH_SHORT).show();
            }
        });
    }

}