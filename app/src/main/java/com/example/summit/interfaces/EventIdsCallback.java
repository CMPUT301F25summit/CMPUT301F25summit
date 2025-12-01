package com.example.summit.interfaces;

import java.util.List;

/**
 * Callback for loading event IDs from Firestore.
 */
public interface EventIdsCallback {
    void onEventIdsLoaded(List<String> eventIds);
    void onLoadFailure(String error);
}
