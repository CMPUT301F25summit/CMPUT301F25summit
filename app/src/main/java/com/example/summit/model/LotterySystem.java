package com.example.summit.model;

import java.util.ArrayList;
import java.util.List;

public class LotterySystem {
    /*TODO: implement lottery logic - random selection
    - draw entrants randomly from waitinglist
    - update entrant invitation status (they get accept/decline option)
    */

    public LotterySystem() {}

    public List<Entrant> sampleEntrants(WaitingList list, int spots) {
        /*todo: 1. implement random selection
        I've just put in a stub for now, delete/comment when actual implementation is done*/
        return list.getEntrants().subList(0, Math.min(spots, list.getTotalEntrants()));
    }



}
