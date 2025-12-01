package com.example.summit.model;

import android.location.Location;

import com.google.firebase.firestore.GeoPoint;

public class Entrant extends User {
    private boolean notificationsEnabled;
    private Boolean isLocationShared;
    private GeoPoint location;
    private String city;
    private Boolean invitationAccepted;

    public Entrant (String name, String email, String deviceId, String phone) {
        super (name, email, deviceId, phone);
        this.notificationsEnabled = true; //set to true since entrant allowed to opt-out
        this.isLocationShared = false;
    }
    public Entrant() {} // TODO: overload for firebase

    public Entrant(String deviceId) { //overload for lottery
        this.deviceId = deviceId;
        this.name = "Unknown";
        this.email = "";
        this.phone = "";
        this.city = "";
        this.isLocationShared = false;
    }

    @Override
    public String getRole() {
        return "Entrant";
    }

    //entrant specific methods below
    public void setLocation(GeoPoint location) {
        this.location = location;
    }
    public GeoPoint getLocation() {
        return this.location;
    }

    public void setLocationShared(Boolean isShared) {
        this.isLocationShared = isShared;
    }
    public Boolean getLocationShared() {
        return this.isLocationShared;
    }
    public boolean isNotificationsEnabled() {
        return(notificationsEnabled);
    }
    public void optInNotifications() {
        notificationsEnabled = true;
    }
    public void optOutNotifications() {
        notificationsEnabled = false;
    }

    public void acceptInvitation() {
        /*TODO : let entrant accept invitation*/
        this.invitationAccepted = true;
    }
    public  void declineInvitation() {
        /*TODO : let entrant accept invitation*/
        this.invitationAccepted = false;
    }

    public Boolean getInvitationStatus() {
        return invitationAccepted;
    }
    public String getCity() {
        return this.city;
    }
    public void setCity(String city) {
        this.city = city;
    }


}
