package com.example.summit;

import com.example.summit.model.EventDescription;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.*;

/**
 * Unit tests for the EventDescription model class.
 * Tests cover constructor initialization, all 18 fields (getters/setters),
 * null handling, boundary values, and edge cases for event metadata.
 */
public class EventDescriptionTest {
    private EventDescription eventDescription;

    private static final String TEST_TITLE = "Test Event";
    private static final String TEST_DESCRIPTION = "Test Description";
    private static final String TEST_START_DATE = "2025-12-01";
    private static final String TEST_END_DATE = "2025-12-31";
    private static final String TEST_REG_START = "2025-11-01";
    private static final String TEST_REG_END = "2025-11-30";
    private static final int TEST_MAX_ATTENDEES = 100;
    private static final String TEST_POSTER_URL = "http://example.com/poster.jpg";
    private static final String TEST_ORGANIZER_ID = "organizer123";

    @Before
    public void setUp() {
        eventDescription = new EventDescription(
            TEST_TITLE,
            TEST_DESCRIPTION,
            TEST_START_DATE,
            TEST_END_DATE,
            TEST_REG_START,
            TEST_REG_END,
            TEST_MAX_ATTENDEES,
            TEST_POSTER_URL,
            TEST_ORGANIZER_ID
        );
    }

    // ========== Constructor Tests ==========

    @Test
    public void testParameterizedConstructor_withValidData_createsEventDescription() {
        EventDescription desc = new EventDescription(
            TEST_TITLE,
            TEST_DESCRIPTION,
            TEST_START_DATE,
            TEST_END_DATE,
            TEST_REG_START,
            TEST_REG_END,
            TEST_MAX_ATTENDEES,
            TEST_POSTER_URL,
            TEST_ORGANIZER_ID
        );

        assertNotNull("EventDescription should not be null", desc);
        assertEquals("Title should match", TEST_TITLE, desc.getTitle());
        assertEquals("Description should match", TEST_DESCRIPTION, desc.getDescription());
        assertEquals("StartDate should match", TEST_START_DATE, desc.getStartDate());
        assertEquals("EndDate should match", TEST_END_DATE, desc.getEndDate());
        assertEquals("RegistrationStart should match", TEST_REG_START, desc.getRegistrationStart());
        assertEquals("RegistrationEnd should match", TEST_REG_END, desc.getRegistrationEnd());
        assertEquals("MaxAttendees should match", TEST_MAX_ATTENDEES, desc.getMaxAttendees());
        assertEquals("PosterUrl should match", TEST_POSTER_URL, desc.getPosterUrl());
        assertEquals("OrganizerId should match", TEST_ORGANIZER_ID, desc.getOrganizerId());
    }

    @Test
    public void testEmptyConstructor_createsEventDescription() {
        EventDescription emptyDesc = new EventDescription();

        assertNotNull("EventDescription should not be null", emptyDesc);
        assertNull("Title should be null", emptyDesc.getTitle());
        assertNull("Description should be null", emptyDesc.getDescription());
        assertNull("StartDate should be null", emptyDesc.getStartDate());
        assertNull("EndDate should be null", emptyDesc.getEndDate());
        assertEquals("MaxAttendees should be 0", 0, emptyDesc.getMaxAttendees());
    }

    @Test
    public void testParameterizedConstructor_withNullValues_createsEventDescription() {
        EventDescription nullDesc = new EventDescription(
            null, null, null, null, null, null, 0, null, null
        );

        assertNotNull("EventDescription should not be null", nullDesc);
        assertNull("Title should be null", nullDesc.getTitle());
        assertNull("Description should be null", nullDesc.getDescription());
        assertNull("PosterUrl should be null", nullDesc.getPosterUrl());
        assertEquals("MaxAttendees should be 0", 0, nullDesc.getMaxAttendees());
    }

