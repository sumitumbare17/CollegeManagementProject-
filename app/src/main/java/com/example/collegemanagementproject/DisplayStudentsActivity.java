package com.example.collegemanagementproject;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DisplayStudentsActivity extends AppCompatActivity {

    private Spinner spinnerClass, spinnerDept;
    private ListView listViewStudents;
    private DatabaseReference databaseReference;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_students);

        // Initialize Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("student");

        // Initialize UI elements
        spinnerClass = findViewById(R.id.spinnerClass);
        spinnerDept = findViewById(R.id.spinnerDept);
        listViewStudents = findViewById(R.id.listViewStudents);

        // Set up class dropdown
        String[] classes = new String[]{"Select Class", "FY", "SY", "TY", "BTech"};
        ArrayAdapter<String> classAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, classes);
        spinnerClass.setAdapter(classAdapter);

        // Set up department dropdown
        String[] departments = new String[]{"Select Department", "CSE", "ECE", "MECH", "CIVIL"};
        ArrayAdapter<String> deptAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, departments);
        spinnerDept.setAdapter(deptAdapter);

        // Spinner item selection listeners
        spinnerClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                loadStudents();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        spinnerDept.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                loadStudents();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
    }

    private void loadStudents() {
        String selectedClass = spinnerClass.getSelectedItem().toString();
        String selectedDept = spinnerDept.getSelectedItem().toString();

        // Validate that a class and department are selected
        if (selectedClass.equals("Select Class") || selectedDept.equals("Select Department")) {
            return;
        }

        // Get reference to the Firebase path based on the selected class and department
        DatabaseReference studentsRef = databaseReference.child(selectedDept).child(selectedClass);

        studentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> studentList = new ArrayList<>();

                for (DataSnapshot studentSnapshot : dataSnapshot.getChildren()) {
                    // Assuming student data has a "name" field
                    String studentName = studentSnapshot.child("name").getValue(String.class);
                    studentList.add(studentName);
                }

                // Set up the list adapter to display student names
                ArrayAdapter<String> adapter = new ArrayAdapter<>(DisplayStudentsActivity.this, android.R.layout.simple_list_item_1, studentList);
                listViewStudents.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(DisplayStudentsActivity.this, "Failed to load students: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
