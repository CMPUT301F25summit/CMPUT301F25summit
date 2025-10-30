package com.example.summit.model;

import java.io.Serializable;

public abstract class User implements Serializable {
    protected String id;
    protected String name;
    protected String email;
    protected String deviceId;

    public User(String id, String name, String email, String deviceId) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.deviceId = deviceId;
    }

    public User() {} // overloading for firebase implementation later ;)

    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getDeviceId() { return deviceId; }

    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }

    public abstract String getRole(); //for role specific behavior

}
