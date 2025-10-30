package com.example.summit.model;

public class Entrant extends User {
    private boolean notificationsEnabled;
    private Boolean invitationAccepted;
    public Entrant (String id, String name, String email, String deviceId) {
        super (id, name, email, deviceId);
        this.notificationsEnabled = true; //set to true since entrant allowed to opt-out
    }
    public Entrant() {} // TODO: overload for firebase

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

    public void acceptInvitation() {    /*TODO : let entrant accept invitation*/
        this.invitationAccepted = true;
    }
    public  void declineInvitation() {  /*TODO : let entrant accept invitation*/
        this.invitationAccepted = false;
    }

    public Boolean getInvitationStatus() {
        return invitationAccepted;
    }

}
