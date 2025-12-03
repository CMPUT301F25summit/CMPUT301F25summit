package com.example.summit.model;

import android.content.Context;
import android.util.Log;
import android.location.Location;
import android.widget.Toast;

import com.example.summit.interfaces.DeleteCallback;
import com.example.summit.interfaces.EventIdsCallback;
import com.example.summit.interfaces.EventLoadCallback;
import com.example.summit.interfaces.EventPosterLoadCallback;
import com.example.summit.interfaces.ImageDeleteCallback;
import com.example.summit.interfaces.MigrationCallback;
import com.example.summit.interfaces.NotificationLogCallback;
import com.example.summit.interfaces.OrganizerLoadCallback;
import com.example.summit.interfaces.UserLoadCallback;
import com.google.firebase.firestore.Query;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    /**
     * Loads all users from Firestore with real-time updates using SnapshotListeners.
     * <p>
     * This method sets up three separate SnapshotListeners on the 'entrants', 'organizers',
     * and 'admins' collections. The listeners merge results into a unified list of UserProfile
     * objects and notify the callback whenever any collection changes.
     *
     * @param callback The callback to handle loaded users or errors
     */
    public static void loadAllUsersRealtime(UserLoadCallback callback) {
        List<UserProfile> allUsers = new ArrayList<>();
        AtomicInteger loadedCollections = new AtomicInteger(0);

        // Listener for Entrants collection
        db.collection("entrants").addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.e("Firebase", "Error loading entrants: " + error);
                callback.onLoadFailure("Error loading entrants: " + error.getMessage());
                return;
            }

            synchronized (allUsers) {
                // Remove old entrants from the list
                allUsers.removeIf(u -> "Entrant".equals(u.getRole()));

                // Add current entrants
                if (value != null) {
                    for (DocumentSnapshot doc : value.getDocuments()) {
                        Entrant entrant = doc.toObject(Entrant.class);
                        if (entrant != null) {
                            allUsers.add(new UserProfile(entrant));
                        }
                    }
                }

                // Notify callback if all collections have loaded at least once
                if (loadedCollections.incrementAndGet() >= 3) {
                    callback.onUsersLoaded(new ArrayList<>(allUsers));
                } else if (loadedCollections.get() == 3) {
                    // Subsequent updates after initial load
                    callback.onUsersLoaded(new ArrayList<>(allUsers));
                }
            }
        });

        // Reset counter for tracking
        loadedCollections.set(0);

        // Listener for Organizers collection
        db.collection("organizers").addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.e("Firebase", "Error loading organizers: " + error);
                callback.onLoadFailure("Error loading organizers: " + error.getMessage());
                return;
            }

            synchronized (allUsers) {
                // Remove old organizers from the list
                allUsers.removeIf(u -> "Organizer".equals(u.getRole()));

                // Add current organizers
                if (value != null) {
                    for (DocumentSnapshot doc : value.getDocuments()) {
                        Organizer organizer = doc.toObject(Organizer.class);
                        if (organizer != null) {
                            allUsers.add(new UserProfile(organizer));
                        }
                    }
                }

                loadedCollections.incrementAndGet();
                callback.onUsersLoaded(new ArrayList<>(allUsers));
            }
        });

        // Listener for Admins collection
        db.collection("admins").addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.e("Firebase", "Error loading admins: " + error);
                callback.onLoadFailure("Error loading admins: " + error.getMessage());
                return;
            }

            synchronized (allUsers) {
                // Remove old admins from the list
                allUsers.removeIf(u -> "Admin".equals(u.getRole()));

                // Add current admins
                if (value != null) {
                    for (DocumentSnapshot doc : value.getDocuments()) {
                        Admin admin = doc.toObject(Admin.class);
                        if (admin != null) {
                            allUsers.add(new UserProfile(admin));
                        }
                    }
                }

                loadedCollections.incrementAndGet();
                callback.onUsersLoaded(new ArrayList<>(allUsers));
            }
        });
    }

    /**
     * Deletes a single user from the appropriate Firestore collection based on their role.
     *
     * @param userProfile The UserProfile containing the user to delete
     * @param callback The callback to handle success or failure
     */
    public static void deleteUser(UserProfile userProfile, DeleteCallback callback) {
        String collection = getCollectionForRole(userProfile.getRole());
        String deviceId = userProfile.getDeviceId();

        db.collection(collection)
                .document(deviceId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firebase", "User deleted: " + deviceId + " from " + collection);
                    callback.onDeleteSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "Error deleting user: " + deviceId, e);
                    callback.onDeleteFailure("Error deleting user: " + e.getMessage());
                });
    }

    /**
     * Deletes multiple users from their respective Firestore collections.
     * <p>
     * This method deletes users based on their roles, routing each deletion
     * to the appropriate collection ('entrants', 'organizers', or 'admins').
     * It tracks deletion progress and invokes the callback when all deletions
     * are complete or if errors occur.
     *
     * @param userProfiles List of UserProfiles to delete
     * @param callback The callback to handle success or failure
     */
    public static void deleteUsers(List<UserProfile> userProfiles, DeleteCallback callback) {
        if (userProfiles == null || userProfiles.isEmpty()) {
            callback.onDeleteFailure("No users to delete");
            return;
        }

        AtomicInteger pendingDeletions = new AtomicInteger(userProfiles.size());
        AtomicInteger failureCount = new AtomicInteger(0);

        for (UserProfile userProfile : userProfiles) {
            deleteUser(userProfile, new DeleteCallback() {
                @Override
                public void onDeleteSuccess() {
                    if (pendingDeletions.decrementAndGet() == 0) {
                        if (failureCount.get() == 0) {
                            callback.onDeleteSuccess();
                        } else {
                            callback.onDeleteFailure(failureCount.get() + " deletion(s) failed");
                        }
                    }
                }

                @Override
                public void onDeleteFailure(String error) {
                    Log.e("Firebase", "Deletion failed: " + error);
                    failureCount.incrementAndGet();
                    if (pendingDeletions.decrementAndGet() == 0) {
                        callback.onDeleteFailure(failureCount.get() + " deletion(s) failed");
                    }
                }
            });
        }
    }

    /**
     * Helper method to determine the Firestore collection name for a given user role.
     *
     * @param role The user role ("Entrant", "Organizer", or "Admin")
     * @return The collection name ("entrants", "organizers", or "admins")
     * @throws IllegalArgumentException if the role is not recognized
     */
    private static String getCollectionForRole(String role) {
        switch (role) {
            case "Entrant":
                return "entrants";
            case "Organizer":
                return "organizers";
            case "Admin":
                return "admins";
            default:
                throw new IllegalArgumentException("Unknown role: " + role);
        }
    }

    /**
     * Loads all event posters with associated organizer names from Firestore.
     * Only includes events that have posterBase64 data.
     * <p>
     * This method fetches events from the 'events' collection and joins them with
     * organizer data from the 'organizers' collection to create EventPoster objects.
     *
     * @param callback Callback invoked with list of EventPoster objects or error
     */
    public static void loadAllEventPosters(EventPosterLoadCallback callback) {
        db.collection("events")
            .get()
            .addOnSuccessListener(eventQuery -> {
                List<EventPoster> posters = new ArrayList<>();
                AtomicInteger pendingOrganizers = new AtomicInteger(0);

                // Filter events that have posters
                List<DocumentSnapshot> eventsWithPosters = new ArrayList<>();
                for (DocumentSnapshot eventDoc : eventQuery.getDocuments()) {
                    EventDescription desc = eventDoc.toObject(EventDescription.class);
                    if (desc != null && desc.getPosterBase64() != null
                        && !desc.getPosterBase64().isEmpty()) {
                        eventsWithPosters.add(eventDoc);
                    }
                }

                if (eventsWithPosters.isEmpty()) {
                    callback.onPostersLoaded(new ArrayList<>());
                    return;
                }

                pendingOrganizers.set(eventsWithPosters.size());

                // For each event, fetch organizer name and create EventPoster
                for (DocumentSnapshot eventDoc : eventsWithPosters) {
                    EventDescription desc = eventDoc.toObject(EventDescription.class);
                    Event event = new Event(desc);
                    event.setId(eventDoc.getId());

                    String organizerId = desc.getOrganizerId();

                    // Fetch organizer name
                    db.collection("organizers")
                        .document(organizerId)
                        .get()
                        .addOnSuccessListener(orgDoc -> {
                            String organizerName = "Unknown Organizer";
                            if (orgDoc.exists()) {
                                Organizer org = orgDoc.toObject(Organizer.class);
                                if (org != null) {
                                    organizerName = org.getName();
                                }
                            }

                            EventPoster poster = new EventPoster(event, organizerName);
                            poster.setPosterUrl(desc.getPosterBase64());
                            posters.add(poster);

                            // Check if all organizers loaded
                            if (pendingOrganizers.decrementAndGet() == 0) {
                                callback.onPostersLoaded(posters);
                            }
                        })
                        .addOnFailureListener(e -> {
                            // Add with default organizer name on failure
                            EventPoster poster = new EventPoster(event, "Unknown Organizer");
                            poster.setPosterUrl(desc.getPosterBase64());
                            posters.add(poster);

                            if (pendingOrganizers.decrementAndGet() == 0) {
                                callback.onPostersLoaded(posters);
                            }
                        });
                }
            })
            .addOnFailureListener(e ->
                callback.onLoadFailure("Failed to load events: " + e.getMessage())
            );
    }

    /**
     * Deletes an event's poster by removing the posterBase64 field from Firestore.
     * Does not delete the entire event, only the image data.
     *
     * @param eventId The ID of the event whose poster should be deleted
     * @param callback Callback invoked on success or failure
     */
    public static void deleteEventPoster(String eventId, ImageDeleteCallback callback) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("posterBase64", null);
        updates.put("posterUrl", null);

        db.collection("events")
            .document(eventId)
            .update(updates)
            .addOnSuccessListener(aVoid -> {
                Log.d("Firebase", "Poster deleted for event: " + eventId);
                callback.onImageDeleteSuccess();
            })
            .addOnFailureListener(e -> {
                Log.e("Firebase", "Error deleting poster: " + eventId, e);
                callback.onImageDeleteFailure("Failed to delete: " + e.getMessage());
            });
    }

    /**
     * Loads all organizers with real-time updates.
     */
    public static void loadAllOrganizersRealtime(OrganizerLoadCallback callback) {
        db.collection("organizers").addSnapshotListener((value, error) -> {
            if (error != null) {
                callback.onLoadFailure(error.getMessage());
                return;
            }
            List<Organizer> organizers = new ArrayList<>();
            if (value != null) {
                for (DocumentSnapshot doc : value.getDocuments()) {
                    Organizer org = doc.toObject(Organizer.class);
                    if (org != null) organizers.add(org);
                }
            }
            callback.onOrganizersLoaded(organizers);
        });
    }

    /**
     * Loads event IDs for a specific organizer.
     */
    public static void loadOrganizerEventIds(String organizerId, EventIdsCallback callback) {
        db.collection("events")
            .get()
            .addOnSuccessListener(query -> {
                List<String> eventIds = new ArrayList<>();
                for (DocumentSnapshot doc : query.getDocuments()) {
                    EventDescription desc = doc.toObject(EventDescription.class);
                    if (desc != null && organizerId.equals(desc.getOrganizerId())) {
                        eventIds.add(doc.getId());
                    }
                }
                callback.onEventIdsLoaded(eventIds);
            })
            .addOnFailureListener(e -> callback.onLoadFailure(e.getMessage()));
    }

    /**
     * Loads notifications for specific events with batching support.
     */
    public static void loadNotificationsForEvents(List<String> eventIds,
                                                    NotificationLogCallback callback) {
        if (eventIds.isEmpty()) {
            callback.onNotificationsLoaded(new ArrayList<>());
            return;
        }

        List<List<String>> batches = batchEventIds(eventIds, 10);
        List<NotificationLogItem> allNotifications = new ArrayList<>();
        AtomicInteger batchesCompleted = new AtomicInteger(0);

        for (List<String> batch : batches) {
            db.collection("notifications")
                .whereIn("eventId", batch)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(query -> {
                    synchronized (allNotifications) {
                        for (DocumentSnapshot doc : query.getDocuments()) {
                            NotificationLogItem item = new NotificationLogItem();
                            item.setNotificationId(doc.getId());
                            item.setRecipientId(doc.getString("entrantId"));
                            item.setEventId(doc.getString("eventId"));
                            item.setMessage(doc.getString("message"));
                            Long timestamp = doc.getLong("timestamp");
                            item.setTimestamp(timestamp != null ? timestamp : 0);
                            item.setType(doc.getString("type"));
                            item.setStatus(doc.getString("status"));
                            allNotifications.add(item);
                        }
                        if (batchesCompleted.incrementAndGet() == batches.size()) {
                            callback.onNotificationsLoaded(allNotifications);
                        }
                    }
                })
                .addOnFailureListener(e -> callback.onLoadFailure(e.getMessage()));
        }
    }

    /**
     * Helper to batch event IDs into groups.
     */
    private static List<List<String>> batchEventIds(List<String> ids, int batchSize) {
        List<List<String>> batches = new ArrayList<>();
        for (int i = 0; i < ids.size(); i += batchSize) {
            batches.add(ids.subList(i, Math.min(ids.size(), i + batchSize)));
        }
        return batches;
    }

    /**
     * Deletes a notification from Firestore.
     */
    public static void deleteNotification(String notificationId, DeleteCallback callback) {
        db.collection("notifications")
            .document(notificationId)
            .delete()
            .addOnSuccessListener(v -> callback.onDeleteSuccess())
            .addOnFailureListener(e -> callback.onDeleteFailure(e.getMessage()));
    }

    /**
     * Updates a notification's message.
     */
    public static void updateNotificationMessage(String notificationId, String newMessage,
                                                  DeleteCallback callback) {
        db.collection("notifications")
            .document(notificationId)
            .update("message", newMessage)
            .addOnSuccessListener(v -> callback.onDeleteSuccess())
            .addOnFailureListener(e -> callback.onDeleteFailure(e.getMessage()));
    }

    /**
     * Logs a notification to the notification_logs collection with enriched data.
     * <p>
     * This method fetches organizer name, event title, and recipient name from Firestore
     * to create a complete log entry. It runs asynchronously and does not block the
     * notification sending process. If logging fails, a Toast error is shown to the organizer.
     *
     * @param organizerId The device ID of the organizer sending the notification
     * @param recipientId The device ID of the recipient (entrant)
     * @param eventId The ID of the event related to this notification
     * @param message The notification message text
     * @param timestamp The timestamp when the notification was sent
     * @param type The type of notification ("custom" or "invitation")
     * @param status The status of the notification ("info", "pending", etc.)
     * @param context The context for displaying Toast messages
     */
    public static void logNotification(String organizerId, String recipientId,
                                       String eventId, String message,
                                       long timestamp, String type, String status,
                                       Context context) {
        // Fetch organizer name
        db.collection("organizers").document(organizerId).get()
            .addOnSuccessListener(organizerDoc -> {
                String organizerName = "Unknown Organizer";
                if (organizerDoc.exists() && organizerDoc.getString("name") != null) {
                    organizerName = organizerDoc.getString("name");
                }
                final String finalOrganizerName = organizerName;

                // Fetch event title
                db.collection("events").document(eventId).get()
                    .addOnSuccessListener(eventDoc -> {
                        String eventTitle = "Unknown Event";
                        if (eventDoc.exists() && eventDoc.getString("title") != null) {
                            eventTitle = eventDoc.getString("title");
                        }
                        final String finalEventTitle = eventTitle;

                        // Fetch recipient name
                        db.collection("entrants").document(recipientId).get()
                            .addOnSuccessListener(entrantDoc -> {
                                String recipientName = "Unknown Entrant";
                                if (entrantDoc.exists() && entrantDoc.getString("name") != null) {
                                    recipientName = entrantDoc.getString("name");
                                }

                                // Create and write log entry
                                writeLogEntry(organizerId, finalOrganizerName, recipientId, recipientName,
                                    eventId, finalEventTitle, message, timestamp, type, status, context);
                            })
                            .addOnFailureListener(e -> {
                                Log.e("Firebase", "Failed to fetch entrant name", e);
                                // Continue logging with "Unknown Entrant"
                                writeLogEntry(organizerId, finalOrganizerName, recipientId, "Unknown Entrant",
                                    eventId, finalEventTitle, message, timestamp, type, status, context);
                            });
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firebase", "Failed to fetch event title", e);
                        // Continue logging with "Unknown Event"
                        createLogWithDefaults(organizerId, finalOrganizerName, recipientId, null,
                            eventId, "Unknown Event", message, timestamp, type, status, context);
                    });
            })
            .addOnFailureListener(e -> {
                Log.e("Firebase", "Failed to fetch organizer name", e);
                // Continue logging with "Unknown Organizer"
                createLogWithDefaults(organizerId, "Unknown Organizer", recipientId, null,
                    eventId, null, message, timestamp, type, status, context);
            });
    }

    /**
     * Helper method to create a log entry when some enrichment data is unavailable.
     * This ensures logs are still created even if some Firestore reads fail.
     */
    private static void createLogWithDefaults(String organizerId, String organizerName,
                                              String recipientId, String recipientName,
                                              String eventId, String eventTitle,
                                              String message, long timestamp,
                                              String type, String status, Context context) {
        // If recipientName is null, try to fetch it
        if (recipientName == null) {
            db.collection("entrants").document(recipientId).get()
                .addOnSuccessListener(entrantDoc -> {
                    String name = "Unknown Entrant";
                    if (entrantDoc.exists() && entrantDoc.getString("name") != null) {
                        name = entrantDoc.getString("name");
                    }
                    writeLogEntry(organizerId, organizerName, recipientId, name,
                        eventId, eventTitle != null ? eventTitle : "Unknown Event",
                        message, timestamp, type, status, context);
                })
                .addOnFailureListener(e -> {
                    writeLogEntry(organizerId, organizerName, recipientId, "Unknown Entrant",
                        eventId, eventTitle != null ? eventTitle : "Unknown Event",
                        message, timestamp, type, status, context);
                });
        } else if (eventTitle == null) {
            // If eventTitle is null, try to fetch it
            db.collection("events").document(eventId).get()
                .addOnSuccessListener(eventDoc -> {
                    String title = "Unknown Event";
                    if (eventDoc.exists() && eventDoc.getString("title") != null) {
                        title = eventDoc.getString("title");
                    }
                    writeLogEntry(organizerId, organizerName, recipientId, recipientName,
                        eventId, title, message, timestamp, type, status, context);
                })
                .addOnFailureListener(e -> {
                    writeLogEntry(organizerId, organizerName, recipientId, recipientName,
                        eventId, "Unknown Event", message, timestamp, type, status, context);
                });
        } else {
            writeLogEntry(organizerId, organizerName, recipientId, recipientName,
                eventId, eventTitle, message, timestamp, type, status, context);
        }
    }

    /**
     * Final step: writes the log entry to Firestore.
     */
    private static void writeLogEntry(String organizerId, String organizerName,
                                      String recipientId, String recipientName,
                                      String eventId, String eventTitle,
                                      String message, long timestamp,
                                      String type, String status, Context context) {
        Map<String, Object> logEntry = new HashMap<>();
        logEntry.put("organizerId", organizerId);
        logEntry.put("organizerName", organizerName);
        logEntry.put("recipientId", recipientId);
        logEntry.put("recipientName", recipientName);
        logEntry.put("eventId", eventId);
        logEntry.put("eventTitle", eventTitle);
        logEntry.put("message", message);
        logEntry.put("timestamp", timestamp);
        logEntry.put("type", type);
        logEntry.put("status", status);

        db.collection("notification_logs")
            .add(logEntry)
            .addOnSuccessListener(docRef ->
                Log.d("Firebase", "Notification logged: " + docRef.getId()))
            .addOnFailureListener(e -> {
                Log.e("Firebase", "Failed to log notification", e);
                if (context != null) {
                    Toast.makeText(context, "Notification sent but logging failed: " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
                }
            });
    }

    /**
     * Migrates historical notifications from the "notifications" collection to "notification_logs".
     * <p>
     * This method queries notifications from the last 30 days and creates corresponding entries
     * in the notification_logs collection with enriched data (organizer name, event title, recipient name).
     * The organizerId is derived from the event document since old notifications don't store it directly.
     * <p>
     * This migration runs asynchronously and does not block the UI. It should be called once when
     * the admin app starts for the first time.
     *
     * @param context  The context for displaying error messages
     * @param callback Callback to report migration progress and completion
     */
    public static void migrateHistoricalNotifications(Context context, MigrationCallback callback) {
        // Calculate timestamp for 30 days ago
        long thirtyDaysInMillis = 30L * 24 * 60 * 60 * 1000;
        long cutoffTimestamp = System.currentTimeMillis() - thirtyDaysInMillis;

        Log.d("Firebase", "Starting migration of notifications from last 30 days");

        // Query notifications from last 30 days
        db.collection("notifications")
            .whereGreaterThanOrEqualTo("timestamp", cutoffTimestamp)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(500)
            .get()
            .addOnSuccessListener(querySnapshot -> {
                if (querySnapshot.isEmpty()) {
                    Log.d("Firebase", "No notifications to migrate");
                    callback.onMigrationComplete(0, 0);
                    return;
                }

                int totalNotifications = querySnapshot.size();
                final int[] successCount = {0};
                final int[] failureCount = {0};
                final int[] processedCount = {0};

                Log.d("Firebase", "Found " + totalNotifications + " notifications to migrate");

                // Process each notification
                for (DocumentSnapshot notificationDoc : querySnapshot.getDocuments()) {
                    String eventId = notificationDoc.getString("eventId");
                    String recipientId = notificationDoc.getString("entrantId");
                    String message = notificationDoc.getString("message");
                    Long timestampLong = notificationDoc.getLong("timestamp");
                    String status = notificationDoc.getString("status");
                    String type = notificationDoc.getString("type");

                    // Default type to "custom" if missing
                    if (type == null) {
                        type = "custom";
                    }

                    long timestamp = timestampLong != null ? timestampLong : System.currentTimeMillis();
                    final String finalType = type;

                    // Fetch event document to get organizerId
                    db.collection("events").document(eventId).get()
                        .addOnSuccessListener(eventDoc -> {
                            String organizerId = "unknown";
                            if (eventDoc.exists()) {
                                // Events store organizerId in the description field
                                String docOrganizerId = eventDoc.getString("organizerId");
                                if (docOrganizerId != null) {
                                    organizerId = docOrganizerId;
                                }
                            }

                            // Use logNotification to create enriched log entry
                            // Pass null context to avoid showing toast for each migration
                            logNotification(
                                organizerId,
                                recipientId,
                                eventId,
                                message,
                                timestamp,
                                finalType,
                                status,
                                null  // Don't show toast for historical migrations
                            );

                            successCount[0]++;
                            processedCount[0]++;

                            // Check if all notifications have been processed
                            if (processedCount[0] == totalNotifications) {
                                Log.d("Firebase", "Migration complete: " + successCount[0] +
                                    " succeeded, " + failureCount[0] + " failed");
                                callback.onMigrationComplete(successCount[0], failureCount[0]);
                            }
                        })
                        .addOnFailureListener(e -> {
                            Log.e("Firebase", "Failed to fetch event for migration: " + e.getMessage());

                            // Still try to migrate with "unknown" organizerId
                            logNotification(
                                "unknown",
                                recipientId,
                                eventId,
                                message,
                                timestamp,
                                finalType,
                                status,
                                null
                            );

                            failureCount[0]++;
                            processedCount[0]++;

                            // Check if all notifications have been processed
                            if (processedCount[0] == totalNotifications) {
                                Log.d("Firebase", "Migration complete: " + successCount[0] +
                                    " succeeded, " + failureCount[0] + " failed");
                                callback.onMigrationComplete(successCount[0], failureCount[0]);
                            }
                        });
                }
            })
            .addOnFailureListener(e -> {
                Log.e("Firebase", "Failed to query notifications for migration", e);
                callback.onMigrationError("Failed to query notifications: " + e.getMessage());
            });
    }

}
