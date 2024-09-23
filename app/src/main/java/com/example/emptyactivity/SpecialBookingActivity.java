package com.example.emptyactivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class SpecialBookingActivity extends AppCompatActivity {

    private EditText nameEditText, departmentEditText, purposeEditText, chairsEditText;
    private RadioGroup audioSystemRadioGroup;
    private Button submitSpecialBookingButton;
    private String selectedHall, selectedDate, username;
    private List<String> selectedTimeSlots; // List to hold multiple time slots
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_special_booking);

        selectedHall = getIntent().getStringExtra("selected_hall");
        selectedDate = getIntent().getStringExtra("selected_date");
        selectedTimeSlots = getIntent().getStringArrayListExtra("selected_time_slots"); // Retrieve the list of time slots
        username = getIntent().getStringExtra("username");

        db = FirebaseFirestore.getInstance();

        // Initialize views
        TextView selectedHallTextView = findViewById(R.id.selectedHallTextView);
        TextView selectedTimeSlotsTextView = findViewById(R.id.selectedTimeSlotsTextView); // TextView to display time slots
        nameEditText = findViewById(R.id.nameEditText);
        departmentEditText = findViewById(R.id.departmentEditText);
        purposeEditText = findViewById(R.id.purposeEditText);
        chairsEditText = findViewById(R.id.chairsEditText);
        audioSystemRadioGroup = findViewById(R.id.audioSystemRadioGroup);
        submitSpecialBookingButton = findViewById(R.id.submitSpecialBookingButton);

        // Display selected hall
        selectedHallTextView.setText("Selected Hall: " + selectedHall);

        // Display all selected time slots
        if (selectedTimeSlots != null && !selectedTimeSlots.isEmpty()) {
            selectedTimeSlotsTextView.setText("Selected Time Slots: " + String.join(", ", selectedTimeSlots));
        } else {
            selectedTimeSlotsTextView.setText("No Time Slots Selected");
        }

        submitSpecialBookingButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString();
            String department = departmentEditText.getText().toString();
            String purpose = purposeEditText.getText().toString();
            String chairs = chairsEditText.getText().toString();
            int numberOfChairs = Integer.parseInt(chairs); // Convert String to int
            boolean audioSystem = audioSystemRadioGroup.getCheckedRadioButtonId() == R.id.radioYes;

            if (name.isEmpty() || department.isEmpty() || purpose.isEmpty() || chairs.isEmpty()) {
                Toast.makeText(SpecialBookingActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                saveSpecialBooking(name, department, purpose, numberOfChairs, audioSystem);
            }
        });
    }

    private void saveSpecialBooking(String name, String department, String purpose, int numberOfChairs, boolean audioSystem) {
        // Save to Firestore
        db.collection("bookings")
                .add(new SpecialBookingRequest(name, department, purpose, selectedDate, selectedHall, selectedTimeSlots, "pending", numberOfChairs, audioSystem, username))
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(SpecialBookingActivity.this, "Special Booking request sent", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(SpecialBookingActivity.this, "Failed to send booking request", Toast.LENGTH_SHORT).show();
                });
    }
}
