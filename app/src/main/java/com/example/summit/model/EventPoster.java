package com.example.summit.model;

/**
 * Wrapper class that combines event and organizer information for displaying in admin image gallery.
 * <p>
 * This class simplifies the data needed for the admin image moderation feature by
 * combining relevant event details with organizer information into a single object.
 */
public class EventPoster {
    private String eventId;
    private String eventTitle;
    private String posterUrl;
    private String organizerId;
    private String organizerName;

    /**
     * Creates an EventPoster from an Event object and organizer name.
     *
     * @param event The Event containing event description and ID
     * @param organizerName The name of the organizer who created the event
     */
    public EventPoster(Event event, String organizerName) {
        this.eventId = event.getId();
        this.eventTitle = event.getEventDescription().getTitle();
        this.posterUrl = event.getEventDescription().getPosterUrl();
        this.organizerId = event.getEventDescription().getOrganizerId();
        this.organizerName = organizerName;
    }

    /**
     * Empty constructor for Firebase deserialization.
     */
    public EventPoster() {}

    /**
     * Gets the event ID.
     *
     * @return The event ID
     */
    public String getEventId() {
        return eventId;
    }

    /**
     * Sets the event ID.
     *
     * @param eventId The event ID to set
     */
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    /**
     * Gets the event title.
     *
     * @return The event title
     */
    public String getEventTitle() {
        return eventTitle;
    }

    /**
     * Sets the event title.
     *
     * @param eventTitle The event title to set
     */
    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    /**
     * Gets the poster URL from Firebase Storage.
     *
     * @return The poster URL
     */
    public String getPosterUrl() {
        return posterUrl;
    }

    /**
     * Sets the poster URL.
     *
     * @param posterUrl The poster URL to set
     */
    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    /**
     * Gets the organizer's device ID.
     *
     * @return The organizer ID
     */
    public String getOrganizerId() {
        return organizerId;
    }

    /**
     * Sets the organizer ID.
     *
     * @param organizerId The organizer ID to set
     */
    public void setOrganizerId(String organizerId) {
        this.organizerId = organizerId;
    }

    /**
     * Gets the organizer's name.
     *
     * @return The organizer name
     */
    public String getOrganizerName() {
        return organizerName;
    }

    /**
     * Sets the organizer's name.
     *
     * @param organizerName The organizer name to set
     */
    public void setOrganizerName(String organizerName) {
        this.organizerName = organizerName;
    }
}
