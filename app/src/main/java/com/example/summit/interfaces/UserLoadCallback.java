package com.example.summit.interfaces;

import com.example.summit.model.UserProfile;
import java.util.List;

/**
 * Callback interface for asynchronous user loading operations from Firebase.
 * <p>
 * Used by Firebase methods that load user profiles from Firestore collections.
 * Provides success and failure callbacks to handle the results of user loading operations.
 */
public interface UserLoadCallback {
    /**
     * Called when users are successfully loaded from Firebase.
     *
     * @param users The list of UserProfile objects loaded from Firestore
     */
    void onUsersLoaded(List<UserProfile> users);

    /**
     * Called when user loading fails.
     *
     * @param error The error message describing what went wrong
     */
    void onLoadFailure(String error);
}
