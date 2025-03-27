package com.example.myapplication.models;

public class Book {
    private String userId;
    private String carId;
    private String carTitle;
    private String fullName;
    private String phone;
    private String email;
    private String selectedDealer;
    private String bookingDate;

    public Book() {}

    public Book(String userId, String carId, String carTitle, String fullName, String phone, String email, String selectedDealer, String bookingDate) {
        this.userId = userId;
        this.carId = carId;
        this.carTitle = carTitle;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.selectedDealer = selectedDealer;
        this.bookingDate = bookingDate;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getCarId() { return carId; }
    public void setCarId(String carId) { this.carId = carId; }

    public String getCarTitle() { return carTitle; }
    public void setCarTitle(String carTitle) { this.carTitle = carTitle; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSelectedDealer() { return selectedDealer; }
    public void setSelectedDealer(String selectedDealer) { this.selectedDealer = selectedDealer; }

    public String getBookingDate() { return bookingDate; }
    public void setBookingDate(String bookingDate) { this.bookingDate = bookingDate; }
}
