package com.example.collegemanagementproject;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminAssignClassteacher extends AppCompatActivity {

    private Spinner spinnerDepartment, spinnerClass, spinnerFaculty;
    private Button btnAssign, btnDeallocate;

    private DatabaseReference facultyReference;
    private DatabaseReference classTeacherReference;

    private List<String> facultyList = new ArrayList<>();
    private List<String> facultyIds = new ArrayList<>(); // To keep track of faculty IDs

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_assign_classteacher);

        // Initialize Firebase References
        facultyReference = FirebaseDatabase.getInstance().getReference("faculties");
        classTeacherReference = FirebaseDatabase.getInstance().getReference("classteacher");

        // Initialize Views
        spinnerDepartment = findViewById(R.id.spinnerDepartment);
        spinnerClass = findViewById(R.id.spinnerClass);
        spinnerFaculty = findViewById(R.id.spinnerFaculty);
        btnAssign = findViewById(R.id.btnAssign);
        btnDeallocate = findViewById(R.id.btnDeallocate);

        // Populate Department and Class Spinners
        populateStaticSpinners();

        // Load Faculty into Faculty Spinner
        loadFacultyIntoSpinner();

        // Assign Button Click
        btnAssign.setOnClickListener(view -> {
            if (validateSpinners()) {
                assignClassTeacher();
            }
        });

        // Deallocate Button Click
        btnDeallocate.setOnClickListener(view -> {
            if (validateFacultySelection()) {
                deallocateClass();
            }
        });
    }

    // Populate Department and Class Spinners
    private void populateStaticSpinners() {
        // Department Spinner
        String[] departments = {"Select Department", "MECH", "CSE", "CIVIL", "ECE"};
        ArrayAdapter<String> departmentAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, departments);
        departmentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDepartment.setAdapter(departmentAdapter);

        // Class Spinner
        String[] classes = {"Select Class", "FY", "SY", "TY", "BTECH"};
        ArrayAdapter<String> classAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, classes);
        classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerClass.setAdapter(classAdapter);
    }

    // Load Faculty into Spinner from Firebase
    private void loadFacultyIntoSpinner() {
        facultyReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                facultyList.clear();
                facultyIds.clear();
                facultyList.add("Select Faculty");

                for (DataSnapshot facultySnapshot : snapshot.getChildren()) {
                    String facultyName = facultySnapshot.child("facultyName").getValue(String.class);
                    String facultyId = facultySnapshot.getKey(); // Save faculty ID
                    if (facultyName != null && facultyId != null) {
                        facultyList.add(facultyName);
                        facultyIds.add(facultyId);
                    }
                }

                ArrayAdapter<String> facultyAdapter = new ArrayAdapter<>(AdminAssignClassteacher.this, android.R.layout.simple_spinner_item, facultyList);
                facultyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerFaculty.setAdapter(facultyAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminAssignClassteacher.this, "Failed to load faculties!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Validate Spinner Selections
    private boolean validateSpinners() {
        if (spinnerDepartment.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Select a Department!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (spinnerClass.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Select a Class!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (spinnerFaculty.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Select a Faculty!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    // Validate Faculty Selection for Deallocation
    private boolean validateFacultySelection() {
        if (spinnerFaculty.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Select a Faculty to Deallocate!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    // Assign Class Teacher
    private void assignClassTeacher() {
        String department = spinnerDepartment.getSelectedItem().toString();
        String className = spinnerClass.getSelectedItem().toString();
        int facultyIndex = spinnerFaculty.getSelectedItemPosition() - 1; // Adjust for "Select Faculty"
        String facultyId = facultyIds.get(facultyIndex);
        String facultyName = facultyList.get(facultyIndex + 1);

        // Check if the faculty is already assigned to a class
        classTeacherReference.child(facultyId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(AdminAssignClassteacher.this, "This faculty is already assigned to a class!", Toast.LENGTH_SHORT).show();
                } else {
                    // Assign class teacher
                    Map<String, Object> data = new HashMap<>();
                    data.put("department", department);
                    data.put("class", className);
                    data.put("facultyName", facultyName);

                    classTeacherReference.child(facultyId).setValue(data).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(AdminAssignClassteacher.this, "Class Teacher Assigned Successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AdminAssignClassteacher.this, "Failed to Assign Class Teacher!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminAssignClassteacher.this, "Failed to check assignment!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Deallocate Class from Faculty
    private void deallocateClass() {
        int facultyIndex = spinnerFaculty.getSelectedItemPosition() - 1; // Adjust for "Select Faculty"
        String facultyId = facultyIds.get(facultyIndex);

        classTeacherReference.child(facultyId).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(AdminAssignClassteacher.this, "Class Deallocated Successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(AdminAssignClassteacher.this, "Failed to Deallocate Class!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
