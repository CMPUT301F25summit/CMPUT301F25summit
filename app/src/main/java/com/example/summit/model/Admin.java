package com.example.summit.model;

/**
 * Represents an administrator user in the system.
 * <p>
 * Admins have elevated privileges including:
 * - Viewing all events in the system
 * - Deleting events and associated data
 * - Managing user profiles (future functionality)
 * - Managing organizers (future functionality)
 * - Managing images and QR codes (future functionality)
 * <p>
 */
public class Admin extends User {
    private String location;

    /**
     * Constructs an Admin with full user details.
     *
     * @param name The administrator's name
     * @param email The administrator's email address
     * @param deviceId The unique device ID (used as primary identifier)
     * @param phone The administrator's phone number
     */
    public Admin(String name, String email, String deviceId, String phone) {
        super(name, email, deviceId, phone);
    }

    /**
     * Constructs an Admin with full user details including location.
     *
     * @param name The administrator's name
     * @param email The administrator's email address
     * @param deviceId The unique device ID (used as primary identifier)
     * @param phone The administrator's phone number
     * @param location The administrator's city/location
     */
    public Admin(String name, String email, String deviceId, String phone, String location) {
        super(name, email, deviceId, phone);
        this.location = location;
    }

    /**
     * Empty constructor required for Firebase Firestore deserialization.
     */
    public Admin() {}

    /**
     * Returns the role identifier for this user type.
     *
     * @return The string "Admin"
     */
    @Override
    public String getRole() {
        return "Admin";
    }

    /**
     * Gets the administrator's city/location.
     *
     * @return The city/location string
     */
    public String getCity() {
        return this.location;
    }

    /**
     * Sets the administrator's city/location.
     *
     * @param city The city/location to set
     */
    public void setCity(String city) {
        this.location = city;
    }
}
