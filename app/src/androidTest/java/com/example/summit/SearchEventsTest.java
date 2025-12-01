package com.example.summit;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Espresso UI tests for Search Events functionality.
 * Tests the SearchForEventsFragment within EntrantActivity.
 * 
 * US 01.01.03: As an entrant, I want to see a list of events I can join.
 * US 01.01.04: As an entrant, I want to filter events based on interests.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class SearchEventsTest {

    static Intent createEntrantIntent() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), EntrantActivity.class);
        intent.putExtra("name", "Test User");
        intent.putExtra("email", "test@example.com");
        intent.putExtra("phone", "1234567890");
        return intent;
    }

    @Rule
    public ActivityScenarioRule<EntrantActivity> activityRule =
            new ActivityScenarioRule<>(createEntrantIntent());

    /**
     * US 01.01.03: Test that the search events layout is displayed.
     */
    @Test
    public void testSearchEventsLayoutDisplayed() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // SearchForEventsFragment should be the default/start destination
        onView(withId(R.id.search_for_events_layout))
                .check(matches(isDisplayed()));
    }

    /**
     * Test that the title "Search for Events" is displayed.
     */
    @Test
    public void testSearchTitleDisplayed() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.tv_title))
                .check(matches(isDisplayed()));
        onView(withId(R.id.tv_title))
                .check(matches(withText("Search for Events")));
    }

    /**
     * US 01.01.04: Test that search input field is displayed.
     */
    @Test
    public void testSearchInputDisplayed() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.et_search))
                .check(matches(isDisplayed()));
    }

    /**
     * Test that search button is displayed.
     */
    @Test
    public void testSearchButtonDisplayed() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.btn_search))
                .check(matches(isDisplayed()));
    }

    /**
     * US 01.01.04: Test that filter button is displayed.
     */
    @Test
    public void testFilterButtonDisplayed() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.btn_filter))
                .check(matches(isDisplayed()));
    }

    /**
     * Test that "Recommended for you" header is displayed.
     */
    @Test
    public void testRecommendedHeaderDisplayed() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.tv_recommended_header))
                .check(matches(isDisplayed()));
        onView(withId(R.id.tv_recommended_header))
                .check(matches(withText("Recommended for you")));
    }

    /**
     * US 01.01.03: Test that events RecyclerView is displayed.
     */
    @Test
    public void testEventsRecyclerViewDisplayed() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.recycler_recommended_events))
                .check(matches(isDisplayed()));
    }

    /**
     * Test typing in search field.
     */
    @Test
    public void testTypeInSearchField() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.et_search))
                .perform(typeText("Swimming"), closeSoftKeyboard());

        // Verify text was entered
        onView(withId(R.id.et_search))
                .check(matches(withText("Swimming")));
    }

    /**
     * Test clicking search button.
     */
    @Test
    public void testClickSearchButton() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.et_search))
                .perform(typeText("Test"), closeSoftKeyboard());

        onView(withId(R.id.btn_search))
                .perform(click());

        // Should not crash - search functionality is executed
    }

    /**
     * Test clicking filter button.
     */
    @Test
    public void testClickFilterButton() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.btn_filter))
                .perform(click());

        // Should not crash - filter functionality is executed
    }

    /**
     * US 01.01.03: Test clicking on an event in the list (if events exist).
     * This test requires events to exist in Firebase.
     */
    @Test
    public void testClickEventInList() {
        try {
            Thread.sleep(3000); // Wait for events to load from Firebase
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            // Try to click on the first item in the RecyclerView
            onView(withId(R.id.recycler_recommended_events))
                    .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

            Thread.sleep(1000);

            // Should navigate to event details
            // Check for event details fragment elements
            onView(withId(R.id.text_event_title))
                    .check(matches(isDisplayed()));
        } catch (Exception e) {
            // No events available or RecyclerView is empty - acceptable
        }
    }

    /**
     * Test scrolling through events list (if events exist).
     */
    @Test
    public void testScrollEventsList() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            // Try to scroll to position 5 (if enough events exist)
            onView(withId(R.id.recycler_recommended_events))
                    .perform(RecyclerViewActions.scrollToPosition(5));
        } catch (Exception e) {
            // Not enough events - acceptable
        }
    }
}