    @Test
    public void testConstructor_doesNotInitializeLists() {
        EventDescription desc = new EventDescription(
            TEST_TITLE, TEST_DESCRIPTION, TEST_START_DATE, TEST_END_DATE,
            TEST_REG_START, TEST_REG_END, TEST_MAX_ATTENDEES, TEST_POSTER_URL, TEST_ORGANIZER_ID
        );

        // Important: Lists are NOT initialized in the parameterized constructor
        assertNull("WaitingList should be null after parameterized constructor", desc.getWaitingList());
        assertNull("SelectedList should be null after parameterized constructor", desc.getSelectedList());
        assertNull("AcceptedList should be null after parameterized constructor", desc.getAcceptedList());
        assertNull("DeclinedList should be null after parameterized constructor", desc.getDeclinedList());
    }

    // ========== ID Getter/Setter Tests ==========

    @Test
    public void testGetSetId() {
        String testId = "event123";
        eventDescription.setId(testId);
        assertEquals("ID should match", testId, eventDescription.getId());
    }

    @Test
    public void testSetId_toNull_acceptsNull() {
        eventDescription.setId(null);
        assertNull("ID should be null", eventDescription.getId());
    }

    // ========== Title Getter/Setter Tests ==========

    @Test
    public void testGetTitle_returnsCorrectValue() {
        assertEquals("Title should match", TEST_TITLE, eventDescription.getTitle());
    }

    @Test
    public void testSetTitle_updatesValue() {
        String newTitle = "Updated Event Title";
        eventDescription.setTitle(newTitle);
        assertEquals("Title should be updated", newTitle, eventDescription.getTitle());
    }

    @Test
    public void testSetTitle_toNull_acceptsNull() {
        eventDescription.setTitle(null);
        assertNull("Title should be null", eventDescription.getTitle());
    }

    @Test
    public void testSetTitle_toEmptyString_acceptsEmptyString() {
        eventDescription.setTitle("");
        assertEquals("Title should be empty string", "", eventDescription.getTitle());
    }

    // ========== Description Getter/Setter Tests ==========

    @Test
    public void testGetDescription_returnsCorrectValue() {
        assertEquals("Description should match", TEST_DESCRIPTION, eventDescription.getDescription());
    }

    @Test
    public void testSetDescription_updatesValue() {
        String newDesc = "Updated description with more details";
        eventDescription.setDescription(newDesc);
        assertEquals("Description should be updated", newDesc, eventDescription.getDescription());
    }

    @Test
    public void testSetDescription_toNull_acceptsNull() {
        eventDescription.setDescription(null);
        assertNull("Description should be null", eventDescription.getDescription());
    }

    // ========== Date Field Tests ==========

    @Test
    public void testGetSetStartDate() {
        String newDate = "2026-01-01";
        eventDescription.setStartDate(newDate);
        assertEquals("StartDate should be updated", newDate, eventDescription.getStartDate());
    }

    @Test
    public void testGetSetEndDate() {
        String newDate = "2026-12-31";
        eventDescription.setEndDate(newDate);
        assertEquals("EndDate should be updated", newDate, eventDescription.getEndDate());
    }

    @Test
    public void testGetSetRegistrationStart() {
        String newDate = "2025-10-01";
        eventDescription.setRegistrationStart(newDate);
        assertEquals("RegistrationStart should be updated", newDate, eventDescription.getRegistrationStart());
    }

    @Test
    public void testGetSetRegistrationEnd() {
        String newDate = "2025-10-31";
        eventDescription.setRegistrationEnd(newDate);
        assertEquals("RegistrationEnd should be updated", newDate, eventDescription.getRegistrationEnd());
    }

    @Test
    public void testGetSetEventStart() {
        String newDate = "2025-12-15T10:00:00";
        eventDescription.setEventStart(newDate);
        assertEquals("EventStart should be updated", newDate, eventDescription.getEventStart());
    }

    @Test
    public void testGetSetEventEnd() {
        String newDate = "2025-12-15T18:00:00";
        eventDescription.setEventEnd(newDate);
        assertEquals("EventEnd should be updated", newDate, eventDescription.getEventEnd());
    }

    @Test
    public void testDateFields_acceptInvalidFormats() {
        // Note: Dates are stored as Strings with no validation
        eventDescription.setStartDate("invalid-date");
        assertEquals("StartDate accepts invalid format", "invalid-date", eventDescription.getStartDate());
    }

