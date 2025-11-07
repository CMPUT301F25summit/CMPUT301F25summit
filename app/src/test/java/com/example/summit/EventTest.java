package com.example.summit;

import com.example.summit.model.Event;
import com.example.summit.model.EventDescription;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.*;

/**
 * Unit tests for the Event model class.
 * Tests cover constructor initialization, list management (registeredEntrants, waitingList, declinedEntrants),
 * getters/setters, null handling, and edge cases for entrant management.
 */
public class EventTest {
    private Event event;
    private EventDescription testDescription;

    private static final String TEST_EVENT_ID = "event123";
    private static final String TEST_ENTRANT_1 = "entrant001";
    private static final String TEST_ENTRANT_2 = "entrant002";
    private static final String TEST_ENTRANT_3 = "entrant003";

    @Before
    public void setUp() {
        // Create a test EventDescription
        testDescription = new EventDescription(
            "Test Event",
            "Test Description",
            "2025-12-01",
            "2025-12-31",
            "2025-11-01",
            "2025-11-30",
            100,
            "http://example.com/poster.jpg",
            "organizer123"
        );

        event = new Event(testDescription);
        event.setId(TEST_EVENT_ID);
    }

    // ========== Constructor Tests ==========

    @Test
    public void testEmptyConstructor_initializesAllLists() {
        Event emptyEvent = new Event();

        assertNotNull("Event should not be null", emptyEvent);
        assertNotNull("RegisteredEntrants list should not be null", emptyEvent.getRegisteredEntrants());
        assertNotNull("WaitingList should not be null", emptyEvent.getWaitingList());
        assertNotNull("DeclinedEntrants list should not be null", emptyEvent.getDeclinedEntrants());

        assertTrue("RegisteredEntrants should be empty", emptyEvent.getRegisteredEntrants().isEmpty());
        assertTrue("WaitingList should be empty", emptyEvent.getWaitingList().isEmpty());
        assertTrue("DeclinedEntrants should be empty", emptyEvent.getDeclinedEntrants().isEmpty());
    }

    @Test
    public void testConstructor_withEventDescription_initializesAllLists() {
        Event newEvent = new Event(testDescription);

        assertNotNull("Event should not be null", newEvent);
        assertEquals("Description should match", testDescription, newEvent.getDescription());
        assertNotNull("RegisteredEntrants list should not be null", newEvent.getRegisteredEntrants());
        assertNotNull("WaitingList should not be null", newEvent.getWaitingList());
        assertNotNull("DeclinedEntrants list should not be null", newEvent.getDeclinedEntrants());

        assertTrue("RegisteredEntrants should be empty", newEvent.getRegisteredEntrants().isEmpty());
        assertTrue("WaitingList should be empty", newEvent.getWaitingList().isEmpty());
        assertTrue("DeclinedEntrants should be empty", newEvent.getDeclinedEntrants().isEmpty());
    }

    @Test
    public void testConstructor_withNullDescription_initializesLists() {
        Event nullDescEvent = new Event(null);

        assertNotNull("Event should not be null", nullDescEvent);
        assertNull("Description should be null", nullDescEvent.getDescription());
        assertNotNull("RegisteredEntrants list should still be initialized", nullDescEvent.getRegisteredEntrants());
        assertNotNull("WaitingList should still be initialized", nullDescEvent.getWaitingList());
        assertNotNull("DeclinedEntrants list should still be initialized", nullDescEvent.getDeclinedEntrants());
    }

    @Test
    public void testConstructor_listsAreArrayLists() {
        Event newEvent = new Event();

        assertTrue("RegisteredEntrants should be ArrayList", newEvent.getRegisteredEntrants() instanceof ArrayList);
        assertTrue("WaitingList should be ArrayList", newEvent.getWaitingList() instanceof ArrayList);
        assertTrue("DeclinedEntrants should be ArrayList", newEvent.getDeclinedEntrants() instanceof ArrayList);
    }

    // ========== ID Getter/Setter Tests ==========

