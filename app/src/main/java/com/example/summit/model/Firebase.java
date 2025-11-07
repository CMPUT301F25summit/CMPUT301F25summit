package com.example.summit.model;

import android.util.Log;

import com.example.summit.interfaces.DeleteCallback;
import com.example.summit.interfaces.EventLoadCallback;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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

    /**
     * Saves or updates an {@link Admin} object in the 'admins' collection in Firestore.
     * <p>
     * The admin's device ID is used as the unique document ID in Firestore.
     * Logs success or failure to the console.
     *
     * @param admin The Admin object to be saved.
     */
    public static void saveAdmin(Admin admin){
        db.collection("admins")
                .document(admin.getDeviceId())
                .set(admin)
                .addOnSuccessListener(a -> Log.d("Firebase", "Admin saved"))
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

    public static void deleteOrganizer(Organizer organizer) {
        db.collection("organizers")
                .document(organizer.getDeviceId())
                .delete()
                .addOnSuccessListener(a -> Log.d("Firebase", "Organizer deleted"))
                .addOnFailureListener(e -> Log.e("Firebase", "Error: " + e));

    }

    public static void deleteEvent(Event event) {
        db.collection("events")
                .document(event.getId())
                .delete()
                .addOnSuccessListener(a -> Log.d("Firebase", "Event deleted"))
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
                    List<Event> result = new ArrayList<>();
                    for (com.google.firebase.firestore.DocumentSnapshot doc : query.getDocuments()) {
                        EventDescription desc = doc.toObject(EventDescription.class);
                        if (desc != null) {

                            Event event = new Event(desc);
                            event.setId(doc.getId());   // ✅ attach Firestore ID
                            result.add(event);

                        }
                    }
                    callback.onEventsLoaded(result);
                })
                .addOnFailureListener(e ->
                        Log.e("Firebase", "Error loading events: " + e));
    }

    /**
     * Deletes a single event and its associated QR code from Firestore.
     * <p>
     * This method deletes the event document from the 'events' collection
     * and the corresponding QR code from the 'qrcodes' collection.
     * Uses the provided {@link DeleteCallback} to notify success or failure.
     *
     * @param eventId The ID of the event to delete.
     * @param callback The callback to handle success or failure.
     */
    public static void deleteEvent(String eventId, DeleteCallback callback) {
        List<String> singleEvent = new ArrayList<>();
        singleEvent.add(eventId);
        deleteEvents(singleEvent, callback);
    }

    /**
     * Deletes multiple events and their associated QR codes from Firestore.
     * <p>
     * Iterates through the list of event IDs and deletes each event
     * document from the 'events' collection and its corresponding QR code from
     * the 'qrcodes' collection. It tracks the deletion progress and invokes
     * the callback when all deletions are complete or if an error occurs.
     *
     * @param eventIds The list of event IDs to delete.
     * @param callback The callback to handle success or failure.
     */
    public static void deleteEvents(List<String> eventIds, DeleteCallback callback) {
        if (eventIds == null || eventIds.isEmpty()) {
            callback.onDeleteFailure("No events to delete");
            return;
        }

        for (String eventId : eventIds) {
            // Delete event document
            db.collection("events")
                    .document(eventId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Log.d("Firebase", "Event deleted: " + eventId);
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firebase", "Error deleting event: " + eventId, e);
                    });

            // Delete associated QR code
            db.collection("qrcodes")
                    .document(eventId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Log.d("Firebase", "QR code deleted: " + eventId);
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firebase", "Error deleting QR code: " + eventId, e);
                    });
        }
    }

}
