package com.example.collegemanagementproject;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentAddQuery extends AppCompatActivity {
    private Spinner spinnerFaculty;
    private EditText etQuery;
    private Button btnSubmitQuery;
    private RecyclerView rvQueries;
    private DatabaseReference dbRef;
    private List<String> facultyList, facultyIds;
    private QueryAdapter queryAdapter;
    private String selectedFacultyId, studentId;
    private List<QueryModel> queryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_add_query);

        studentId = getIntent().getStringExtra("STUDENT_ID"); // Get logged-in student ID
        spinnerFaculty = findViewById(R.id.spinnerFaculty);
        etQuery = findViewById(R.id.etQuery);
        btnSubmitQuery = findViewById(R.id.btnSubmitQuery);
        rvQueries = findViewById(R.id.rvQueries);
        dbRef = FirebaseDatabase.getInstance().getReference();

        facultyList = new ArrayList<>();
        facultyIds = new ArrayList<>();
        queryList = new ArrayList<>();

        rvQueries.setLayoutManager(new LinearLayoutManager(this));
        queryAdapter = new QueryAdapter(this, queryList);
        rvQueries.setAdapter(queryAdapter);

        fetchFacultyNames();
        fetchStudentQueries();

        btnSubmitQuery.setOnClickListener(v -> submitQuery());
    }

    private void fetchFacultyNames() {
        dbRef.child("faculties").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                facultyList.clear();
                facultyIds.clear();
                facultyList.add("Select Faculty");

                for (DataSnapshot facultySnapshot : snapshot.getChildren()) {
                    String facultyId = facultySnapshot.getKey();
                    String facultyName = facultySnapshot.child("facultyName").getValue(String.class);
                    if (facultyName != null) {
                        facultyList.add(facultyName);
                        facultyIds.add(facultyId);
                    }
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(StudentAddQuery.this, android.R.layout.simple_spinner_item, facultyList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerFaculty.setAdapter(adapter);

                spinnerFaculty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (position > 0) selectedFacultyId = facultyIds.get(position - 1);
                    }
                    @Override public void onNothingSelected(AdapterView<?> parent) {}
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void submitQuery() {
        String queryText = etQuery.getText().toString().trim();
        if (selectedFacultyId == null || queryText.isEmpty()) {
            Toast.makeText(this, "Please select a faculty and enter a query!", Toast.LENGTH_SHORT).show();
            return;
        }

        String queryId = dbRef.child("queries").push().getKey();
        Map<String, Object> queryData = new HashMap<>();
        queryData.put("studentId", studentId);
        queryData.put("facultyId", selectedFacultyId);
        queryData.put("queryText", queryText);
        queryData.put("response", "");
        queryData.put("timestamp", System.currentTimeMillis());

        dbRef.child("queries").child(queryId).setValue(queryData).addOnSuccessListener(unused ->
                Toast.makeText(StudentAddQuery.this, "Query submitted!", Toast.LENGTH_SHORT).show());

        etQuery.setText("");
        fetchStudentQueries();
    }

    private void fetchStudentQueries() {
        dbRef.child("queries").orderByChild("studentId").equalTo(studentId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                queryList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    QueryModel query = data.getValue(QueryModel.class);
                    if (query != null) queryList.add(query);
                }
                queryAdapter.notifyDataSetChanged();
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}
 class QueryModel {
    private String queryId;
    private String studentId;
    private String facultyId;
    private String queryText;
    private String response;
    private long timestamp;

    // Default constructor for Firebase
    public QueryModel() {}

    // Constructor with queryId
    public QueryModel(String queryId, String facultyId, String queryText, String response, String studentId, long timestamp) {
        this.queryId = queryId;
        this.facultyId = facultyId;
        this.queryText = queryText;
        this.response = response;
        this.studentId = studentId;
        this.timestamp = timestamp;
    }
     public QueryModel(String facultyId, String queryText, String response, String studentId, long timestamp) {
         this.facultyId = facultyId;
         this.queryText = queryText;
         this.response = response;
         this.studentId = studentId;
         this.timestamp = timestamp;
     }

    // Getters and setters
    public String getQueryId() {
        return queryId;
    }

    public void setQueryId(String queryId) {
        this.queryId = queryId;
    }

    public String getFacultyId() {
        return facultyId;
    }

    public void setFacultyId(String facultyId) {
        this.facultyId = facultyId;
    }

    public String getQueryText() {
        return queryText;
    }

    public void setQueryText(String queryText) {
        this.queryText = queryText;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
