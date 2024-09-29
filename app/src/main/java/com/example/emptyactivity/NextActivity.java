package com.example.emptyactivity;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.content.Intent;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.text.SimpleDateFormat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.ArrayList;

public class NextActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private EditText editTextDate;
    private List<String> selectedTimeSlots;
    private String selectedHall;
    private List<String> bookedTimeSlots;
    private List<String> pendingTimeSlots;
    private Button bookButton;
    private Calendar myCalendar;
    private ConstraintLayout frameLayout;
    private Button selectedButton; // To store the currently selected button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);

        // Initialize the selectedTimeSlots list
        selectedTimeSlots = new ArrayList<>();  // Initialize the list here

        Intent intent = getIntent();
        selectedHall = intent.getStringExtra("SELECTED_HALL");  //Nithin Hall
        TextView textViewHall = findViewById(R.id.textViewHall);
        textViewHall.setText("Selected Hall: " + selectedHall);
        editTextDate = findViewById(R.id.editTextDate);
        myCalendar = Calendar.getInstance();
        frameLayout = findViewById(R.id.frameLayout);
        frameLayout.setVisibility(View.GONE); // Initially hide the frameLayout
        bookedTimeSlots = new ArrayList<>();
        pendingTimeSlots = new ArrayList<>();

        db = FirebaseFirestore.getInstance();

        bookButton = findViewById(R.id.button); // Initialize bookButton

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDate();
                frameLayout.setVisibility(View.VISIBLE); // Show frameLayout after selecting date
                checkBookedTimeSlots(); // Check slots when date is selected
            }
        };

        editTextDate.setOnClickListener(v -> new DatePickerDialog(NextActivity.this, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show());

        // Set click listener for all buttons inside frameLayout
        // Set click listener for all buttons inside frameLayout
        for (int i = 4; i <= 12; i++) {
            int buttonId = getResources().getIdentifier("button" + i, "id", getPackageName());
            Button but = findViewById(buttonId);
            but.setTag("available"); // Set initial tag to "available"
            but.setOnClickListener(v -> {
                String status = (String) v.getTag();
                if (status.equals("booked")) {
                    Toast.makeText(NextActivity.this, "This time slot is booked and cannot be selected", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (status.equals("pending")) {
                    Toast.makeText(NextActivity.this, "This time slot is pending and cannot be selected", Toast.LENGTH_SHORT).show();
                    return;
                }

                String selectedTime = ((Button) v).getText().toString();

                if (selectedTimeSlots.contains(selectedTime)) {
                    // If the time slot is already selected, unselect it
                    selectedTimeSlots.remove(selectedTime);
                    v.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark)); // Reset to available color
                    v.setTag("available");
                } else {
                    // Select the time slot
                    selectedTimeSlots.add(selectedTime);
                    v.setBackgroundColor(getResources().getColor(R.color.blue)); // Mark as selected
                    v.setTag("selected");
                }
            });
        }


        bookButton.setOnClickListener(v -> {
            if (!isDateEntered()) {
                Toast.makeText(NextActivity.this, "Please enter the date", Toast.LENGTH_SHORT).show();
            } else if (selectedTimeSlots.isEmpty()) {
                Toast.makeText(NextActivity.this, "Please select at least one time slot", Toast.LENGTH_SHORT).show();
            } else {
                // Check if any of the selected slots are booked or pending
                for (String timeSlot : selectedTimeSlots) {
                    if (isSlotPending(timeSlot) || isSlotBooked(timeSlot)) {
                        String statusMessage = isSlotPending(timeSlot) ? "pending" : "booked";
                        Toast.makeText(NextActivity.this, "Time slot " + timeSlot + " is " + statusMessage + ". Please choose another slot.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                // If all slots are available, navigate to booking activity
                navigateToBookingActivity();
            }
        });
    }

    private void updateDate() {
        String myFormat = "dd/MM/yyyy"; // Change date format as needed
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        editTextDate.setText(sdf.format(myCalendar.getTime()));
        resetButtons();
        selectedTimeSlots.clear(); // Clear the selected time slots when date is changed
    }

    private void checkBookedTimeSlots() {
        resetButtons();

        String selectedDate = editTextDate.getText().toString();
        String selectedHall = getIntent().getStringExtra("SELECTED_HALL");

        if (selectedDate.isEmpty()) {
            Toast.makeText(NextActivity.this, "Please select a date", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedHall == null) {
            Toast.makeText(NextActivity.this, "Selected hall is missing", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("bookings")
                .whereEqualTo("date", selectedDate)
                .whereEqualTo("hall", selectedHall)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        bookedTimeSlots.clear();
                        pendingTimeSlots.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Retrieve the list of time slots
                            List<String> timeSlotsArray = (List<String>) document.get("timeSlots");
                            String status = document.getString("status");

                            if (timeSlotsArray != null && status != null) {
                                for (String bookedTimeSlot : timeSlotsArray) {
                                    if (status.equals("approved")) {
                                        bookedTimeSlots.add(bookedTimeSlot);
                                    } else if (status.equals("pending")) {
                                        pendingTimeSlots.add(bookedTimeSlot);
                                    }

                                    // Update button color and tag for each time slot in the array
                                    int buttonId = getButtonIdFromTimeSlot(bookedTimeSlot);
                                    if (buttonId != -1) {
                                        Button button = findViewById(buttonId);
                                        if (button != null) {
                                            if (status.equals("approved")) {
                                                button.setBackgroundColor(Color.RED); // Approved bookings in red
                                                button.setTag("booked");
                                            } else if (status.equals("pending")) {
                                                button.setBackgroundColor(Color.YELLOW); // Pending bookings in yellow
                                                button.setTag("pending");
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        Toast.makeText(NextActivity.this, "Error getting booked time slots", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private int getButtonIdFromTimeSlot(String timeSlot) {
        // Updated to match the new time slot strings with AM/PM
        switch (timeSlot) {
            case "9.00 AM - 10.00 AM":
                return R.id.button4;
            case "10.00 AM - 11.00 AM":
                return R.id.button5;
            case "11.00 AM - 11.15 AM":
                return R.id.button6;
            case "11.15 AM - 12.15 PM":
                return R.id.button7;
            case "12.15 PM - 1.15 PM":
                return R.id.button8;
            case "1.15 PM - 2.15 PM":
                return R.id.button9;
            case "2.15 PM - 3.15 PM":
                return R.id.button10;
            case "3.15 PM - 4.15 PM":
                return R.id.button11;
            case "4.15 PM - 5.00 PM":
                return R.id.button12;
            default:
                return -1; // Invalid time slot
        }
    }


    private void resetButtons() {
        for (int i = 4; i <= 12; i++) {
            int buttonId = getResources().getIdentifier("button" + i, "id", getPackageName());
            Button but = findViewById(buttonId);
            but.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark)); // Reset to default color
            but.setTag("available"); // Reset tag to "available"
        }
    }

    private boolean isSlotBooked(String timeSlot) {
        return bookedTimeSlots.contains(timeSlot);
    }

    private boolean isSlotPending(String timeSlot) {
        return pendingTimeSlots.contains(timeSlot);
    }

    private boolean isDateEntered() {
        return !editTextDate.getText().toString().isEmpty();
    }

    private void navigateToBookingActivity() {
        Intent intent;
        if (selectedHall.equals("KK Hall") || selectedHall.equals("KS Hall")) {
            // Navigate to SpecialBookingActivity if the selected hall is "KK Hall" or "KS Hall"
            intent = new Intent(NextActivity.this, SpecialBookingActivity.class);
            // Put extras for SpecialBookingActivity
            intent.putExtra("username", getIntent().getStringExtra("username")); // Pass username
            intent.putExtra("selected_date", editTextDate.getText().toString());
            intent.putStringArrayListExtra("selected_time_slots", new ArrayList<>(selectedTimeSlots));
            intent.putExtra("selected_hall", selectedHall);
        } else {
            // Navigate to BookingActivity for all other halls
            intent = new Intent(NextActivity.this, BookingActivity.class);
            intent.putExtra("username", getIntent().getStringExtra("username")); // Pass username
            intent.putExtra("selected_date", editTextDate.getText().toString());
            intent.putStringArrayListExtra("selected_time_slots", new ArrayList<>(selectedTimeSlots));
            intent.putExtra("selected_hall", selectedHall);
        }

        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recheck booked time slots and clear selected slots when the activity is resumed
        checkBookedTimeSlots();
        selectedTimeSlots.clear(); // Clear the selected time slots when returning to this page
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
                startActivity(new Intent(this, Schedule.class));
                return true;
            case R.id.Aboutmenu:
                Toast.makeText(this, "View Bookings", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, AboutUsActivity.class));
                return true;
            case R.id.logoutmenu:
                Toast.makeText(this, "Logout page", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showExitConfirmationDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Alert");
        alertDialogBuilder.setIcon(R.drawable.ic_baseline_announcement_24);
        alertDialogBuilder.setMessage("Do you want to exit?");
        alertDialogBuilder.setCancelable(false);

        alertDialogBuilder.setPositiveButton("Yes", (dialog, which) -> finishAffinity()); // Exit from the app

        alertDialogBuilder.setNegativeButton("No", (dialog, which) -> dialog.dismiss()); // Dismiss the dialog and remain on the same page

        // Create and show the alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
