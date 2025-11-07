package com.example.summit;

import static org.junit.Assert.assertEquals;

import com.example.summit.model.Entrant;
import com.example.summit.model.WaitingList;

import org.junit.Test;

public class WaitingListTest {
    @Test
    public void testAddEntrant() {
        WaitingList waitingList = new WaitingList();
        waitingList.addEntrant(new Entrant("1234", "Kai", "121312", "12312"));
        assertEquals(waitingList.getEntrants().size(), 1);
    }

    @Test
    public void testRemoveEntrant() {
        WaitingList waitingList = new WaitingList();
        Entrant e = new Entrant("1234", "Kai", "121312", "12312");
        waitingList.addEntrant(e);

        waitingList.addEntrant(new Entrant("12312", "Kaidfasf", "121312", "12312"));
        waitingList.removeEntrant(e);

        assertEquals(waitingList.getEntrants().size(), 1);
    }

}

