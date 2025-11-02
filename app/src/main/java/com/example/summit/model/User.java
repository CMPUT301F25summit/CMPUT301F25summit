package com.example.summit.model;

import java.io.Serializable;

public abstract class User implements Serializable {
 //   protected String id; deprecated since users identified by deviceId
    protected String name;
    protected String email;
    protected String deviceId;
    protected String phone;

    public User(String name, String email, String deviceId, String phone) {
       // this.id = id; deprecated
        this.name = name;
        this.email = email;
        this.deviceId = deviceId;
        this.phone = phone;
    }

    public User() {} // overloading for firebase implementation later ;)

   // public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getDeviceId() { return deviceId; }

    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() {return phone;}
    public void setPhone(String phone) {this.phone = phone;}

    public abstract String getRole(); //for role specific behavior

}
