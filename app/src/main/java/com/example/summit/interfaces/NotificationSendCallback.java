package com.example.summit.interfaces;

/**
 * Callback interface for asynchronous notification sending operations to organizers.
 * <p>
 * Used by Firebase methods that send notifications to organizers via Firestore.
 */
public interface NotificationSendCallback {
    /**
     * Called when a notification is successfully sent to an organizer.
     */
    void onNotificationSent();

    /**
     * Called when notification sending fails.
     *
     * @param error The error message describing what went wrong
     */
    void onNotificationFailure(String error);
}
