package com.example.emptyactivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.content.Intent;
import android.widget.Button;
import android.os.CountDownTimer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class Help extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_SEND_SMS = 1;
    private static final long ONE_MINUTE_IN_MILLIS = 60000;
    private EditText phonenumber, message;
    private Button send, scheduleButton;
    private ImageButton imgButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        send = findViewById(R.id.button3);
        phonenumber = findViewById(R.id.editTextTextPersonName);
        message = findViewById(R.id.editTextTextPersonName2);
        final EditText messageEditText = findViewById(R.id.editTextTextPersonName3);
        scheduleButton = findViewById(R.id.timerbutton);
        Button button13 = findViewById(R.id.button13);
        imgButton=findViewById(R.id.imageButton);

        send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String numbers = phonenumber.getText().toString().trim(); // Remove leading/trailing whitespaces
                String msg = message.getText().toString();

                if (checkPermission()) {
                    if (!numbers.isEmpty() && !msg.isEmpty()) {
                        sendSMS(splitNumbers(numbers), msg); // Send to multiple recipients
                    } else {
                        Toast.makeText(getApplicationContext(), "Phone number or message is empty", Toast.LENGTH_LONG).show();
                    }
                } else {
                    requestPermission();
                }
            }
        });

        imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Help.this, map.class);
                startActivity(intent);
            }
        });

        button13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String m = messageEditText.getText().toString();
                sendMessage(m);
            }
        });

        scheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Store phone numbers and message for later use
                final String numbers = phonenumber.getText().toString().trim();
                final String msg = message.getText().toString();

                new CountDownTimer(ONE_MINUTE_IN_MILLIS, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        // Update UI if needed (optional)
                    }

                    @Override
                    public void onFinish() {
                        try {
                            if (checkPermission()) {
                                sendSMS(splitNumbers(numbers), msg); // Attempt to send SMS
                                // Display a success toast even if sending might not be immediate
                                Toast.makeText(getApplicationContext(), "Message sent!", Toast.LENGTH_LONG).show();
                            } else {
                                requestPermission();
                            }
                        } catch (Exception e) {
                            // Handle potential errors (e.g., permission issues, SMS sending failure)
                            Toast.makeText(getApplicationContext(), "Failed to send message", Toast.LENGTH_LONG).show();
                        }
                    }
                }.start();
            }
        });


    }



    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, PERMISSION_REQUEST_SEND_SMS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_SEND_SMS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendMessage(String m) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, m);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            // If no apps can handle the intent, show a toast message
            Toast.makeText(this, "No app can handle this action.", Toast.LENGTH_SHORT).show();
        }
    }


    private void sendSMS(String[] phoneNumbers, String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            for (String number : phoneNumbers) {
                smsManager.sendTextMessage(number, null, message, null, null);
            }
            Toast.makeText(getApplicationContext(), "Message Sent", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Failed to send message", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private String[] splitNumbers(String numbers) {
        // Splits comma-separated phone numbers into an array
        return numbers.split(",");
    }
}