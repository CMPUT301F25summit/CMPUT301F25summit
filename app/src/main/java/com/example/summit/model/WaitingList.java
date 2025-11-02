package com.example.summit.model;

import java.util.ArrayList;
import java.util.List;

// convert comments into javadoc later for appropriate methods
public class WaitingList {
    private List<Entrant> entrants;

    public WaitingList() {
        this.entrants = new ArrayList<>();
    }

    //add entrant if not added
    public void addEntrant(Entrant entrant) {
        if (!entrants.contains(entrant)) {
            entrants.add(entrant);
        }
    }

    //remove an entrant if in there
    public void removeEntrant (Entrant entrant) {
        entrants.remove(entrant);
    }

    public int getTotalEntrants() { //size of "entrants" waiting list (for waiting list count)
        return (entrants.size());
    }

    public List<Entrant> getEntrants() { //to retrieve entire list of entrants
        return entrants;
    }

    //to find entrant by id, would be useful for accepting/declining invitations if implemented :)
    public Entrant getEntrantById(String id) {
        for (Entrant entrant : entrants) {
            if (entrant.getDeviceId().equals(id)) {
                return entrant;
            }
        }
        return null;
    }
}
