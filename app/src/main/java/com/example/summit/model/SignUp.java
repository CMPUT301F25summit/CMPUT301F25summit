package com.example.summit.model;

import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Manages the business logic for event sign-ups, waiting lists, and invitations.
 * <p>
 * This class acts as a service or controller that coordinates actions between
 * {@link Entrant} objects, {@link WaitingList} objects, and the Firebase backend.
 * It handles the logic for joining, leaving, and accepting invitations.
 */
public class SignUp {

    public SignUp() {
    }

    /**
     * Adds an entrant to a specified waiting list and syncs the changes to Firebase.
     * <p>
     * This method modifies the local list and then calls Firebase methods
     * to save the entrant and update the list on the server.
     *
     * @param entrant The entrant to be added to the list.
     * @param list    The waiting list to which the entrant will be added.
     */
    public void joinWaitingList(Entrant entrant, WaitingList list) {
        list.addEntrant(entrant);
        Firebase.saveEntrant(entrant); //stub
        Firebase.updateWaitingList(list);
        //TODO : push to firebase
    }

    /**
     * Removes an entrant from a specified waiting list and updates the list in Firebase.
     *
     * @param entrant The entrant to be removed.
     * @param list    The waiting list from which the entrant will be removed.
     */
    public void leaveWaitingList(Entrant entrant, WaitingList list) {
        list.removeEntrant(entrant);
        list.removeEntrant(entrant);    // why does this happen twice?
        Firebase.updateWaitingList(list);
    }

    /**
     * Marks an entrant's invitation as accepted.
     * Wraps {@link Entrant} method.
     *
     * @param entrant The entrant who is accepting the invitation.
     */
    public void acceptInvitation(Entrant entrant) {
        entrant.acceptInvitation();
    }

    /**
     * Attempts to join an entrant to a local waiting list, preventing duplicates.
     * <p>
     * This method only modifies the local {@link WaitingList} object and does not
     * sync with Firebase.
     *
     * @param entrant The entrant attempting to join.
     * @param list    The waiting list they are trying to join.
     * @return {@code true} if the entrant was successfully added (was not already on the list),
     * {@code false} if the entrant was already on the list.
     */
    public boolean joinEvent(Entrant entrant, WaitingList list) {
        if (!list.getEntrants().contains(entrant)) {
            list.addEntrant(entrant);
            return true;
        }
        else return false;
    }

    /**
     * Directly adds an entrant to an event's waiting list in Firestore.
     * <p>
     * This method writes the {@link Entrant} object to the 'waitingList' subcollection
     * of a specific event document, using the entrant's device ID as the document ID.
     * It includes success and failure logging to the console.
     *
     * @param entrant The entrant object to be saved to Firestore.
     * @param eventId The unique identifier for the event.
     */
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

    /**
     * Attempts to remove an entrant from a local waiting list.
     * <p>
     * This method only modifies the local {@link WaitingList} object and does not
     * sync with Firebase.
     *
     * @param entrant The entrant attempting to leave.
     * @param list    The waiting list they are trying to leave.
     * @return {@code true} if the entrant was successfully removed (was on the list),
     * {@code false} if the entrant was not found on the list.
     */
    public boolean leaveEvent(Entrant entrant, WaitingList list) {
        if (list.getEntrants().contains(entrant)) {
            list.removeEntrant(entrant);
            return true;
        }
        else return false;
    }

}
