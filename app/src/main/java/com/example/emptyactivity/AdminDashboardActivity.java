package com.example.emptyactivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class AdminDashboardActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private LinearLayout bookingsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        db = FirebaseFirestore.getInstance();
        bookingsLayout = findViewById(R.id.bookingsLayout);

        // Fetch pending bookings
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
                            String timeSlot = document.getString("timeSlot");
                            String name = document.getString("name");
                            String department = document.getString("department");
                            String purpose = document.getString("purpose");

                            View bookingView = getLayoutInflater().inflate(R.layout.booking_item, null);
                            TextView bookingDetailsTextView = bookingView.findViewById(R.id.bookingDetailsTextView);
                            Button approveButton = bookingView.findViewById(R.id.approveButton);
                            Button rejectButton = bookingView.findViewById(R.id.rejectButton);

                            bookingDetailsTextView.setText("Date: " + date + "\nTime: " + timeSlot + "\nName: " + name +
                                    "\nDepartment: " + department + "\nPurpose: " + purpose);

                            approveButton.setOnClickListener(v -> updateBookingStatus(id, "approved"));
                            rejectButton.setOnClickListener(v -> updateBookingStatus(id, "rejected"));

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
        db.collection("bookings")
                .document(bookingId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String userEmail = documentSnapshot.getString("userEmail");
                        if (userEmail != null) {
                            // Here you would send a notification or an email to the user
                            // For simplicity, let's just show a Toast
                            String message = "Booking " + status + ": " + userEmail;
                            Toast.makeText(AdminDashboardActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}