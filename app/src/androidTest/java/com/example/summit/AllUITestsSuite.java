package com.example.summit;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Test Suite that runs all Espresso UI tests.
 * 
 * Run this suite to execute all UI tests at once:
 * - LoginFlowTest: Tests login/registration flow (US 01.02.01)
 * - RoleSelectionTest: Tests role selection buttons
 * - EntrantProfileTest: Tests entrant profile functionality (US 01.02.02, US 01.06.01)
 * - SearchEventsTest: Tests event searching functionality (US 01.01.03, US 01.01.04)
 * - EventJoinTest: Tests event viewing and joining (US 01.01.01, US 01.05.01)
 * - CreateEventTest: Tests organizer event creation (US 02.01.01, US 02.01.02)
 * - OrganizerFlowTest: Tests complete organizer flow (US 02.02.02, US 01.06.02)
 * - NotificationsTest: Tests notifications functionality (US 01.04.01)
 * - AdminActivityTest: Tests admin functionality (US 03.*)
 * - BottomNavigationTest: Tests navigation between screens
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        LoginFlowTest.class,
        RoleSelectionTest.class,
        EntrantProfileTest.class,
        SearchEventsTest.class,
        EventJoinTest.class,
        NotificationsTest.class,
        BottomNavigationTest.class,
        CreateEventTest.class,
        OrganizerFlowTest.class,
        AdminActivityTest.class
})
public class AllUITestsSuite {
    // This class is gonna stay empty, it's used only as a holder for the above annotations
}
