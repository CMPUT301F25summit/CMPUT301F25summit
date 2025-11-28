package com.example.summit.model;

import android.location.Location;

public class Entrant extends User {
    private boolean notificationsEnabled;
    private Boolean invitationAccepted;
    private String location;
    public Entrant (String name, String email, String deviceId, String phone) {
        super (name, email, deviceId, phone);
        this.notificationsEnabled = true; //set to true since entrant allowed to opt-out
    }
    public Entrant() {} // TODO: overload for firebase

    public Entrant(String deviceId) { //overload for lottery
        this.deviceId = deviceId;
        this.name = "Unknown";
        this.email = "";
        this.phone = "";
        this.location = "";
    }

    @Override
    public String getRole() {
        return "Entrant";
    }

    //entrant specific methods below

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
        return this.location;
    }
    public void setCity(String city) {
        this.location = city;
    }


}
