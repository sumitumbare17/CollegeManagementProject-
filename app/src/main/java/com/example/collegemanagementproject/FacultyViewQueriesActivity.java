package com.example.collegemanagementproject;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;

public class FacultyViewQueriesActivity extends AppCompatActivity implements QueryAdapterFaculty.OnResponseListener {
    private RecyclerView rvFacultyQueries;
    private QueryAdapterFaculty queryAdapter;
    private List<QueryModel> queryList;
    private DatabaseReference databaseReference;
    private String facultyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_view_queries);

        facultyId = getIntent().getStringExtra("facultyID");
        rvFacultyQueries = findViewById(R.id.rvFacultyQueries);
        rvFacultyQueries.setLayoutManager(new LinearLayoutManager(this));

        queryList = new ArrayList<>();
        queryAdapter = new QueryAdapterFaculty(queryList, this, true);
        rvFacultyQueries.setAdapter(queryAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("queries");

        fetchFacultyQueries();
    }
    private void fetchFacultyQueries() {
        databaseReference.orderByChild("facultyId").equalTo(facultyId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                queryList.clear();
                for (DataSnapshot querySnapshot : snapshot.getChildren()) {
                    QueryModel query = querySnapshot.getValue(QueryModel.class);
                    if (query != null) {
                        query.setQueryId(querySnapshot.getKey()); // Ensure Query ID is stored

                        // Exclude queries that already have a response
                        if (query.getResponse() == null || query.getResponse().isEmpty()) {
                            queryList.add(query);
                        }
                    }
                }
                queryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(FacultyViewQueriesActivity.this, "Failed to load queries!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResponseSubmit(String queryId, String response) {
        if (queryId == null || queryId.isEmpty()) {
            Toast.makeText(this, "Query ID is missing!", Toast.LENGTH_SHORT).show();
            return;
        }

        databaseReference.child(queryId).child("response").setValue(response)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Response Submitted", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to Submit Response", Toast.LENGTH_SHORT).show());
    }
}
