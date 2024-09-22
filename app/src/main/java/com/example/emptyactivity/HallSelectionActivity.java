package com.example.emptyactivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class HallSelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hall_selection);

        // Reference buttons from the XML layout
        Button buttonKKHall = findViewById(R.id.buttonKKHall);
        Button buttonKSHall = findViewById(R.id.buttonKSHall);
        Button buttonHall1 = findViewById(R.id.buttonHall1);
        Button buttonHall2 = findViewById(R.id.buttonHall2);
        Button buttonHall3 = findViewById(R.id.buttonHall3);
        Button buttonHall4 = findViewById(R.id.buttonHall4);
        Button buttonHall5 = findViewById(R.id.buttonHall5);
        Button buttonHall6 = findViewById(R.id.buttonHall6);


        // Set click listeners for each button
        buttonKKHall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToNextActivity("KK Hall");
            }
        });

        buttonKSHall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToNextActivity("KS Hall");
            }
        });

        buttonHall1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToNextActivity("IT Seminar Hall");
            }
        });

        buttonHall2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToNextActivity("CSE Seminar Hall");
            }
        });

        buttonHall3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToNextActivity("ECE Seminar Hall");
            }
        });

        buttonHall4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToNextActivity("EEE Seminar Hall");
            }
        });

        buttonHall5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToNextActivity("Civil Seminar Hall");
            }
        });

        buttonHall6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToNextActivity("Mech Seminar Hall");
            }
        });
    }

    // Method to navigate to NextActivity with the selected hall name
    private void navigateToNextActivity(String hallName) {
        Intent intent = new Intent(this, NextActivity.class);
        String username = getIntent().getStringExtra("username");
        intent.putExtra("username", username);
        intent.putExtra("SELECTED_HALL", hallName); // Pass hall name to NextActivity
        startActivity(intent);
    }
}
