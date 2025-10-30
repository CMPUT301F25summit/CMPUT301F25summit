package com.example.summit.model;

public class SignUp {

    public SignUp() {
    }

    public void joinWaitingList(Entrant entrant, WaitingList list) {
        list.addEntrant(entrant);
        //TODO : push to firebase
    }

    public void leaveWaitingList(Entrant entrant, WaitingList list) {
        list.removeEntrant(entrant);
    }

    public void acceptInvitation(Entrant entrant) {
        entrant.acceptInvitation();
    }

    public boolean joinEvent(Entrant entrant, WaitingList list) {
        if (!list.getEntrants().contains(entrant)) {
            list.addEntrant(entrant);
            return true;
        }
        else return false;
    }

    public boolean leaveEvent(Entrant entrant, WaitingList list) {
        if (list.getEntrants().contains(entrant)) {
            list.removeEntrant(entrant);
            return true;
        }
        else return false;
    }

}