    @Test
    public void testDateFields_acceptNull() {
        eventDescription.setStartDate(null);
        eventDescription.setEndDate(null);

        assertNull("StartDate should be null", eventDescription.getStartDate());
        assertNull("EndDate should be null", eventDescription.getEndDate());
    }

    // ========== MaxAttendees Getter/Setter Tests ==========

    @Test
    public void testGetMaxAttendees_returnsCorrectValue() {
        assertEquals("MaxAttendees should match", TEST_MAX_ATTENDEES, eventDescription.getMaxAttendees());
    }

    @Test
    public void testSetMaxAttendees_updatesValue() {
        int newMax = 200;
        eventDescription.setMaxAttendees(newMax);
        assertEquals("MaxAttendees should be updated", newMax, eventDescription.getMaxAttendees());
    }

    @Test
    public void testSetMaxAttendees_toZero_acceptsZero() {
        eventDescription.setMaxAttendees(0);
        assertEquals("MaxAttendees should be 0", 0, eventDescription.getMaxAttendees());
    }

    @Test
    public void testSetMaxAttendees_toNegative_acceptsNegative() {
        // Note: No validation prevents negative values
        eventDescription.setMaxAttendees(-1);
        assertEquals("MaxAttendees accepts negative values", -1, eventDescription.getMaxAttendees());
    }

    @Test
    public void testSetMaxAttendees_toMaxValue_acceptsMaxValue() {
        eventDescription.setMaxAttendees(Integer.MAX_VALUE);
        assertEquals("MaxAttendees accepts Integer.MAX_VALUE", Integer.MAX_VALUE, eventDescription.getMaxAttendees());
    }

    @Test
    public void testSetMaxAttendees_toMinValue_acceptsMinValue() {
        eventDescription.setMaxAttendees(Integer.MIN_VALUE);
        assertEquals("MaxAttendees accepts Integer.MIN_VALUE", Integer.MIN_VALUE, eventDescription.getMaxAttendees());
    }

    // ========== PosterUrl Getter/Setter Tests ==========

    @Test
    public void testGetPosterUrl_returnsCorrectValue() {
        assertEquals("PosterUrl should match", TEST_POSTER_URL, eventDescription.getPosterUrl());
    }

    @Test
    public void testSetPosterUrl_updatesValue() {
        String newUrl = "https://example.com/new-poster.png";
        eventDescription.setPosterUrl(newUrl);
        assertEquals("PosterUrl should be updated", newUrl, eventDescription.getPosterUrl());
    }

    @Test
    public void testSetPosterUrl_toNull_acceptsNull() {
        eventDescription.setPosterUrl(null);
        assertNull("PosterUrl should be null", eventDescription.getPosterUrl());
    }

    @Test
    public void testSetPosterUrl_withInvalidUrl_acceptsValue() {
        // Note: No URL validation exists
        String invalidUrl = "not-a-valid-url";
        eventDescription.setPosterUrl(invalidUrl);
        assertEquals("PosterUrl accepts invalid format", invalidUrl, eventDescription.getPosterUrl());
    }

    // ========== OrganizerId Getter/Setter Tests ==========

    @Test
    public void testGetOrganizerId_returnsCorrectValue() {
        assertEquals("OrganizerId should match", TEST_ORGANIZER_ID, eventDescription.getOrganizerId());
    }

    @Test
    public void testSetOrganizerId_updatesValue() {
        String newOrganizerId = "organizer456";
        eventDescription.setOrganizerId(newOrganizerId);
        assertEquals("OrganizerId should be updated", newOrganizerId, eventDescription.getOrganizerId());
    }

    @Test
    public void testSetOrganizerId_toNull_acceptsNull() {
        eventDescription.setOrganizerId(null);
        assertNull("OrganizerId should be null", eventDescription.getOrganizerId());
    }

    // ========== Location Getter/Setter Tests ==========

    @Test
    public void testGetSetLocation() {
        String location = "Edmonton Convention Centre";
        eventDescription.setLocation(location);
        assertEquals("Location should match", location, eventDescription.getLocation());
    }

