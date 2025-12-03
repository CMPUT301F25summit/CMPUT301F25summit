package com.example.summit.model;

/**
 * Model class for notification log items in Admin Notification Hub.
 * Represents a notification with enriched data from multiple Firestore sources.
 */
public class NotificationLogItem {
    private String notificationId;      // Firestore document ID
    private String recipientId;         // entrantId
    private String recipientName;       // Fetched from entrants collection
    private String eventId;
    private String eventTitle;          // Fetched from events collection
    private String message;
    private long timestamp;             // Milliseconds
    private String type;                // "custom" or "invitation"
    private String status;              // "pending", "accepted", "declined", "info"
    private boolean isExpanded;         // UI state for message expansion
    private String organizerId;         // Device ID of organizer who sent notification
    private String organizerName;       // Name of the organizer (fetched from organizers collection)

    /**
     * Default constructor for Firestore.
     */
    public NotificationLogItem() {
    }

    /**
     * Full constructor.
     */
    public NotificationLogItem(String notificationId, String organizerId, String organizerName,
                                String recipientId, String recipientName,
                                String eventId, String eventTitle, String message,
                                long timestamp, String type, String status) {
        this.notificationId = notificationId;
        this.organizerId = organizerId;
        this.organizerName = organizerName;
        this.recipientId = recipientId;
        this.recipientName = recipientName;
        this.eventId = eventId;
        this.eventTitle = eventTitle;
        this.message = message;
        this.timestamp = timestamp;
        this.type = type;
        this.status = status;
        this.isExpanded = false;
    }

    // Getters
    public String getNotificationId() {
        return notificationId;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public String getEventId() {
        return eventId;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public String getOrganizerId() {
        return organizerId;
    }

    public String getOrganizerName() {
        return organizerName;
    }

    // Setters
    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    public void setOrganizerId(String organizerId) {
        this.organizerId = organizerId;
    }

    public void setOrganizerName(String organizerName) {
        this.organizerName = organizerName;
    }
}
