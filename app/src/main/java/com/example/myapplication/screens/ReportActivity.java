package com.example.myapplication.screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.model.Report;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ReportActivity extends AppCompatActivity {
    String userId;
    String myId;
    String roomId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        userId = getIntent().getStringExtra("userId");
        myId = getIntent().getStringExtra("myId");
        roomId = getIntent().getStringExtra("roomId");
        RadioGroup reportTypeRadioGroup = findViewById(R.id.reportType);
        Button nextButton = findViewById(R.id.btn_report);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the selected report type
                int selectedReportTypeId = reportTypeRadioGroup.getCheckedRadioButtonId();

                // Check if any radio button is selected
                if (selectedReportTypeId != -1) {
                    // Convert the selected report type ID to the actual value
                    String reportType = getReportType(selectedReportTypeId);

                    // Perform the report submission with userId, myId, roomId, and selected report type
                    submitReport(userId, myId, roomId, reportType);
                } else {
                    // No radio button is selected, show a message to the user
                    Toast.makeText(ReportActivity.this, "Please select a report type", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private String getReportType(int reportTypeId) {
        if (reportTypeId == R.id.luadao) {
            return "Lừa đảo";
        } else if (reportTypeId == R.id.trunglap) {
            return "Trùng lặp";
        } else if (reportTypeId == R.id.dachothue) {
            return "Đã cho thuê";
            // Add more conditions for other report types
        } else {
            return "Lý do khác";
        }
    }
    private void submitReport(String userId, String myId, String roomId, String reportType) {
        DatabaseReference reportsRef = FirebaseDatabase.getInstance().getReference("reports");
        String reportId = FirebaseDatabase.getInstance().getReference("reports").push().getKey();

        // Create a new Report object with the provided information
        Report report = new Report(reportId, userId, myId, roomId, reportType);

        // Use push() to generate a unique ID for the report and save it to the "reports" node
        DatabaseReference newReportRef = reportsRef.push();
        newReportRef.setValue(report)
                .addOnSuccessListener(aVoid -> {
                    // Report saved successfully
                    Toast.makeText(ReportActivity.this, "Report submitted successfully", Toast.LENGTH_SHORT).show();
                    checkAndDeleteRoomIfNecessary(roomId);
                    finish(); // Finish the activity or perform other actions as needed
                })
                .addOnFailureListener(e -> {
                    // Failed to save the report
                    Toast.makeText(ReportActivity.this, "Failed to submit report", Toast.LENGTH_SHORT).show();
                });
    }
    private void checkAndDeleteRoomIfNecessary(String roomId) {
        DatabaseReference reportsRef = FirebaseDatabase.getInstance().getReference("reports");

        // Query to find reports with the specified roomId
        Query query = reportsRef.orderByChild("roomId").equalTo(roomId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Count the occurrences of the roomId in reports
                int reportCount = (int) dataSnapshot.getChildrenCount();

                // If roomId appears 5 times or more, delete the corresponding room
                if (reportCount >= 5) {
                    deleteRoom(roomId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error
            }
        });
    }
    private void deleteRoom(String roomId) {
        DatabaseReference roomsRef = FirebaseDatabase.getInstance().getReference("rooms");

        roomsRef.orderByChild("roomId").equalTo(roomId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot roomSnapshot : dataSnapshot.getChildren()) {
                    // Delete the room
                    roomSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error
            }
        });
    }
}