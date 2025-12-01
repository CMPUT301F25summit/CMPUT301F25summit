package com.example.summit.interfaces;

/**
 * Callback interface for asynchronous image deletion operations from Firebase Storage.
 * <p>
 * Used by Firebase methods that delete event poster images from Storage and update Firestore.
 */
public interface ImageDeleteCallback {
    /**
     * Called when an image is successfully deleted from Storage and Firestore is updated.
     */
    void onImageDeleteSuccess();

    /**
     * Called when image deletion fails.
     *
     * @param error The error message describing what went wrong
     */
    void onImageDeleteFailure(String error);
}
