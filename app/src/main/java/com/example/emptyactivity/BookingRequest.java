package com.example.emptyactivity;

public class BookingRequest {
    private String name;
    private String department;
    private String purpose;
    private String date;
    private String timeSlot;
    private String status;
    private String userEmail;

    public BookingRequest(String name, String department, String purpose, String date, String timeSlot, String status, String userEmail) {
        this.name = name;
        this.department = department;
        this.purpose = purpose;
        this.date = date;
        this.timeSlot = timeSlot;
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

    public String getTimeSlot() {
        return timeSlot;
    }

    public String getStatus() {
        return status;
    }

    public String getUserEmail() {
        return userEmail;
    }

    
}