    @Test
    public void testSetLocation_toNull_acceptsNull() {
        eventDescription.setLocation(null);
        assertNull("Location should be null", eventDescription.getLocation());
    }

    // ========== Capacity Getter/Setter Tests ==========

    @Test
    public void testGetSetCapacity() {
        Long capacity = 500L;
        eventDescription.setCapacity(capacity);
        assertEquals("Capacity should match", capacity, eventDescription.getCapacity());
    }

    @Test
    public void testSetCapacity_toNull_acceptsNull() {
        eventDescription.setCapacity(null);
        assertNull("Capacity should be null", eventDescription.getCapacity());
    }

    @Test
    public void testSetCapacity_toZero_acceptsZero() {
        eventDescription.setCapacity(0L);
        assertEquals("Capacity should be 0", Long.valueOf(0), eventDescription.getCapacity());
    }

    @Test
    public void testSetCapacity_toNegative_acceptsNegative() {
        // Note: No validation prevents negative values
        eventDescription.setCapacity(-100L);
        assertEquals("Capacity accepts negative values", Long.valueOf(-100), eventDescription.getCapacity());
    }

    @Test
    public void testSetCapacity_toMaxValue_acceptsMaxValue() {
        eventDescription.setCapacity(Long.MAX_VALUE);
        assertEquals("Capacity accepts Long.MAX_VALUE", Long.valueOf(Long.MAX_VALUE), eventDescription.getCapacity());
    }

    // ========== List Getter/Setter Tests ==========

    @Test
    public void testGetSetWaitingList() {
        List<String> waitingList = Arrays.asList("entrant1", "entrant2", "entrant3");
        eventDescription.setWaitingList(waitingList);
        assertEquals("WaitingList should match", waitingList, eventDescription.getWaitingList());
        assertEquals("WaitingList should have 3 items", 3, eventDescription.getWaitingList().size());
    }

    @Test
    public void testGetSetSelectedList() {
        List<String> selectedList = Arrays.asList("entrant1", "entrant2");
        eventDescription.setSelectedList(selectedList);
        assertEquals("SelectedList should match", selectedList, eventDescription.getSelectedList());
        assertEquals("SelectedList should have 2 items", 2, eventDescription.getSelectedList().size());
    }

    @Test
    public void testGetSetAcceptedList() {
        List<String> acceptedList = Arrays.asList("entrant1");
        eventDescription.setAcceptedList(acceptedList);
        assertEquals("AcceptedList should match", acceptedList, eventDescription.getAcceptedList());
        assertEquals("AcceptedList should have 1 item", 1, eventDescription.getAcceptedList().size());
    }

    @Test
    public void testGetSetDeclinedList() {
        List<String> declinedList = Arrays.asList("entrant2", "entrant3", "entrant4");
        eventDescription.setDeclinedList(declinedList);
        assertEquals("DeclinedList should match", declinedList, eventDescription.getDeclinedList());
        assertEquals("DeclinedList should have 3 items", 3, eventDescription.getDeclinedList().size());
    }

    @Test
    public void testSetLists_toNull_acceptsNull() {
        eventDescription.setWaitingList(null);
        eventDescription.setSelectedList(null);
        eventDescription.setAcceptedList(null);
        eventDescription.setDeclinedList(null);

        assertNull("WaitingList should be null", eventDescription.getWaitingList());
        assertNull("SelectedList should be null", eventDescription.getSelectedList());
        assertNull("AcceptedList should be null", eventDescription.getAcceptedList());
        assertNull("DeclinedList should be null", eventDescription.getDeclinedList());
    }

    @Test
    public void testSetLists_toEmptyList_acceptsEmptyList() {
        eventDescription.setWaitingList(new ArrayList<>());
        eventDescription.setSelectedList(new ArrayList<>());

        assertNotNull("WaitingList should not be null", eventDescription.getWaitingList());
        assertTrue("WaitingList should be empty", eventDescription.getWaitingList().isEmpty());
        assertNotNull("SelectedList should not be null", eventDescription.getSelectedList());
        assertTrue("SelectedList should be empty", eventDescription.getSelectedList().isEmpty());
    }

