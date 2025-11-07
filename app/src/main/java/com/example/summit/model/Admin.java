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
 * Like other users, admins are identified by their device ID.
 */
public class Admin extends User {
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
}
