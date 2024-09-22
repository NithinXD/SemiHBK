package com.example.emptyactivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import androidx.annotation.NonNull;
import android.util.Log;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;


import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private boolean isAdminMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Find views by ID
        EditText editTextUsername = findViewById(R.id.editTextUsername);
        EditText editTextPassword = findViewById(R.id.editTextPassword);
        Switch switchLoginMode = findViewById(R.id.switchLoginMode);
        TextView textViewRegister = findViewById(R.id.textViewRegister);
        TextView textViewForgotPassword = findViewById(R.id.textViewForgotPassword);
        textViewForgotPassword.setOnClickListener(v -> {
            // Show forgot password dialog
            showForgotPasswordDialog();
        });
        // Listen to switch changes
        switchLoginMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isAdminMode = isChecked;
            if (isAdminMode) {
                editTextUsername.setHint(getString(R.string.hint_admin_username));
                editTextPassword.setHint(getString(R.string.hint_admin_password));
                Toast.makeText(this, "Admin Mode", Toast.LENGTH_SHORT).show();
            } else {
                editTextUsername.setHint(getString(R.string.hint_username));
                editTextPassword.setHint(getString(R.string.hint_password));
                Toast.makeText(this, "User Mode", Toast.LENGTH_SHORT).show();
            }
        });

        // Set a spannable string to change the color of "Register here" text
        String fullText = "Don't have an Account? Register here";
        SpannableString spannableString = new SpannableString(fullText);

        // Define the range for the "Register here" text
        int start = fullText.indexOf("Register here");
        int end = start + "Register here".length();

        // Apply color to "Register here"
        spannableString.setSpan(new ForegroundColorSpan(Color.BLUE), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Apply clickable span to "Register here"
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                // Navigate to RegisterActivity
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            }
        }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Set the spannable string to the TextView
        textViewRegister.setText(spannableString);
        textViewRegister.setMovementMethod(LinkMovementMethod.getInstance());

        // Set a click listener for the login button
        findViewById(R.id.buttonLogin).setOnClickListener(view -> {
            String username = editTextUsername.getText().toString();
            String password = editTextPassword.getText().toString();

            if (isAdminMode) {
                handleAdminLogin(username, password);
            } else {
                handleUserLogin(username, password);
            }
        });
    }
    // Method to show Forgot Password dialog
    private void showForgotPasswordDialog() {
        // Create an EditText to get the email
        final EditText resetEmail = new EditText(this);
        resetEmail.setHint("Enter your email");

        // Create a dialog
        new AlertDialog.Builder(this)
                .setTitle("Forgot Password")
                .setMessage("Enter your email to receive a password reset link.")
                .setView(resetEmail)
                .setPositiveButton("Send", (dialog, which) -> {
                    // Get the email entered by the user
                    String email = resetEmail.getText().toString().trim();
                    if (email.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Please enter an email address", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // Send password reset email
                    sendPasswordResetEmail(email);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    // Method to send password reset email using Firebase
    private void sendPasswordResetEmail(String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Password reset link sent to " + email, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Failed to send reset link: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void handleUserLogin(String username, String password) {
        Log.d("UserLogin", "Username: " + username);
        mAuth.signInWithEmailAndPassword(username, password).addOnSuccessListener(authResult -> {
            Intent intent = new Intent(MainActivity.this, HallSelectionActivity.class);
            intent.putExtra("username", username); // Add the username as an extra
            startActivity(intent);
        }).addOnFailureListener(e -> {
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void handleAdminLogin(String username, String password) {
            if (username.equals("marit@tce.edu") && password.equals("akila@123")) {
            mAuth.signInWithEmailAndPassword(username, password)
                    .addOnSuccessListener(authResult -> {
                        startActivity(new Intent(MainActivity.this, AdminDashboardActivity.class));
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(MainActivity.this, "Login Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(MainActivity.this, "Invalid admin credentials", Toast.LENGTH_SHORT).show();
        }
    }
}
