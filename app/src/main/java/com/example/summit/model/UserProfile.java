package com.example.summit.model;

/**
 * Wrapper class that unifies Entrant, Organizer, and Admin objects for display in a single list.
 * <p>
 * This class provides a common interface for displaying user information regardless of their role,
 * making it easier to handle mixed user types in the admin profile management interface.
 */
public class UserProfile {
    private String deviceId;
    private String name;
    private String city;
    private String role;
    private User originalUser;

    /**
     * Creates a UserProfile from any User subclass (Entrant, Organizer, or Admin).
     *
     * @param user The User object to wrap (can be Entrant, Organizer, or Admin)
     */
    public UserProfile(User user) {
        this.deviceId = user.getDeviceId();
        this.name = user.getName();
        this.role = user.getRole();
        this.originalUser = user;

        // Set city based on role - only Entrants and Admins have location field
        if (user instanceof Entrant) {
            this.city = ((Entrant) user).getCity();
        } else if (user instanceof Admin) {
            this.city = ((Admin) user).getCity();
        } else {
            // Organizers don't have location field
            this.city = "N/A";
        }

        // Handle null or empty city values
        if (this.city == null || this.city.isEmpty()) {
            this.city = "N/A";
        }
    }

    /**
     * Gets the device ID (unique identifier for the user).
     *
     * @return The device ID
     */
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * Gets the user's name.
     *
     * @return The user's name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the user's city/location.
     *
     * @return The city or "N/A" if not available
     */
    public String getCity() {
        return city;
    }

    /**
     * Gets the user's role.
     *
     * @return The role string: "Entrant", "Organizer", or "Admin"
     */
    public String getRole() {
        return role;
    }

    /**
     * Gets the original User object that this profile wraps.
     *
     * @return The original User object
     */
    public User getOriginalUser() {
        return originalUser;
    }

    /**
     * Sets the device ID.
     *
     * @param deviceId The device ID to set
     */
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * Sets the user's name.
     *
     * @param name The name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the user's city/location.
     *
     * @param city The city to set
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Sets the user's role.
     *
     * @param role The role to set
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Sets the original User object.
     *
     * @param originalUser The User object to set
     */
    public void setOriginalUser(User originalUser) {
        this.originalUser = originalUser;
    }
}
