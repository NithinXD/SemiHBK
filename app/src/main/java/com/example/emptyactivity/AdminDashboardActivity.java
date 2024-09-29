package com.example.emptyactivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminDashboardActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private LinearLayout bookingsLayout;
    private Spinner hallFilterSpinner;
    private String selectedHallFilter = "All"; // Default filter is "All" to show all halls

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        db = FirebaseFirestore.getInstance();
        bookingsLayout = findViewById(R.id.bookingsLayout);
        hallFilterSpinner = findViewById(R.id.hallFilterSpinner);

        // Set up Spinner with hall names
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.hall_names, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hallFilterSpinner.setAdapter(adapter);

        // Set listener to filter bookings based on selected hall
        hallFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedHallFilter = parent.getItemAtPosition(position).toString();
                fetchPendingBookings(); // Fetch bookings based on selected hall
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Fetch all pending bookings on start
        fetchPendingBookings();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.item_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Bookingmenu:
                Toast.makeText(this, "View Bookings", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, ScheduleAdmin.class));
                return true;

            case R.id.logoutmenu:
                Toast.makeText(this, "Logout page", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void fetchPendingBookings() {
        bookingsLayout.removeAllViews(); // Clear previous views

        db.collection("bookings")
                .whereEqualTo("status", "pending")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot value, FirebaseFirestoreException error) {
                        if (error != null) {
                            Toast.makeText(AdminDashboardActivity.this, "Failed to fetch bookings", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        bookingsLayout.removeAllViews(); // Clear previous views

                        for (QueryDocumentSnapshot document : value) {
                            String id = document.getId();
                            String date = document.getString("date");
                            List<String> timeSlots = (List<String>) document.get("timeSlots"); // Get timeSlots as a list
                            String name = document.getString("name");
                            String department = document.getString("department");
                            String purpose = document.getString("purpose");
                            String hall = document.getString("hall");
                            String userEmail = document.getString("userEmail");
                            Boolean audioSystem = document.getBoolean("audioSystem");
                            Long numberOfChairs = document.getLong("numberOfChairs"); // Using Long to handle Firestore numbers

                            // Skip bookings that do not match the selected hall, unless "All" is selected
                            if (!selectedHallFilter.equals("All") && !selectedHallFilter.equals(hall)) {
                                continue; // Skip this booking if it doesn't match the filter
                            }

                            // Format the time slots with bullet points and line breaks
                            StringBuilder timeSlotsFormatted = new StringBuilder();
                            if (timeSlots != null) {
                                for (String timeSlot : timeSlots) {
                                    timeSlotsFormatted.append("• ").append(timeSlot).append("\n"); // Add bullet points and line breaks
                                }
                            }

                            // Prepare booking details
                            StringBuilder bookingDetails = new StringBuilder();
                            bookingDetails.append("Date: ").append(date).append("\n");
                            bookingDetails.append("Time Slots:\n").append(timeSlotsFormatted.toString());
                            if (name != null)
                                bookingDetails.append("Name: ").append(name).append("\n");
                            if (department != null)
                                bookingDetails.append("Department: ").append(department).append("\n");
                            if (purpose != null)
                                bookingDetails.append("Purpose: ").append(purpose).append("\n");
                            if (hall != null)
                                bookingDetails.append("Hall: ").append(hall).append("\n");
                            if (numberOfChairs != null)
                                bookingDetails.append("Number of Chairs: ").append(numberOfChairs).append("\n");
                            if (audioSystem != null)
                                bookingDetails.append("Audio System: ").append(audioSystem ? "Yes" : "No").append("\n");
                            if (userEmail != null)
                                bookingDetails.append("User Email: ").append(userEmail).append("\n");

                            View bookingView = getLayoutInflater().inflate(R.layout.booking_item, null);
                            TextView bookingDetailsTextView = bookingView.findViewById(R.id.bookingDetailsTextView);
                            Button approveButton = bookingView.findViewById(R.id.approveButton);
                            Button rejectButton = bookingView.findViewById(R.id.rejectButton);

                            // Set the formatted text to the TextView
                            bookingDetailsTextView.setText(bookingDetails.toString());

                            // Set up approve and reject button click listeners
                            approveButton.setOnClickListener(v -> updateBookingStatus(id, "approved"));
                            rejectButton.setOnClickListener(v -> updateBookingStatus(id, "rejected"));

                            // Add the booking view to the layout
                            bookingsLayout.addView(bookingView);
                        }
                    }
                });
    }

    private void updateBookingStatus(String bookingId, String status) {
        DocumentReference bookingRef = db.collection("bookings").document(bookingId);

        Map<String, Object> updates = new HashMap<>();
        updates.put("status", status);

        bookingRef.update(updates)
                .addOnSuccessListener(aVoid -> {
                    String message = status.equals("approved") ? "Booking Approved" : "Booking Rejected";
                    Toast.makeText(AdminDashboardActivity.this, message, Toast.LENGTH_SHORT).show();
                    notifyUser(bookingId, status);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AdminDashboardActivity.this, "Failed to update booking", Toast.LENGTH_SHORT).show();
                });
    }

    private void notifyUser(String bookingId, String status) {
        // First, get the userEmail from the bookings collection using the bookingId
        db.collection("bookings")
                .document(bookingId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String userEmail = documentSnapshot.getString("userEmail");
                        String date = documentSnapshot.getString("date");
                        List<String> timeSlots = (List<String>) documentSnapshot.get("timeSlots");
                        String name = documentSnapshot.getString("name");
                        String department = documentSnapshot.getString("department");
                        String purpose = documentSnapshot.getString("purpose");
                        String hall = documentSnapshot.getString("hall");
                        Long numberOfChairs = documentSnapshot.getLong("numberOfChairs");
                        Boolean audioSystem = documentSnapshot.getBoolean("audioSystem");

                        if (userEmail != null) {
                            // Send email notification
                            StringBuilder emailBody = new StringBuilder();
                            emailBody.append("Booking ").append(status).append("!\n");
                            emailBody.append("Date: ").append(date).append("\n");
                            emailBody.append("Time Slots: ").append(timeSlots).append("\n");
                            emailBody.append("Name: ").append(name).append("\n");
                            emailBody.append("Department: ").append(department).append("\n");
                            emailBody.append("Purpose: ").append(purpose).append("\n");
                            emailBody.append("Hall: ").append(hall).append("\n");
                            if (numberOfChairs != null) {
                                emailBody.append("Number of Chairs: ").append(numberOfChairs).append("\n");
                            }
                            if (audioSystem != null) {
                                emailBody.append("Audio System: ").append(audioSystem ? "Yes" : "No").append("\n");
                            }
                            emailBody.append("Your booking has been ").append(status).append(".");

                            // Send email
                            sendEmail(userEmail, "Booking " + status, emailBody.toString());

                            // Display confirmation Toast
                            Toast.makeText(AdminDashboardActivity.this, "Email sent to " + userEmail, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(AdminDashboardActivity.this, "Email not found in booking document", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AdminDashboardActivity.this, "Failed to get booking details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Method to send email
    private void sendEmail(String recipientEmail, String subject, String body) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:")); // Only email apps should handle this
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{recipientEmail});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, body);

        try {
            startActivity(Intent.createChooser(emailIntent, "Send email using..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }
}