    @Test
    public void testGetId_returnsCorrectValue() {
        assertEquals("ID should match", TEST_EVENT_ID, event.getId());
    }

    @Test
    public void testSetId_updatesValue() {
        String newId = "newEvent456";
        event.setId(newId);
        assertEquals("ID should be updated", newId, event.getId());
    }

    @Test
    public void testSetId_toNull_acceptsNull() {
        event.setId(null);
        assertNull("ID should be null", event.getId());
    }

    @Test
    public void testSetId_toEmptyString_acceptsEmptyString() {
        event.setId("");
        assertEquals("ID should be empty string", "", event.getId());
    }

    // ========== Description Getter/Setter Tests ==========

    @Test
    public void testGetDescription_returnsCorrectValue() {
        assertEquals("Description should match", testDescription, event.getDescription());
    }

    @Test
    public void testSetDescription_updatesValue() {
        EventDescription newDesc = new EventDescription(
            "New Event",
            "New Description",
            "2026-01-01",
            "2026-12-31",
            "2025-12-01",
            "2025-12-31",
            50,
            "http://example.com/new.jpg",
            "organizer456"
        );

        event.setDescription(newDesc);
        assertEquals("Description should be updated", newDesc, event.getDescription());
    }

    @Test
    public void testSetDescription_toNull_acceptsNull() {
        event.setDescription(null);
        assertNull("Description should be null", event.getDescription());
    }

    // ========== RegisteredEntrants List Tests ==========

    @Test
    public void testGetRegisteredEntrants_returnsEmptyList_initially() {
        Event newEvent = new Event();
        assertTrue("RegisteredEntrants should be empty initially", newEvent.getRegisteredEntrants().isEmpty());
    }

    @Test
    public void testRegisteredEntrants_isMutable_canAddItems() {
        List<String> entrants = event.getRegisteredEntrants();
        entrants.add(TEST_ENTRANT_1);

        assertEquals("Should have 1 entrant", 1, event.getRegisteredEntrants().size());
        assertTrue("Should contain the added entrant", event.getRegisteredEntrants().contains(TEST_ENTRANT_1));
    }

    @Test
    public void testRegisteredEntrants_isMutable_canRemoveItems() {
        event.getRegisteredEntrants().add(TEST_ENTRANT_1);
        event.getRegisteredEntrants().add(TEST_ENTRANT_2);

        event.getRegisteredEntrants().remove(TEST_ENTRANT_1);

        assertEquals("Should have 1 entrant", 1, event.getRegisteredEntrants().size());
        assertFalse("Should not contain removed entrant", event.getRegisteredEntrants().contains(TEST_ENTRANT_1));
        assertTrue("Should still contain second entrant", event.getRegisteredEntrants().contains(TEST_ENTRANT_2));
    }

    @Test
    public void testSetRegisteredEntrants_replacesEntireList() {
        List<String> newList = Arrays.asList(TEST_ENTRANT_1, TEST_ENTRANT_2, TEST_ENTRANT_3);
        event.setRegisteredEntrants(newList);

        assertEquals("Should have 3 entrants", 3, event.getRegisteredEntrants().size());
        assertTrue("Should contain all entrants", event.getRegisteredEntrants().containsAll(newList));
    }

    @Test
    public void testSetRegisteredEntrants_toNull_acceptsNull() {
        event.setRegisteredEntrants(null);
        assertNull("RegisteredEntrants should be null", event.getRegisteredEntrants());
    }

    @Test
    public void testSetRegisteredEntrants_toEmptyList_acceptsEmptyList() {
        event.getRegisteredEntrants().add(TEST_ENTRANT_1);
        event.setRegisteredEntrants(new ArrayList<>());

        assertNotNull("RegisteredEntrants should not be null", event.getRegisteredEntrants());
        assertTrue("RegisteredEntrants should be empty", event.getRegisteredEntrants().isEmpty());
    }

    // ========== WaitingList Tests ==========

    @Test
    public void testGetWaitingList_returnsEmptyList_initially() {
        Event newEvent = new Event();
        assertTrue("WaitingList should be empty initially", newEvent.getWaitingList().isEmpty());
    }

