package com.example.myapplication.models;

public class User {
    private String userId;
    private String username;
    private String email;
    private String avatar;
    private String phone;
    private String address;

    public User(){};

    public User(String userId, String username, String email, String avatar, String phone, String address){
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.avatar = avatar;
        this.phone = phone;
        this.address = address;
    }

    public String getUserId() {return userId;}
    public void setUserId(String userId) { this.userId = userId;}

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username;}

    public String getEmail() { return email;}
    public void setEmail(String email) { this.email = email;}

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address;}
}
