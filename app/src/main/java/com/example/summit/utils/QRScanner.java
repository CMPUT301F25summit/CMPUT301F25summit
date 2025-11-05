package com.example.summit.utils;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Utility class for QR code scanning operations.
 * Provides helper methods for scanning QR codes and processing event IDs.
 * Works in conjunction with QRCodeGenerator for complete QR code functionality.
 */
public class QRScanner {

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private QRScanner() {
        throw new UnsupportedOperationException("QRScanner is a utility class and cannot be instantiated");
    }

    /**
     * Validates if a scanned string is a valid event ID format.
     * Checks if the string is not null and not empty.
     */
    public static boolean isValidEventId(String scannedData) {
        return scannedData != null && !scannedData.trim().isEmpty();
    }

    /**
     * Fetches event data from Firebase using the event ID.
     */
    public static void fetchEventById(String eventId, EventFetchCallback callback) {
        if (!isValidEventId(eventId)) {
            callback.onFailure(new IllegalArgumentException("Invalid event ID"));
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events")
                .document(eventId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        callback.onSuccess(documentSnapshot);
                    } else {
                        callback.onFailure(new Exception("Event not found"));
                    }
                })
                .addOnFailureListener(callback::onFailure);
    }

    /**
     * Creates a navigation bundle containing the event ID.
     * Use this to pass event ID between fragments.
     */
    public static Bundle createEventIdBundle(String eventId) {
        Bundle bundle = new Bundle();
        bundle.putString("eventId", eventId);
        return bundle;
    }

    /**
     * Extracts event ID from a navigation bundle.
     */
    public static String getEventIdFromBundle(Bundle bundle) {
        if (bundle == null) {
            return null;
        }
        return bundle.getString("eventId");
    }

    /**
     * Navigates to event details using the Navigation Component.
     */
    public static boolean navigateToEventDetails(Fragment fragment, String eventId, int actionId) {
        try {
            Bundle bundle = createEventIdBundle(eventId);
            NavController navController = NavHostFragment.findNavController(fragment);
            navController.navigate(actionId, bundle);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Callback interface for event fetching operations.
     * Implement this interface to handle event fetch results.
     */
    public interface EventFetchCallback {
        /**
         * Called when the event is successfully fetched from Firebase.
         */
        void onSuccess(DocumentSnapshot eventData);

        /**
         * Called when event fetching fails.
         */
        void onFailure(Exception e);
    }
}