    @Test
    public void testWaitingList_isMutable_canAddItems() {
        event.getWaitingList().add(TEST_ENTRANT_1);
        event.getWaitingList().add(TEST_ENTRANT_2);

        assertEquals("Should have 2 entrants", 2, event.getWaitingList().size());
        assertTrue("Should contain entrant 1", event.getWaitingList().contains(TEST_ENTRANT_1));
        assertTrue("Should contain entrant 2", event.getWaitingList().contains(TEST_ENTRANT_2));
    }

    @Test
    public void testSetWaitingList_replacesEntireList() {
        List<String> newList = Arrays.asList(TEST_ENTRANT_1, TEST_ENTRANT_2);
        event.setWaitingList(newList);

        assertEquals("Should have 2 entrants", 2, event.getWaitingList().size());
        assertTrue("Should contain all entrants", event.getWaitingList().containsAll(newList));
    }

    @Test
    public void testSetWaitingList_toNull_acceptsNull() {
        event.setWaitingList(null);
        assertNull("WaitingList should be null", event.getWaitingList());
    }

    // ========== DeclinedEntrants List Tests ==========

    @Test
    public void testGetDeclinedEntrants_returnsEmptyList_initially() {
        Event newEvent = new Event();
        assertTrue("DeclinedEntrants should be empty initially", newEvent.getDeclinedEntrants().isEmpty());
    }

    @Test
    public void testDeclinedEntrants_isMutable_canAddItems() {
        event.getDeclinedEntrants().add(TEST_ENTRANT_1);

        assertEquals("Should have 1 entrant", 1, event.getDeclinedEntrants().size());
        assertTrue("Should contain the added entrant", event.getDeclinedEntrants().contains(TEST_ENTRANT_1));
    }

    @Test
    public void testSetDeclinedEntrants_replacesEntireList() {
        List<String> newList = Arrays.asList(TEST_ENTRANT_1, TEST_ENTRANT_2, TEST_ENTRANT_3);
        event.setDeclinedEntrants(newList);

        assertEquals("Should have 3 entrants", 3, event.getDeclinedEntrants().size());
        assertTrue("Should contain all entrants", event.getDeclinedEntrants().containsAll(newList));
    }

    @Test
    public void testSetDeclinedEntrants_toNull_acceptsNull() {
        event.setDeclinedEntrants(null);
        assertNull("DeclinedEntrants should be null", event.getDeclinedEntrants());
    }

    // ========== List Independence Tests ==========

    @Test
    public void testAllLists_areIndependent() {
        Event newEvent = new Event();

        newEvent.getRegisteredEntrants().add(TEST_ENTRANT_1);
        newEvent.getWaitingList().add(TEST_ENTRANT_2);
        newEvent.getDeclinedEntrants().add(TEST_ENTRANT_3);

        assertEquals("RegisteredEntrants should have 1 item", 1, newEvent.getRegisteredEntrants().size());
        assertEquals("WaitingList should have 1 item", 1, newEvent.getWaitingList().size());
        assertEquals("DeclinedEntrants should have 1 item", 1, newEvent.getDeclinedEntrants().size());

        assertFalse("Lists should not share items", newEvent.getRegisteredEntrants().contains(TEST_ENTRANT_2));
        assertFalse("Lists should not share items", newEvent.getWaitingList().contains(TEST_ENTRANT_3));
    }

    @Test
    public void testLists_doNotShareReferences() {
        Event event1 = new Event();
        Event event2 = new Event();

        event1.getRegisteredEntrants().add(TEST_ENTRANT_1);

        assertTrue("Event1 should contain entrant", event1.getRegisteredEntrants().contains(TEST_ENTRANT_1));
        assertFalse("Event2 should not contain entrant from Event1", event2.getRegisteredEntrants().contains(TEST_ENTRANT_1));
    }

    // ========== Edge Cases and Business Logic Tests ==========

