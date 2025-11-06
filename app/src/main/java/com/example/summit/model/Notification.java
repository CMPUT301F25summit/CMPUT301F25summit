package com.example.summit.model;

public class Notification {
    private String id;
    private String eventId;
    private String recipientId;
    private String message;
    private long timestamp;

    public Notification() {} // Firestore needs empty constructor

    public Notification(String id, String eventId, String recipientId,
                        String message, long timestamp) {
        this.id = id;
        this.eventId = eventId;
        this.recipientId = recipientId;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getId() { return id; }
    public String getEventId() { return eventId; }
    public String getRecipientId() { return recipientId; }
    public String getMessage() { return message; }
    public long getTimestamp() { return timestamp; }
}

