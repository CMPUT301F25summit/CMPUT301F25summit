package com.example.summit.model;

import android.util.Log;

import com.example.summit.interfaces.EventLoadCallback;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;


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
        db.collection("organizers")
                .document(organizer.getDeviceId())
                .set(organizer)
                .addOnSuccessListener(a -> Log.d("Firebase", "Entrant saved"))
                .addOnFailureListener(e -> Log.e("Firebase", "Error: " + e));
    }
    public static void updateWaitingList(WaitingList list) {}
    public void saveEvent(Event event){}


    // loads details for the profile fragment of entrant
    public static void loadEntrantDetails(Entrant entrant) {
        db.collection("entrants")
                .document(entrant.getDeviceId())
                .get()
                .addOnSuccessListener(a -> Log.d("Firebase", "Entrant Loaded"))
                .addOnFailureListener(e -> Log.e("Firebase", "Error: " + e));
    }

    // delete an entrant from the database
    public static void deleteEntrant(Entrant entrant) {
        db.collection("entrants")
                .document(entrant.getDeviceId())
                .delete()
                .addOnSuccessListener(a -> Log.d("Firebase", "Entrant deleted"))
                .addOnFailureListener(e -> Log.e("Firebase", "Error: " + e));

    }

    public static void loadEvents(EventLoadCallback callback) {
        db.collection("events")
                .get()
                .addOnSuccessListener(query -> {
                    List<EventDescription> result = new ArrayList<>();
                    for (com.google.firebase.firestore.DocumentSnapshot doc : query.getDocuments()) {
                        EventDescription desc = doc.toObject(EventDescription.class);
                        if (desc != null) result.add(desc);
                    }
                    callback.onEventsLoaded(result);
                })
                .addOnFailureListener(e ->
                        Log.e("Firebase", "Error loading events: " + e));
    }

}
