package com.example.emptyactivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

public class ProgressActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        progressBar = findViewById(R.id.progressBar);

        // Set the initial progress to 0
        progressBar.setProgress(0);

        // Start the progress bar animation
        startProgressBar();
    }

    private void startProgressBar() {
        // Create a new thread to update the progress bar
        new Thread(() -> {
            // Loop through increasing the progress by 33% every second for approximately 3 seconds
            for (int progress = 0; progress <= 100; progress += 50) {
                final int finalProgress = progress; // Finalize progress for lambda
                try {
                    // Update the progress bar on the UI thread
                    handler.post(() -> progressBar.setProgress(finalProgress));
                    // Sleep for 1 second
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // After 3 seconds, start the MainActivity
            Intent intent = new Intent(ProgressActivity.this, MainActivity.class);
            startActivity(intent);

            // Finish the ProgressActivity
            finish();
        }).start();
    }
}
