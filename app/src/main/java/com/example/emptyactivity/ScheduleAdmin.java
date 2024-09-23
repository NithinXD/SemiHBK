package com.example.emptyactivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ScheduleAdmin extends AppCompatActivity {

    private ListView listView;
    private listItemAdmin adapter;
    private EditText editTextDate;
    private Button searchButton;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_admin);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize views
        editTextDate = findViewById(R.id.editTextDate1);
        searchButton = findViewById(R.id.searchButton1);
        listView = findViewById(R.id.list_item_admin);

        // Set OnClickListener for the search button
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchBookings();
            }
        });

        // Set OnClickListener for the EditText to show date picker dialog
        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year1, month1, dayOfMonth1) -> {
                    String formattedDate = String.format("%02d/%02d/%04d", dayOfMonth1, month1 + 1, year1);
                    editTextDate.setText(formattedDate);
                },
                year, month, dayOfMonth);

        datePickerDialog.show();
    }

    private void searchBookings() {
        String selectedDate = editTextDate.getText().toString().trim();

        if (selectedDate.isEmpty()) {
            Toast.makeText(this, "Please select a date.", Toast.LENGTH_SHORT).show();
            return;
        }

        getBookingsFromDatabase(selectedDate, searchResults -> updateListView(searchResults));
    }

    private void getBookingsFromDatabase(String selectedDate, FirestoreCallback firestoreCallback) {
        List<ListItem> searchResults = new ArrayList<>();

        db.collection("bookings")
                .whereEqualTo("date", selectedDate)
                .whereEqualTo("status", "approved")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                Booking booking = document.toObject(Booking.class);
                                searchResults.add(new ListItem(
                                        R.drawable.ic_baseline_event_note_24,
                                        "SLOT: " + booking.getTimeSlots(),
                                        "Booked By: " + booking.getName() + " " + booking.getDepartment() + "\nHall: " + booking.getHall()

                                ));
                            }
                        } else {
                            Toast.makeText(ScheduleAdmin.this, "No accepted bookings found for this date.", Toast.LENGTH_SHORT).show();
                        }
                        firestoreCallback.onCallback(searchResults);
                    } else {
                        Toast.makeText(ScheduleAdmin.this, "Error getting bookings.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private interface FirestoreCallback {
        void onCallback(List<ListItem> searchResults);
    }

    private void updateListView(List<ListItem> searchResults) {
        if (adapter == null) {
            adapter = new listItemAdmin(this, searchResults);
            listView.setAdapter(adapter);
        } else {
            adapter.updateData(searchResults);
        }
    }
}
