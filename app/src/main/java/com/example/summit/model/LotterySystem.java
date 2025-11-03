package com.example.summit.model;

import static java.util.Collections.shuffle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LotterySystem {
    /*TODO: implement lottery logic - random selection
    - update entrant invitation status (they get accept/decline option)
    */
    // Represents the next in like entrants
    ArrayList<Entrant> waitingListEntrantOrder;
    int totalEntrantsAcceptedInvited = 0;
    int totalSpots;
    // Represents currently invited entrants
    ArrayList<Entrant> invitedEntrantList;

    // Represents accepted entrants
    ArrayList<Entrant> acceptedEntrants;

    public LotterySystem(int totalSpots) {
        this.totalSpots = totalSpots;
    }

    /***
     * Randomly samples entrants from a waiting list
     * <br>
     * The list is stored within lottery system, and subsequent calls sample from the same list
     *
     * <h1>
     *    This function assumes that the WaitingList passed in does not change!
     * </h1>
     * <br>
     * returns - List of randomly invited entrants
     */
    public List<Entrant> sampleEntrants(WaitingList list, int spots) {
        if(this.totalEntrantsAcceptedInvited == 0) {
            // Create the random order to sample entrants in
            ArrayList<Entrant> entrants = (ArrayList<Entrant>)list.getEntrants();
            Collections.shuffle(entrants);
            this.waitingListEntrantOrder = entrants;
        }
        int availableSpots = totalSpots - totalEntrantsAcceptedInvited;
        int canInvite = Math.min(spots, availableSpots);

        int remainingEntrants = waitingListEntrantOrder.size() - totalEntrantsAcceptedInvited;
        canInvite = Math.min(canInvite, remainingEntrants);

        List<Entrant> newInvites = new ArrayList<>();
        for(int i = 0; i < canInvite; i++) {
            int index = totalEntrantsAcceptedInvited + i;
            Entrant nextEntrant = waitingListEntrantOrder.get(index);
            newInvites.add(nextEntrant);
            this.invitedEntrantList.add(nextEntrant);
        }
        totalEntrantsAcceptedInvited += canInvite;
        return newInvites;
    }

    public void handleEntrantResponse(Entrant entrant, boolean accepted) {
        if(accepted) {
            acceptedEntrants.add(entrant);
            invitedEntrantList.remove(entrant);
        }
        else {
            invitedEntrantList.remove(entrant);
        }
    }

    public List<Entrant> getAccepted() {
        return this.acceptedEntrants;
    }

    public List<Entrant> getInvited() {
        return this.invitedEntrantList;
    }

}
