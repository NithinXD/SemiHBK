package com.example.emptyactivity;

import java.util.List;

public class Booking {
    private String date;
    private String name;
    private String department;
    private String purpose;
    private String status;
    private List<String> timeSlots;
    private String userEmail;
    private String hall;
    // Default constructor required for Firestore deserialization
    public Booking() {}

    // Constructor with parameters (optional, for manual creation)
    public Booking(String name, String department, String purpose, String date, List<String> timeSlots, String status, String userEmail, String hall) {
        this.name = name;
        this.department = department;
        this.purpose = purpose;
        this.date = date;
        this.timeSlots = timeSlots;
        this.status = status;
        this.userEmail = userEmail;
        this.hall = hall;
    }

    // Getter methods
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

    public String getUserEmail() {
        return userEmail;
    }

    // Setter methods (optional, for updating values)
    public void setName(String name) {
        this.name = name;
    }

    public void setHall(String hall) {
        this.hall = hall;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTimeSlot(List<String> timeSlots) {
        this.timeSlots = timeSlots;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
