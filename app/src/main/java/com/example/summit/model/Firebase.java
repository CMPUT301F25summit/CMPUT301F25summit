package com.example.summit.model;

import android.util.Log;

import com.example.summit.interfaces.EventLoadCallback;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

/**
 * A utility class for handling interactions with the Firebase Firestore database.
 * <p>
 * This class provides static methods for common database operations such as
 * saving users (Entrants, Organizers) and loading event data.
 * It encapsulates the {@link FirebaseFirestore} instance to provide a simple
 * API for the rest of the application.
 */
public class Firebase {
    /**
     * The single, static instance of the FirebaseFirestore database.
     */
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * Saves or updates an {@link Entrant} object in the 'entrants' collection in Firestore.
     * <p>
     * The entrant's device ID is used as the unique document ID in Firestore.
     * Logs success or failure to the console.
     *
     * @param entrant The Entrant object to be saved.
     */
    public static void saveEntrant(Entrant entrant) {
        db.collection("entrants")
                .document(entrant.getDeviceId())
                .set(entrant)
                .addOnSuccessListener(a -> Log.d("Firebase", "Entrant saved"))
                .addOnFailureListener(e -> Log.e("Firebase", "Error: " + e));
    }

    /**
     * Saves or updates an {@link Organizer} object in the 'organizers' collection in Firestore.
     * <p>
     * The organizer's device ID is used as the unique document ID in Firestore.
     * Logs success or failure to the console.
     *
     * @param organizer The Organizer object to be saved.
     */
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

    /**
     * Asynchronously loads all event descriptions from the 'events' collection in Firestore.
     * <p>
     * On a successful retrieval, it deserializes each document into an
     * {@link EventDescription} object and passes the resulting list to the
     * provided callback. On failure, it logs an error.
     *
     * @param callback The {@link EventLoadCallback} to be invoked with the list
     * of {@link EventDescription} objects on success, or to handle the error.
     */
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
