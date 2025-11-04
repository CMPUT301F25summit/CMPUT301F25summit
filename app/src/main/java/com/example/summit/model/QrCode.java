package com.example.summit.model;

import android.graphics.Bitmap;

/**
 * Represents a QR code associated with an event.
 * @author Summit Team
 * @version 1.0
 * @since 2025-11-04
 */
public class QrCode {

    /**
     * The event ID that this QR code represents.
     * This is the unique identifier that gets encoded in the QR code.
     */
    private String eventId;

    /**
     * The QR code image data as a Base64 encoded string.
     * This allows the QR code to be stored in Firebase or other databases.
     */
    private String qrCodeData;

    /**
     * The URL that the QR code points to (optional).
     * If set, the QR code will encode a full URL instead of just the event ID.
     */
    private String qrCodeUrl;

    /**
     * Timestamp of when this QR code was generated.
     */
    private long generatedTimestamp;

    /**
     * Default constructor required for Firebase.
     */
    public QrCode() {
        // Required empty constructor for Firebase
    }

    /**
     * Creates a new QR code for the specified event.
     *
     * @param eventId The unique identifier of the event this QR code represents
     */
    public QrCode(String eventId) {
        this.eventId = eventId;
        this.generatedTimestamp = System.currentTimeMillis();
    }

    /**
     * Creates a new QR code with a URL for the specified event.
     *
     * @param eventId The unique identifier of the event
     * @param qrCodeUrl The URL that the QR code should point to
     */
    public QrCode(String eventId, String qrCodeUrl) {
        this.eventId = eventId;
        this.qrCodeUrl = qrCodeUrl;
        this.generatedTimestamp = System.currentTimeMillis();
    }

    /**
     * Gets the event ID associated with this QR code.
     *
     * @return The event ID as a String
     */
    public String getEventId() {
        return eventId;
    }

    /**
     * Sets the event ID for this QR code.
     *
     * @param eventId The event ID to set
     */
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    /**
     * Gets the QR code data as a Base64 encoded string.
     *
     * @return The Base64 encoded QR code image data
     */
    public String getQrCodeData() {
        return qrCodeData;
    }

    /**
     * Sets the QR code data as a Base64 encoded string.
     * This is used to store the QR code image in Firebase.
     *
     * @param qrCodeData The Base64 encoded image data
     */
    public void setQrCodeData(String qrCodeData) {
        this.qrCodeData = qrCodeData;
    }

    /**
     * Gets the URL that this QR code points to.
     *
     * @return The QR code URL, or null if not set
     */
    public String getQrCodeUrl() {
        return qrCodeUrl;
    }

    /**
     * Sets the URL for this QR code.
     *
     * @param qrCodeUrl The URL to encode in the QR code
     */
    public void setQrCodeUrl(String qrCodeUrl) {
        this.qrCodeUrl = qrCodeUrl;
    }

    /**
     * Gets the timestamp of when this QR code was generated.
     *
     * @return The generation timestamp in milliseconds
     */
    public long getGeneratedTimestamp() {
        return generatedTimestamp;
    }

    /**
     * Sets the generation timestamp for this QR code.
     *
     * @param generatedTimestamp The timestamp in milliseconds
     */
    public void setGeneratedTimestamp(long generatedTimestamp) {
        this.generatedTimestamp = generatedTimestamp;
    }

    /**
     * Gets the data that should be encoded in the QR code.
     * Returns the URL if set, otherwise returns the event ID.
     *
     * @return The data to encode in the QR code
     */
    public String getEncodedData() {
        return (qrCodeUrl != null && !qrCodeUrl.isEmpty()) ? qrCodeUrl : eventId;
    }
}