package com.example.emptyactivity;

import java.util.List;

public class BookingRequest {
    private String name;
    private String department;
    private String purpose;
    private String date;
    private String hall;
    private List<String> timeSlots; // Changed to List of time slots
    private String status;
    private String userEmail;

    // Constructor to handle multiple time slots
    public BookingRequest(String name, String department, String purpose, String date, String hall, List<String> timeSlots, String status, String userEmail) {
        this.name = name;
        this.department = department;
        this.purpose = purpose;
        this.date = date;
        this.hall = hall;
        this.timeSlots = timeSlots; // Set the list of time slots
        this.status = status;
        this.userEmail = userEmail;
    }

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

    public List<String> getTimeSlots() { // Get the list of time slots
        return timeSlots;
    }

    public String getStatus() {
        return status;
    }

    public String getUserEmail() {
        return userEmail;
    }
}