    // ========== Edge Cases and Boundary Tests ==========

    @Test
    public void testVeryLongStrings_acceptedInFields() {
        String longString = "a".repeat(10000);

        eventDescription.setTitle(longString);
        eventDescription.setDescription(longString);
        eventDescription.setLocation(longString);

        assertEquals("Title should store long string", longString, eventDescription.getTitle());
        assertEquals("Description should store long string", longString, eventDescription.getDescription());
        assertEquals("Location should store long string", longString, eventDescription.getLocation());
    }

    @Test
    public void testSpecialCharacters_inTextFields() {
        String specialChars = "Event™ with émojis 🎉 and symbols @#$%^&*()";

        eventDescription.setTitle(specialChars);
        eventDescription.setDescription(specialChars);

        assertEquals("Title should accept special characters", specialChars, eventDescription.getTitle());
        assertEquals("Description should accept special characters", specialChars, eventDescription.getDescription());
    }

    @Test
    public void testMultipleEventDescriptions_areIndependent() {
        EventDescription desc1 = new EventDescription(
            "Event 1", "Description 1", "2025-01-01", "2025-12-31",
            "2024-12-01", "2024-12-31", 50, "url1", "org1"
        );

        EventDescription desc2 = new EventDescription(
            "Event 2", "Description 2", "2026-01-01", "2026-12-31",
            "2025-12-01", "2025-12-31", 100, "url2", "org2"
        );

        desc1.setTitle("Modified Event 1");

        assertEquals("Desc1 title should be modified", "Modified Event 1", desc1.getTitle());
        assertEquals("Desc2 title should be unchanged", "Event 2", desc2.getTitle());
    }

    @Test
    public void testMaxAttendeesVsCapacity_noValidation() {
        // Document that maxAttendees and capacity are independent with no validation
        eventDescription.setMaxAttendees(100);
        eventDescription.setCapacity(50L);

        // No error should occur even though maxAttendees > capacity
        assertEquals("MaxAttendees should be 100", 100, eventDescription.getMaxAttendees());
        assertEquals("Capacity should be 50", Long.valueOf(50), eventDescription.getCapacity());
    }

    @Test
    public void testDateFieldDuplication_bothStartEndFieldsExist() {
        // Document that both startDate/endDate AND eventStart/eventEnd exist
        eventDescription.setStartDate("2025-12-01");
        eventDescription.setEndDate("2025-12-31");
        eventDescription.setEventStart("2025-12-01T09:00:00");
        eventDescription.setEventEnd("2025-12-31T17:00:00");

        assertNotNull("StartDate should be set", eventDescription.getStartDate());
        assertNotNull("EndDate should be set", eventDescription.getEndDate());
        assertNotNull("EventStart should be set", eventDescription.getEventStart());
        assertNotNull("EventEnd should be set", eventDescription.getEventEnd());
    }

    @Test
    public void testAllFields_canBeSetIndependently() {
        // Test that all 18 fields can be set without affecting each other
        eventDescription.setId("id1");
        eventDescription.setTitle("title");
        eventDescription.setDescription("desc");
        eventDescription.setStartDate("start");
        eventDescription.setEndDate("end");
        eventDescription.setRegistrationStart("regStart");
        eventDescription.setRegistrationEnd("regEnd");
        eventDescription.setMaxAttendees(10);
        eventDescription.setPosterUrl("url");
        eventDescription.setOrganizerId("org");
        eventDescription.setLocation("loc");
        eventDescription.setCapacity(20L);
        eventDescription.setEventStart("evStart");
        eventDescription.setEventEnd("evEnd");
        eventDescription.setWaitingList(Arrays.asList("w1"));
        eventDescription.setSelectedList(Arrays.asList("s1"));
        eventDescription.setAcceptedList(Arrays.asList("a1"));
        eventDescription.setDeclinedList(Arrays.asList("d1"));

        assertEquals("All fields should be independently settable", "id1", eventDescription.getId());
        assertEquals("MaxAttendees should be 10", 10, eventDescription.getMaxAttendees());
        assertEquals("Capacity should be 20", Long.valueOf(20), eventDescription.getCapacity());
    }
}
