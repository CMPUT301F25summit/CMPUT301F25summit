package com.example.summit.model;

import static java.util.Collections.shuffle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LotterySystem {
    // Represents the next in like entrants
    List<Entrant> waitingListEntrantOrder;
    int totalEntrantsAcceptedInvited = 0;
    int totalSpots;
    // Represents currently invited entrants
    List<Entrant> invitedEntrantList;

    // Represents accepted entrants
    List<Entrant> acceptedEntrants;

    public LotterySystem(int totalSpots) {
        this.totalSpots = totalSpots;
        this.invitedEntrantList = new ArrayList<>();
        this.acceptedEntrants = new ArrayList<>();
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
     * returns - ArrayList of randomly invited entrants
     */
    public List<Entrant> sampleEntrants(WaitingList list, int spots) {
        if(this.totalEntrantsAcceptedInvited == 0) {
            // Create the random order to sample entrants in
            List<Entrant> entrants = (List<Entrant>)list.getEntrants();
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

    /***
     *
     * @param entrant - Entrant that responded to invitation
     * @param accepted - boolean representing whether or not entrant accepted invitation
     * <br>
     *    This method adds entrants to the correct list depending on response
     *    It does not replace the entrant
     */
    public void handleEntrantResponse(Entrant entrant, boolean accepted) {
        if(accepted) {
            acceptedEntrants.add(entrant);
            invitedEntrantList.remove(entrant);
        }
        else {
            invitedEntrantList.remove(entrant);
        }
    }

    /***
     *
     * @return List of accepted entrants
     */
    public List<Entrant> getAccepted() {
        return this.acceptedEntrants;
    }

    /***
     *
     * @return List of invited entrants
     */
    public List<Entrant> getInvited() {
        return this.invitedEntrantList;
    }

}
