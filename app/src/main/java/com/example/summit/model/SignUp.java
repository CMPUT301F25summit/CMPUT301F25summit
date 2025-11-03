package com.example.summit.model;

import com.google.firebase.firestore.FirebaseFirestore;

public class SignUp {


    public SignUp() {
    }

    public void joinWaitingList(Entrant entrant, WaitingList list) {
        list.addEntrant(entrant);
        Firebase.saveEntrant(entrant); //stub
        Firebase.updateWaitingList(list);
        //TODO : push to firebase
    }

    public void leaveWaitingList(Entrant entrant, WaitingList list) {
        list.removeEntrant(entrant);
        list.removeEntrant(entrant);
        Firebase.updateWaitingList(list);
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

    public void joinEventFirestore(Entrant entrant, String eventId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("events")
                .document(eventId)
                .collection("waitingList")
                .document(entrant.getDeviceId())
                .set(entrant)
                .addOnSuccessListener(aVoid -> {
                    System.out.println("Entrant added to waiting list!");
                })
                .addOnFailureListener(e -> {
                    System.out.println("Failed to add entrant: " + e.getMessage());
                });
    }



    public boolean leaveEvent(Entrant entrant, WaitingList list) {
        if (list.getEntrants().contains(entrant)) {
            list.removeEntrant(entrant);
            return true;
        }
        else return false;
    }

}
