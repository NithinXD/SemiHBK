package com.example.emptyactivity;

public class Booking {
    private String date;
    private String name;
    private String department;
    private String purpose;
    private String status;
    private String timeSlot;
    private String userEmail;

    // Default constructor required for Firestore deserialization
    public Booking() {}

    // Constructor with parameters (optional, for manual creation)
    public Booking(String name, String department, String purpose, String date, String timeSlot, String status, String userEmail) {
        this.name = name;
        this.department = department;
        this.purpose = purpose;
        this.date = date;
        this.timeSlot = timeSlot;
        this.status = status;
        this.userEmail = userEmail;
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

    public String getTimeSlot() {
        return timeSlot;
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

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
