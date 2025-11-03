package com.example.summit.model;

import android.util.Log;
import com.google.firebase.firestore.FirebaseFirestore;


public class Firebase {

    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static void saveEntrant(Entrant entrant) {
        db.collection("entrants")
                .document(entrant.getDeviceId())
                .set(entrant)
                .addOnSuccessListener(a -> Log.d("Firebase", "Entrant saved"))
                .addOnFailureListener(e -> Log.e("Firebase", "Error: " + e));
    }

    public static void saveOrganizer(Organizer organizer){
        db.collection("entrants")
                .document(organizer.getDeviceId())
                .set(organizer)
                .addOnSuccessListener(a -> Log.d("Firebase", "Entrant saved"))
                .addOnFailureListener(e -> Log.e("Firebase", "Error: " + e));
    }
    public static void updateWaitingList(WaitingList list) {}
    public void saveEvent(Event event){}
}
