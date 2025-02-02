package com.example.collegemanagementproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StudentDashboard extends AppCompatActivity {
    String name ,dob,phone,address,password,studentClass,department;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);
        String studentId = getIntent().getStringExtra("STUDENT_ID");
        fetchStudentDetails(studentId);

        RelativeLayout addstud = findViewById(R.id.viewStudentLayout);
        addstud.setOnClickListener(view -> {
            Intent intent = new Intent(StudentDashboard.this, DisplayStudentsActivity.class);
            startActivity(intent);
        });
        @SuppressLint({"LocalSuppress", "MissingInflatedId"}) RelativeLayout addfac = findViewById(R.id.addQueriesLayout);
        addfac.setOnClickListener(view -> {
            Intent intent = new Intent(StudentDashboard.this, StudentAddQuery.class);
            intent.putExtra("STUDENT_ID",studentId );

            startActivity(intent);
        });
        RelativeLayout viewtime = findViewById(R.id.viewTimetableLayout);
        viewtime.setOnClickListener(view -> {
            Intent intent = new Intent(StudentDashboard.this, ViewTimeTable.class);
            intent.putExtra("department", department);
            intent.putExtra("className",studentClass );
            startActivity(intent);
        });

        RelativeLayout viewprofile = findViewById(R.id.viewProfileLayout);
        viewprofile.setOnClickListener(view -> {
            Intent intent = new Intent(StudentDashboard.this, StudentProfileActivity.class);
            intent.putExtra("studentId", studentId);
            startActivity(intent);
        });



    }

    private void fetchStudentDetails(String studentId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("student");

        databaseReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                for (DataSnapshot deptSnapshot : task.getResult().getChildren()) { // Loop through departments
                    for (DataSnapshot classSnapshot : deptSnapshot.getChildren()) { // Loop through classes
                        if (classSnapshot.hasChild(studentId)) { // Check if student exists in this class
                            DataSnapshot studentSnapshot = classSnapshot.child(studentId);
                             name = studentSnapshot.child("name").getValue(String.class);
                             dob = studentSnapshot.child("dob").getValue(String.class);
                             phone = studentSnapshot.child("phone").getValue(String.class);
                             address = studentSnapshot.child("address").getValue(String.class);
                             password = studentSnapshot.child("password").getValue(String.class);
                             studentClass = studentSnapshot.child("class").getValue(String.class);
                             department = studentSnapshot.child("department").getValue(String.class);
                             return; // Exit loop once student is found
                        }
                    }
                }
                Toast.makeText(this, "Student not found!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error fetching data!", Toast.LENGTH_SHORT).show();
            }
        });
    }



}