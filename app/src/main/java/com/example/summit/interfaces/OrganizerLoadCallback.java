package com.example.summit.interfaces;

import com.example.summit.model.Organizer;
import java.util.List;

/**
 * Callback for loading organizers from Firestore.
 */
public interface OrganizerLoadCallback {
    void onOrganizersLoaded(List<Organizer> organizers);
    void onLoadFailure(String error);
}