    @Test
    public void testSameEntrant_canBeInMultipleLists() {
        // Document that no validation prevents this (though it may be a business rule violation)
        event.getRegisteredEntrants().add(TEST_ENTRANT_1);
        event.getWaitingList().add(TEST_ENTRANT_1);
        event.getDeclinedEntrants().add(TEST_ENTRANT_1);

        assertTrue("Entrant can be in RegisteredEntrants", event.getRegisteredEntrants().contains(TEST_ENTRANT_1));
        assertTrue("Entrant can be in WaitingList", event.getWaitingList().contains(TEST_ENTRANT_1));
        assertTrue("Entrant can be in DeclinedEntrants", event.getDeclinedEntrants().contains(TEST_ENTRANT_1));
    }

    @Test
    public void testDuplicateEntrants_canExistInSameList() {
        // Document that no validation prevents duplicates (though ArrayList allows it)
        event.getRegisteredEntrants().add(TEST_ENTRANT_1);
        event.getRegisteredEntrants().add(TEST_ENTRANT_1);

        assertEquals("List can contain duplicates", 2, event.getRegisteredEntrants().size());
    }

    @Test
    public void testEmptyStringEntrantId_canBeAdded() {
        event.getWaitingList().add("");

        assertTrue("Empty string can be added", event.getWaitingList().contains(""));
        assertEquals("Should have 1 entrant", 1, event.getWaitingList().size());
    }

    @Test
    public void testNullEntrantId_canBeAdded() {
        event.getWaitingList().add(null);

        assertTrue("Null can be added to list", event.getWaitingList().contains(null));
        assertEquals("Should have 1 entrant", 1, event.getWaitingList().size());
    }

    @Test
    public void testMovingEntrant_betweenLists() {
        // Simulate moving an entrant from waiting list to registered
        event.getWaitingList().add(TEST_ENTRANT_1);

        // Move to registered
        event.getWaitingList().remove(TEST_ENTRANT_1);
        event.getRegisteredEntrants().add(TEST_ENTRANT_1);

        assertFalse("Should not be in WaitingList", event.getWaitingList().contains(TEST_ENTRANT_1));
        assertTrue("Should be in RegisteredEntrants", event.getRegisteredEntrants().contains(TEST_ENTRANT_1));
    }

    @Test
    public void testLargeNumberOfEntrants_canBeAdded() {
        for (int i = 0; i < 10000; i++) {
            event.getWaitingList().add("entrant" + i);
        }

        assertEquals("Should have 10000 entrants", 10000, event.getWaitingList().size());
    }

    @Test
    public void testReplacingList_doesNotAffectOldReference() {
        List<String> originalList = event.getRegisteredEntrants();
        originalList.add(TEST_ENTRANT_1);

        List<String> newList = new ArrayList<>();
        newList.add(TEST_ENTRANT_2);
        event.setRegisteredEntrants(newList);

        // Original list should still contain TEST_ENTRANT_1
        assertTrue("Original list reference should be unchanged", originalList.contains(TEST_ENTRANT_1));
        assertFalse("Original list should not contain TEST_ENTRANT_2", originalList.contains(TEST_ENTRANT_2));

        // Event should have new list
        assertFalse("Event should not contain TEST_ENTRANT_1", event.getRegisteredEntrants().contains(TEST_ENTRANT_1));
        assertTrue("Event should contain TEST_ENTRANT_2", event.getRegisteredEntrants().contains(TEST_ENTRANT_2));
    }

    @Test
    public void testMultipleEvents_areIndependent() {
        Event event1 = new Event(testDescription);
        Event event2 = new Event(testDescription);

        event1.setId("event1");
        event2.setId("event2");

        event1.getRegisteredEntrants().add(TEST_ENTRANT_1);
        event2.getRegisteredEntrants().add(TEST_ENTRANT_2);

        assertEquals("Event1 should have 1 entrant", 1, event1.getRegisteredEntrants().size());
        assertEquals("Event2 should have 1 entrant", 1, event2.getRegisteredEntrants().size());
        assertNotEquals("Events should have different IDs", event1.getId(), event2.getId());
    }
}
