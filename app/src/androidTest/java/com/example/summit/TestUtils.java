package com.example.summit;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.view.View;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.matcher.ViewMatchers;

import org.hamcrest.Matcher;

/**
 * Utility class containing helper methods for Espresso UI tests.
 */
public class TestUtils {

    /**
     * Wait for a specified amount of time.
     * Useful for waiting for async operations like Firebase calls.
     *
     * @param millis Time to wait in milliseconds
     */
    public static void waitFor(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Custom ViewAction to wait for a view to become visible.
     * 
     * @param millis Maximum time to wait
     * @return ViewAction that waits
     */
    public static ViewAction waitForView(final long millis) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return ViewMatchers.isRoot();
            }

            @Override
            public String getDescription() {
                return "Wait for " + millis + " milliseconds.";
            }

            @Override
            public void perform(UiController uiController, View view) {
                uiController.loopMainThreadForAtLeast(millis);
            }
        };
    }

    /**
     * Fill in the details form with test data.
     * Used when navigating through the login flow.
     *
     * @param name User's name
     * @param email User's email
     * @param phone User's phone (optional)
     */
    public static void fillDetailsForm(String name, String email, String phone) {
        onView(withId(R.id.input_name))
                .perform(typeText(name), closeSoftKeyboard());
        
        onView(withId(R.id.input_email))
                .perform(typeText(email), closeSoftKeyboard());
        
        if (phone != null && !phone.isEmpty()) {
            onView(withId(R.id.input_phone))
                    .perform(typeText(phone), closeSoftKeyboard());
        }
    }

    /**
     * Navigate from DeviceIDFragment to DetailsFragment.
     * Clicks the continue button and waits for navigation.
     */
    public static void navigateToDetailsFragment() {
        onView(withId(R.id.continue_button))
                .perform(click());
        waitFor(2000);
    }

    /**
     * Navigate from DetailsFragment to RoleSelectionFragment.
     * Fills in required fields and submits.
     *
     * @param name User's name
     * @param email User's email
     */
    public static void navigateToRoleSelection(String name, String email) {
        fillDetailsForm(name, email, "");
        onView(withId(R.id.button_submit))
                .perform(click());
        waitFor(1000);
    }

    /**
     * Select entrant role and navigate to EntrantActivity.
     */
    public static void selectEntrantRole() {
        onView(withId(R.id.button_entrant))
                .perform(click());
        waitFor(1000);
    }

    /**
     * Select organizer role and navigate to OrganizerActivity.
     */
    public static void selectOrganizerRole() {
        onView(withId(R.id.button_organizer))
                .perform(click());
        waitFor(1000);
    }

    /**
     * Select admin role and navigate to AdminActivity.
     */
    public static void selectAdminRole() {
        onView(withId(R.id.button_admin))
                .perform(click());
        waitFor(1000);
    }

    /**
     * Navigate to Profile tab in EntrantActivity.
     */
    public static void navigateToProfileTab() {
        onView(withId(R.id.profileFragment))
                .perform(click());
        waitFor(1000);
    }

    /**
     * Navigate to Search tab in EntrantActivity.
     */
    public static void navigateToSearchTab() {
        onView(withId(R.id.searchForEventsFragment))
                .perform(click());
        waitFor(500);
    }

    /**
     * Navigate to Notifications tab in EntrantActivity.
     */
    public static void navigateToNotificationsTab() {
        onView(withId(R.id.notificationsFragment))
                .perform(click());
        waitFor(500);
    }

    /**
     * Safely check if a view is displayed without throwing exceptions.
     * Useful for conditional test logic based on current screen state.
     *
     * @param viewId Resource ID of the view to check
     * @return true if the view is displayed, false otherwise
     */
    public static boolean isViewDisplayedSafe(int viewId) {
        try {
            onView(withId(viewId)).check(
                    (view, noViewFoundException) -> {
                        if (noViewFoundException != null) {
                            throw noViewFoundException;
                        }
                        if (view.getVisibility() != android.view.View.VISIBLE) {
                            throw new AssertionError("View is not visible");
                        }
                    }
            );
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Test data constants
     */
    public static class TestData {
        public static final String TEST_USER_NAME = "Test User";
        public static final String TEST_USER_EMAIL = "testuser@example.com";
        public static final String TEST_USER_PHONE = "1234567890";
        
        public static final String TEST_ORGANIZER_NAME = "Test Organizer";
        public static final String TEST_ORGANIZER_EMAIL = "organizer@example.com";
        public static final String TEST_ORGANIZER_PHONE = "9876543210";
        
        public static final String TEST_ADMIN_NAME = "Test Admin";
        public static final String TEST_ADMIN_EMAIL = "admin@example.com";
        public static final String TEST_ADMIN_PHONE = "5551234567";
        
        public static final String TEST_EVENT_TITLE = "Swimming Lessons";
        public static final String TEST_EVENT_DESCRIPTION = "Learn to swim at the local pool";
        public static final String TEST_EVENT_LOCATION = "Community Recreation Center";
        public static final String TEST_EVENT_CAPACITY = "20";
    }
}
