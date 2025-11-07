package com.example.summit;

import static org.junit.Assert.assertEquals;

import com.example.summit.model.Entrant;
import com.example.summit.model.LotterySystem;
import com.example.summit.model.WaitingList;

import org.junit.Test;

public class LotterySystemTest {
    @Test
    public void lotterySystemIsCorrect() {
        LotterySystem lotterySystem = new LotterySystem(2);
        WaitingList waitingList = new WaitingList();

        waitingList.addEntrant(new Entrant("Kai", "1234", "1", "121"));
        waitingList.addEntrant(new Entrant("Tan", "12345", "2", "121312"));
        waitingList.addEntrant(new Entrant("kaitan", "1234", "3", "a"));

        lotterySystem.sampleEntrants(waitingList, 1);

        assertEquals(1, lotterySystem.getInvited().size());

        lotterySystem.sampleEntrants(waitingList, 1);

        assertEquals(2, lotterySystem.getInvited().size());
    }
}
