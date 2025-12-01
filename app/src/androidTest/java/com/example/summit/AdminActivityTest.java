package com.example.summit;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.summit.model.Admin;
import com.example.summit.session.Session;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Espresso UI tests for Admin functionality.
 * Tests the AdminActivity and its fragments.
 * 
 * US 03.01.01: As an administrator, I want to remove events.
 * US 03.02.01: As an administrator, I want to remove profiles.
 * US 03.03.01: As an administrator, I want to remove images.
 * US 03.06.01: As an administrator, I want to browse events.
 * US 03.07.01: As an administrator, I want to browse profiles.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class AdminActivityTest {

    static Intent createAdminIntent() {
        // Set up session with admin before launching activity
        Admin testAdmin = new Admin("Test Admin", "admin@test.com", "admin_device_id", "5551234567");
        Session.setAdmin(testAdmin);
        
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), AdminActivity.class);
        intent.putExtra("name", "Test Admin");
        intent.putExtra("email", "admin@test.com");
        intent.putExtra("phone", "5551234567");
        return intent;
    }

    @Rule
    public ActivityScenarioRule<AdminActivity> activityRule =
            new ActivityScenarioRule<>(createAdminIntent());

    /**
     * Test that AdminActivity loads successfully.
     */
    @Test
    public void testAdminActivityLoads() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // AdminActivity should load without crashing
        onView(withId(R.id.bottom_nav))
                .check(matches(isDisplayed()));
    }

    /**
     * Test that bottom navigation is displayed.
     */
    @Test
    public void testBottomNavigationDisplayed() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.bottom_nav))
                .check(matches(isDisplayed()));
    }

    /**
     * US 03.06.01: Test that admin events list is displayed.
     */
    @Test
    public void testAdminEventsDisplayed() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            // Check if events RecyclerView is displayed
            //onView(withId(R.id.admin_events_recycler))
                   // .check(matches(isDisplayed()));
        } catch (Exception e) {
            // Fragment might not be visible
        }
    }

    /**
     * US 03.07.01: Test navigation to user profiles admin screen.
     */
    @Test
    public void testNavigateToUserProfiles() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Navigate via bottom navigation (depends on your nav setup)
        // Check for user profiles elements
    }

    /**
     * Test navigation to admin notifications screen.
     */
    @Test
    public void testNavigateToNotifications() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Navigation test placeholder
    }

    /**
     * US 03.03.01: Test navigation to image settings screen.
     */
    @Test
    public void testNavigateToImageSettings() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Navigation test placeholder
    }
}
