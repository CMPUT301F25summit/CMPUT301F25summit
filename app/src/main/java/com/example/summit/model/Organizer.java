package com.example.summit.model;

//complete implementation
public class Organizer extends User {

    public Organizer(String name, String email, String deviceId, String phone) {
        super(name, email, deviceId, phone);
    }
    public Organizer() { super();}

    @Override
    public String getRole() {
        return "Organizer";
    }
}
