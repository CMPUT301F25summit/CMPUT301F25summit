package com.example.summit.interfaces;

/**
 * Callback interface for asynchronous event deletion operations.
 * <p>
 * Used by Firebase.deleteEvent() and Firebase.deleteEvents() to notify
 * the caller when deletion completes successfully or encounters an error.
 */
public interface DeleteCallback {
    /**
     * Called when all event and QR code deletions complete successfully.
     */
    void onDeleteSuccess();

    /**
     * Called when deletion fails for any reason.
     *
     * @param error A description of the error that occurred
     */
    void onDeleteFailure(String error);
}
