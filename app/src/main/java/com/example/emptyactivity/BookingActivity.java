package com.example.emptyactivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class BookingActivity extends AppCompatActivity {

    private EditText nameEditText, departmentEditText, purposeEditText;
    private Button submitButton;
    private String selectedDate, selectedHall;
    private List<String> selectedTimeSlots; // List to hold multiple selected time slots
    private SQLiteDatabase database;

    private FirebaseFirestore db;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.booking);

        selectedHall = getIntent().getStringExtra("selected_hall");
        selectedDate = getIntent().getStringExtra("selected_date");
        selectedTimeSlots = getIntent().getStringArrayListExtra("selected_time_slots"); // Get the list of selected time slots
        userEmail = getIntent().getStringExtra("username");

        TextView titleTextView = findViewById(R.id.titleTextView);
        titleTextView.setText("Selected Hall: " + selectedHall +
                "\nSelected Date: " + selectedDate +
                "\nSelected Time Slots: " + String.join(", ", selectedTimeSlots));

        nameEditText = findViewById(R.id.nameEditText);
        departmentEditText = findViewById(R.id.departmentEditText);
        purposeEditText = findViewById(R.id.purposeEditText);
        submitButton = findViewById(R.id.submitButton);

        // Set default value for departmentEditText

        // Initialize database
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        database = dbHelper.getWritableDatabase();

        // Disable the submit button initially
        submitButton.setEnabled(false);

        // Add text change listener to enable/disable submit button based on input fields
        nameEditText.addTextChangedListener(textWatcher);
        departmentEditText.addTextChangedListener(textWatcher);
        purposeEditText.addTextChangedListener(textWatcher);

        submitButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString();
            String department = departmentEditText.getText().toString();
            String purpose = purposeEditText.getText().toString();

            db = FirebaseFirestore.getInstance();

            if (!name.isEmpty() && !department.isEmpty() && !purpose.isEmpty()) {
                insertData(selectedDate, selectedTimeSlots, name, department, purpose);

                // Save all the selected time slots in a single booking request
                db.collection("bookings").add(new BookingRequest(name, department, purpose, selectedDate, selectedHall, selectedTimeSlots, "pending", userEmail))
                        .addOnSuccessListener(documentReference -> {
                            Toast.makeText(BookingActivity.this, "Booking request sent", Toast.LENGTH_SHORT).show();
                            finish(); // Close the activity after the request is sent
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(BookingActivity.this, "Failed to send booking request", Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            // Enable submit button if all fields have values
            String name = nameEditText.getText().toString().trim();
            String department = departmentEditText.getText().toString().trim();
            String purpose = purposeEditText.getText().toString().trim();

            submitButton.setEnabled(!name.isEmpty() && !department.isEmpty() && !purpose.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    private void insertData(String date, List<String> timeSlots, String name, String department, String purpose) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.BookingEntry.COLUMN_DATE, date);
        values.put(DatabaseHelper.BookingEntry.COLUMN_TIME_SLOT, String.join(", ", timeSlots)); // Store time slots as a single string
        values.put(DatabaseHelper.BookingEntry.COLUMN_NAME, name);
        values.put(DatabaseHelper.BookingEntry.COLUMN_DEPARTMENT, department);
        values.put(DatabaseHelper.BookingEntry.COLUMN_PURPOSE, purpose);

        long newRowId = database.insert(DatabaseHelper.BookingEntry.TABLE_NAME, null, values);

        if (newRowId != -1) {
            Toast.makeText(this, "Booking request saved locally", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error saving request locally", Toast.LENGTH_SHORT).show();
        }
    }
}
