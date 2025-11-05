package com.example.summit.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a waiting list for an event.
 * <p>
 * This class manages a collection of {@link Entrant} objects, ensuring that
 * each entrant can only be added to the list once.
 */
public class WaitingList {
    private List<Entrant> entrants;

    /**
     * Initializes empty waiting list.
     */
    public WaitingList() {
        this.entrants = new ArrayList<>();
    }

    /**
     * Adds an entrant to the waiting list if not already added.
     *
     * @param entrant The {@link Entrant} to add to the list.
     */
    public void addEntrant(Entrant entrant) {
        if (!entrants.contains(entrant)) {
            entrants.add(entrant);
        }
    }

    /**
     * Removes an entrant from the waiting list.
     * <p>
     * If the entrant is not in the list, this method does nothing.
     *
     * @param entrant The {@link Entrant} to remove from the list.
     */
    public void removeEntrant (Entrant entrant) {
        entrants.remove(entrant);
    }

    /**
     * Gets the total number of entrants currently on the waiting list.
     *
     * @return The size of the waiting list.
     */
    public int getTotalEntrants() { //size of "entrants" waiting list (for waiting list count)
        return (entrants.size());
    }

    /**
     * Retrieves the complete list of entrants on the waiting list.
     *
     * @return A {@link List} containing all {@link Entrant} objects on the list.
     */
    public List<Entrant> getEntrants() { //to retrieve entire list of entrants
        return entrants;
    }

    /**
     * Finds and returns an entrant from the list based on their device ID.
     * <p>
     * This is useful for accepting or declining invitations.
     *
     * @param id The device ID string to search for.
     * @return The matching {@link Entrant} object, or {@code null} if no entrant
     * with that ID is found.
     */
    public Entrant getEntrantById(String id) {
        for (Entrant entrant : entrants) {
            if (entrant.getDeviceId().equals(id)) {
                return entrant;
            }
        }
        return null;
    }
}
