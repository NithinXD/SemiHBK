package com.example.emptyactivity;

import android.net.Uri;

import java.util.List;

public class SpecialBookingRequest {
    private String name;
    private String department;
    private String purpose;
    private String date;
    private String hall;
    private List<String> timeSlots; // Updated to List
    private String status;
    private int numberOfChairs;
    private boolean audioSystem;
    private String userEmail;
    private String pdfUri;

    public SpecialBookingRequest(String name, String department, String purpose, String date, String hall, List<String> timeSlots, String status, int numberOfChairs, boolean audioSystem, String userEmail, String pdfUri) {
        this.name = name;
        this.department = department;
        this.purpose = purpose;
        this.date = date;
        this.hall = hall;
        this.timeSlots = timeSlots;
        this.status = status;
        this.numberOfChairs = numberOfChairs;
        this.audioSystem = audioSystem;
        this.userEmail = userEmail;
        this.pdfUri = pdfUri;
    }

    // Getter methods for Firestore
    public String getName() {
        return name;
    }

    public String getDepartment() {
        return department;
    }

    public String getPurpose() {
        return purpose;
    }

    public String getDate() {
        return date;
    }

    public String getHall() {
        return hall;
    }

    public List<String> getTimeSlots() {
        return timeSlots;
    }

    public String getStatus() {
        return status;
    }

    public int getNumberOfChairs() {
        return numberOfChairs;
    }

    public boolean isAudioSystem() {
        return audioSystem;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getPdfUri() {
        return pdfUri;
    }
}
