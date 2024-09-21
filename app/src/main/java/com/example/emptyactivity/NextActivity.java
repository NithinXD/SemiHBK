package com.example.emptyactivity;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.content.Intent;
import android.widget.EditText;
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
    private String selectedTime;
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
        for (int i = 4; i <= 12; i++) {
            int buttonId = getResources().getIdentifier("button" + i, "id", getPackageName());
            Button but = findViewById(buttonId);
            but.setOnClickListener(v -> {
                if (selectedButton != null) {
                    selectedButton.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark)); // Reset previous selection
                }
                v.setBackgroundColor(getResources().getColor(R.color.blue)); // Set clicked button to blue
                selectedButton = (Button) v;
                selectedTime = ((Button) v).getText().toString();
            });
        }

        bookButton.setOnClickListener(v -> {
            if (!isDateEntered()) {
                Toast.makeText(NextActivity.this, "Please enter the date", Toast.LENGTH_SHORT).show();
            } else if (selectedTime == null) {
                Toast.makeText(NextActivity.this, "Please select a time slot", Toast.LENGTH_SHORT).show();
            } else if (isSlotPending(selectedTime) || isSlotBooked(selectedTime)) {
                String statusMessage = isSlotPending(selectedTime) ? "pending" : "booked";
                Toast.makeText(NextActivity.this, "This slot is " + statusMessage + ". Please choose another slot.", Toast.LENGTH_SHORT).show();
            } else {
                navigateToBookingActivity();
            }
        });
    }

    @Override
    public void onBackPressed() {
        showExitConfirmationDialog();
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

    @Override
    protected void onResume() {
        super.onResume();
        // Recheck booked time slots when the activity is resumed
        checkBookedTimeSlots();
    }


    private void updateDate() {
        String myFormat = "dd/MM/yyyy"; // Change date format as needed
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        editTextDate.setText(sdf.format(myCalendar.getTime()));
        resetButtons();
    }

    private void checkBookedTimeSlots() {
        // Reset button colors to default initially
        resetButtons();

        String selectedDate = editTextDate.getText().toString();

        if (selectedDate.isEmpty()) {
            Toast.makeText(NextActivity.this, "Please select a date", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("bookings")
                .whereEqualTo("date", selectedDate)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        bookedTimeSlots.clear();
                        pendingTimeSlots.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String bookedTimeSlot = document.getString("timeSlot");
                            String status = document.getString("status");
                            if (bookedTimeSlot != null && status != null) {
                                if (status.equals("approved")) {
                                    bookedTimeSlots.add(bookedTimeSlot);
                                } else if (status.equals("pending")) {
                                    pendingTimeSlots.add(bookedTimeSlot);
                                }
                                // Correctly identify and update the button color
                                int buttonId = getButtonIdFromTimeSlot(bookedTimeSlot);
                                if (buttonId != -1) {
                                    Button button = findViewById(buttonId);
                                    if (button != null) {
                                        if (status.equals("approved")) {
                                            button.setBackgroundColor(Color.RED); // Approved bookings in red
                                        } else if (status.equals("pending")) {
                                            button.setBackgroundColor(Color.YELLOW); // Pending bookings in yellow
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
        // Assuming time slots are formatted as "9.00-10.00" and match button IDs from 4 to 12
        switch (timeSlot) {
            case "9.00-10.00": return R.id.button4;
            case "10.00-11.00": return R.id.button5;
            case "11.00-11.15": return R.id.button6;
            case "11.15-12.15": return R.id.button7;
            case "12.15-1.15": return R.id.button8;
            case "1.15-2.15": return R.id.button9;
            case "2.15-3.15": return R.id.button10;
            case "3.15-4.15": return R.id.button11;
            case "4.15-5.00": return R.id.button12;
            default: return -1; // Invalid time slot
        }
    }

    private void resetButtons() {
        for (int i = 4; i <= 12; i++) {
            int buttonId = getResources().getIdentifier("button" + i, "id", getPackageName());
            Button but = findViewById(buttonId);
            but.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark)); // Reset to default color
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
        Intent intent = new Intent(NextActivity.this, BookingActivity.class);
        intent.putExtra("selected_date", editTextDate.getText().toString());
        intent.putExtra("selected_time_slot", selectedTime);
        startActivity(intent);
    }
}
