package com.example.summit.interfaces;

import com.example.summit.model.EventPoster;
import java.util.List;

/**
 * Callback interface for asynchronous event poster loading operations from Firebase.
 * <p>
 * Used by Firebase methods that load event posters with associated organizer information.
 */
public interface EventPosterLoadCallback {
    /**
     * Called when event posters are successfully loaded from Firebase.
     *
     * @param posters The list of EventPoster objects loaded from Firestore
     */
    void onPostersLoaded(List<EventPoster> posters);

    /**
     * Called when poster loading fails.
     *
     * @param error The error message describing what went wrong
     */
    void onLoadFailure(String error);
}
