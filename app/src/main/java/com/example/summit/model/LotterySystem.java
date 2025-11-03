package com.example.summit.model;

import static java.util.Collections.shuffle;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class LotterySystem {
    /*TODO: implement lottery logic - random selection
    - update entrant invitation status (they get accept/decline option)
    */

    public LotterySystem() {
    }

    /***
     * Randomly samples entrants from a waiting list
     *
     * This works under the assumption that all entrants in the waiting list can be chosen
     * for the event (Users invited already shouldn't be in the waitinglist)
     *
     * returns - List of randomly sampled entrants
     */
    public List<Entrant> sampleEntrants(WaitingList list, int spots) {

        shuffle(list.getEntrants());
        return list.getEntrants().subList(0, Math.min(spots, list.getTotalEntrants()));
    }
}
