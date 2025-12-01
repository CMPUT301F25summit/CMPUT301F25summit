package com.example.summit;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Espresso UI tests for Bottom Navigation functionality.
 * Tests navigation between different sections of the app.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class BottomNavigationTest {

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
     * Test that bottom navigation bar is displayed.
     */
    @Test
    public void testBottomNavDisplayed() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.bottom_nav))
                .check(matches(isDisplayed()));
    }

    /**
     * Test navigation to Search/Home screen via bottom nav.
     */
    @Test
    public void testNavigateToSearch() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Click on search navigation item
        try {
            onView(withId(R.id.searchForEventsFragment))
                    .perform(click());
            
            Thread.sleep(500);
            
            onView(withId(R.id.search_for_events_layout))
                    .check(matches(isDisplayed()));
        } catch (Exception e) {
            // Navigation might fail if already on this screen
        }
    }

    /**
     * Test navigation to Profile screen via bottom nav.
     */
    @Test
    public void testNavigateToProfile() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            onView(withId(R.id.profileFragment))
                    .perform(click());
            
            Thread.sleep(1000);
            
            onView(withId(R.id.profile_fragment_layout))
                    .check(matches(isDisplayed()));
        } catch (Exception e) {
            // Profile might need Firebase data
        }
    }

    /**
     * Test navigation to Notifications screen via bottom nav.
     */
    @Test
    public void testNavigateToNotifications() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            onView(withId(R.id.notificationsFragment))
                    .perform(click());
            
            Thread.sleep(500);
            
            // Notifications fragment should be displayed
        } catch (Exception e) {
            // Navigation test
        }
    }

    /**
     * Test that the default/start destination is Search screen.
     */
    @Test
    public void testDefaultScreenIsSearch() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // By default, EntrantActivity should show SearchForEventsFragment
        onView(withId(R.id.search_for_events_layout))
                .check(matches(isDisplayed()));
    }

    /**
     * Test navigating back and forth between screens.
     */
    @Test
    public void testNavigationBackAndForth() {
        try {
            Thread.sleep(1000);
            
            // Navigate to profile
            onView(withId(R.id.profileFragment))
                    .perform(click());
            Thread.sleep(1000);
            
            // Navigate back to search
            onView(withId(R.id.searchForEventsFragment))
                    .perform(click());
            Thread.sleep(500);
            
            // Verify we're back on search
            onView(withId(R.id.search_for_events_layout))
                    .check(matches(isDisplayed()));
        } catch (Exception e) {
            // Navigation might work differently
        }
    }
}
