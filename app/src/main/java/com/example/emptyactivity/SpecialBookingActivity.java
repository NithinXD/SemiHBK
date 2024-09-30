package com.example.emptyactivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.List;

public class SpecialBookingActivity extends AppCompatActivity {

    private EditText nameEditText, departmentEditText, purposeEditText, chairsEditText;
    private RadioGroup audioSystemRadioGroup;
    private Button submitSpecialBookingButton, uploadPdfButton;
    private String selectedHall, selectedDate, username;
    private List<String> selectedTimeSlots; // List to hold multiple time slots
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private Uri pdfUri; // Uri to hold the selected PDF file

    private static final int PICK_PDF_REQUEST = 1; // Request code for file picker

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_special_booking);

        selectedHall = getIntent().getStringExtra("selected_hall");
        selectedDate = getIntent().getStringExtra("selected_date");
        selectedTimeSlots = getIntent().getStringArrayListExtra("selected_time_slots"); // Retrieve the list of time slots
        username = getIntent().getStringExtra("username");

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        // Initialize views
        TextView selectedHallTextView = findViewById(R.id.selectedHallTextView);
        TextView selectedTimeSlotsTextView = findViewById(R.id.selectedTimeSlotsTextView); // TextView to display time slots
        nameEditText = findViewById(R.id.nameEditText);
        departmentEditText = findViewById(R.id.departmentEditText);
        purposeEditText = findViewById(R.id.purposeEditText);
        chairsEditText = findViewById(R.id.chairsEditText);
        audioSystemRadioGroup = findViewById(R.id.audioSystemRadioGroup);
        submitSpecialBookingButton = findViewById(R.id.submitSpecialBookingButton);
        uploadPdfButton = findViewById(R.id.uploadPdfButton); // PDF upload button

        // Display selected hall
        selectedHallTextView.setText("Selected Hall: " + selectedHall);

        // Display all selected time slots
        if (selectedTimeSlots != null && !selectedTimeSlots.isEmpty()) {
            selectedTimeSlotsTextView.setText("Selected Time Slots: " + String.join(", ", selectedTimeSlots));
        } else {
            selectedTimeSlotsTextView.setText("No Time Slots Selected");
        }

        // Set up the PDF upload button click listener
        uploadPdfButton.setOnClickListener(v -> selectPdf());

        submitSpecialBookingButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString();
            String department = departmentEditText.getText().toString();
            String purpose = purposeEditText.getText().toString();
            String chairs = chairsEditText.getText().toString();
            int numberOfChairs = Integer.parseInt(chairs); // Convert String to int
            boolean audioSystem = audioSystemRadioGroup.getCheckedRadioButtonId() == R.id.radioYes;

            if (name.isEmpty() || department.isEmpty() || purpose.isEmpty() || chairs.isEmpty() || pdfUri == null) {
                Toast.makeText(SpecialBookingActivity.this, "Please fill all fields and upload a PDF", Toast.LENGTH_SHORT).show();
            } else {
                uploadPdfToFirebase(name, department, purpose, numberOfChairs, audioSystem);
            }
        });
    }

    // Method to select a PDF file from the device
    private void selectPdf() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Select PDF"), PICK_PDF_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_PDF_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            pdfUri = data.getData(); // Get the selected PDF URI
            Toast.makeText(this, "PDF selected: " + pdfUri.toString(), Toast.LENGTH_SHORT).show(); // Check Uri
        } else {
            Toast.makeText(this, "No PDF selected", Toast.LENGTH_SHORT).show();
        }
    }


    // Method to upload the PDF and save other booking details
    // Method to upload the PDF and save other booking details
    // Method to upload the PDF and save other booking details
    // Method to upload the PDF and save other booking details
    private void uploadPdfToFirebase(String name, String department, String purpose, int numberOfChairs, boolean audioSystem) {
        String userEmail = username; // Assuming username is part of the email
        // Replace special characters in email (e.g., '@' with '-')
        String sanitizedEmail = userEmail;
        String sanitizedDate = selectedDate.replace("/", "-").replace(" ", "_");

        // Handle empty or null time slot
        String timeSlot = selectedTimeSlots != null && !selectedTimeSlots.isEmpty() ? selectedTimeSlots.get(0) : "no-time-slot";

        // Create a nested folder structure: <email>/<date>/<hall>/<timeslot>
        StorageReference storageRef = storage.getReference()
                .child(sanitizedEmail)  // <email>
                .child(sanitizedDate)    // <date>
                .child(selectedHall)    // <hall>
                .child(timeSlot)        // <timeslot>
                .child("permission_letter.pdf");  // The actual file path

        // Upload PDF to Firebase Storage
        storageRef.putFile(pdfUri)
                .addOnSuccessListener(taskSnapshot -> {
                    Toast.makeText(SpecialBookingActivity.this, "PDF uploaded", Toast.LENGTH_SHORT).show();

                    // Get PDF URL and save the booking details to Firestore
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        saveSpecialBooking(name, department, purpose, numberOfChairs, audioSystem, uri.toString());
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(SpecialBookingActivity.this, "Failed to upload PDF: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }


    private void saveSpecialBooking(String name, String department, String purpose, int numberOfChairs, boolean audioSystem, String pdfUrl) {
        // Save to Firestore
        db.collection("bookings")
                .add(new SpecialBookingRequest(name, department, purpose, selectedDate, selectedHall, selectedTimeSlots, "pending", numberOfChairs, audioSystem, username, pdfUrl))
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(SpecialBookingActivity.this, "Special Booking request sent", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(SpecialBookingActivity.this, "Failed to send booking request", Toast.LENGTH_SHORT).show();
                });
    }
}
