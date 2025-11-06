package com.example.summit.model;


public class Admin extends User {
    public Admin(String name, String email, String deviceId, String phone) {
        super(name, email, deviceId, phone);
    }

    public Admin() {} // Empty constructor for Firebase

    @Override
    public String getRole() {
        return "Admin";
    }
}
