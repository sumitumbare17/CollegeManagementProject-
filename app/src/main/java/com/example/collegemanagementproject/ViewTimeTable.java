package com.example.collegemanagementproject;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class ViewTimeTable extends AppCompatActivity {

    private ImageView imgTimetable;
    private TextView txtTimetableDetails;
    private String department, className, facultyName;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_time_table);

        imgTimetable = findViewById(R.id.imgTimetable);
        txtTimetableDetails = findViewById(R.id.txtTimetableDetails);

        // Retrieve data from Intent
        department = getIntent().getStringExtra("department");
        className = getIntent().getStringExtra("className");
        facultyName = getIntent().getStringExtra("facultyName");

        // Initialize Firebase references
        databaseReference = FirebaseDatabase.getInstance().getReference("timetables");
        storageReference = FirebaseStorage.getInstance().getReference("timetables");

        // Query Firebase for the timetable
        loadTimetable(department, className);
    }

    private void loadTimetable(String department, String className) {
        // Use department and className to fetch the timetable URL
        databaseReference.child(department.replace(" ", "_"))
                .child(className.replace(" ", "_"))
                .child("imageUrl")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String imageUrl = dataSnapshot.getValue(String.class);

                            // Load the image using Picasso or Glide
                            Picasso.get().load(imageUrl).into(imgTimetable);

                            // Display details (optional)
                            txtTimetableDetails.setText("Timetable for " + className + " (" + department + ")");
                        } else {
                            Toast.makeText(ViewTimeTable.this, "Timetable not found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(ViewTimeTable.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


}
