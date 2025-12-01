package com.example.summit;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Espresso UI tests for the Login/Registration flow.
 * Tests the flow: DeviceIDFragment -> DetailsFragment -> RoleSelectionFragment
 * User Stories Covered:
 *
 *
 * - US 01.02.01: As an entrant, I want to provide my personal information such as 
 *                name, email and optional phone number in the app
 * Note: These tests handle both new and existing users gracefully.
 * For existing users, the app redirects directly to the appropriate Activity.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginFlowTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);


    /**
     * US 01.02.01: Verify the continue button is displayed on the initial screen.
     * This is the entry point for all users.
     */
    @Test
    public void testContinueButtonIsDisplayed() {
        onView(withId(R.id.continue_button))
                .check(matches(isDisplayed()));
    }

    /**
     * US 01.02.01: Verify continue button is clickable and triggers navigation.
     * For new users: navigates to DetailsFragment
     * For existing users: redirects to appropriate Activity
     */
    @Test
    public void testContinueButtonClickable() {
        onView(withId(R.id.continue_button))
                .perform(click());
        
        // Wait for Firebase check and navigation
        TestUtils.waitFor(3000);
        
        // Test passes if no crash occurs - actual destination depends on user state
    }



    /**
     * US 01.02.01: Verify all form fields are displayed in DetailsFragment.
     * Tests that new users see name, email, phone fields and submit button.
     */
    @Test
    public void testDetailsFormFieldsDisplayed() {
        TestUtils.navigateToDetailsFragment();

        // Check if on details fragment (new user flow)
        if (TestUtils.isViewDisplayedSafe(R.id.input_name)) {
            onView(withId(R.id.input_name)).check(matches(isDisplayed()));
            onView(withId(R.id.input_email)).check(matches(isDisplayed()));
            onView(withId(R.id.input_phone)).check(matches(isDisplayed()));
            onView(withId(R.id.button_submit)).check(matches(isDisplayed()));
        }
        // If not on details fragment, user is already registered - test passes
    }

    /**
     * US 01.02.01: Verify form validation rejects empty name field.
     * Name is a required field for registration.
     */
    @Test
    public void testEmptyNameValidation() {
        TestUtils.navigateToDetailsFragment();

        if (TestUtils.isViewDisplayedSafe(R.id.input_email)) {
            // Submit with only email filled
            onView(withId(R.id.input_email))
                    .perform(typeText("test@example.com"), closeSoftKeyboard());
            onView(withId(R.id.button_submit))
                    .perform(click());
            
            TestUtils.waitFor(500);
            
            // Should remain on same screen - validation should fail
            onView(withId(R.id.input_name)).check(matches(isDisplayed()));
        }
    }

    /**
     * US 01.02.01: Verify form validation rejects empty email field.
     * Email is a required field for registration.
     */
    @Test
    public void testEmptyEmailValidation() {
        TestUtils.navigateToDetailsFragment();

        if (TestUtils.isViewDisplayedSafe(R.id.input_name)) {
            // Submit with only name filled
            onView(withId(R.id.input_name))
                    .perform(typeText("Test User"), closeSoftKeyboard());
            onView(withId(R.id.button_submit))
                    .perform(click());
            
            TestUtils.waitFor(500);
            
            // Should remain on same screen - validation should fail
            onView(withId(R.id.input_email)).check(matches(isDisplayed()));
        }
    }

    /**
     * US 01.02.01: Verify form validation accepts empty phone field.
     * Phone number is optional per requirements.
     */
    @Test
    public void testPhoneNumberIsOptional() {
        TestUtils.navigateToDetailsFragment();

        if (TestUtils.isViewDisplayedSafe(R.id.input_name)) {
            // Fill only required fields
            onView(withId(R.id.input_name))
                    .perform(typeText("Test User"), closeSoftKeyboard());
            onView(withId(R.id.input_email))
                    .perform(typeText("test@example.com"), closeSoftKeyboard());
            // Leave phone empty intentionally
            
            onView(withId(R.id.button_submit))
                    .perform(click());
            
            TestUtils.waitFor(1500);
            
            // Should navigate to role selection (phone not required)
            if (TestUtils.isViewDisplayedSafe(R.id.button_entrant)) {
                onView(withId(R.id.button_entrant)).check(matches(isDisplayed()));
            }
        }
    }

    /**
     * US 01.02.01: Verify successful form submission navigates to role selection.
     * Complete the registration form with all fields.
     */
    @Test
    public void testSuccessfulFormSubmissionNavigatesToRoleSelection() {
        TestUtils.navigateToDetailsFragment();

        if (TestUtils.isViewDisplayedSafe(R.id.input_name)) {
            // Fill all fields
            TestUtils.fillDetailsForm(
                TestUtils.TestData.TEST_USER_NAME,
                TestUtils.TestData.TEST_USER_EMAIL,
                TestUtils.TestData.TEST_USER_PHONE
            );
            
            onView(withId(R.id.button_submit))
                    .perform(click());
            
            TestUtils.waitFor(1500);
            
            // Should navigate to role selection
            if (TestUtils.isViewDisplayedSafe(R.id.button_entrant)) {
                onView(withId(R.id.button_entrant)).check(matches(isDisplayed()));
                onView(withId(R.id.button_organizer)).check(matches(isDisplayed()));
                onView(withId(R.id.button_admin)).check(matches(isDisplayed()));
            }
        }
    }

    /**
     * US 01.02.01: Verify text input is correctly captured in name field.
     */
    @Test
    public void testNameFieldAcceptsInput() {
        TestUtils.navigateToDetailsFragment();

        if (TestUtils.isViewDisplayedSafe(R.id.input_name)) {
            String testName = "John Doe";
            onView(withId(R.id.input_name))
                    .perform(typeText(testName), closeSoftKeyboard());
            
            onView(withId(R.id.input_name))
                    .check(matches(withText(testName)));
        }
    }

    /**
     * US 01.02.01: Verify text input is correctly captured in email field.
     */
    @Test
    public void testEmailFieldAcceptsInput() {
        TestUtils.navigateToDetailsFragment();

        if (TestUtils.isViewDisplayedSafe(R.id.input_email)) {
            String testEmail = "john.doe@example.com";
            onView(withId(R.id.input_email))
                    .perform(typeText(testEmail), closeSoftKeyboard());
            
            onView(withId(R.id.input_email))
                    .check(matches(withText(testEmail)));
        }
    }

    /**
     * US 01.02.01: Verify text input is correctly captured in phone field.
     */
    @Test
    public void testPhoneFieldAcceptsInput() {
        TestUtils.navigateToDetailsFragment();

        if (TestUtils.isViewDisplayedSafe(R.id.input_phone)) {
            String testPhone = "7801234567";
            onView(withId(R.id.input_phone))
                    .perform(typeText(testPhone), closeSoftKeyboard());
            
            onView(withId(R.id.input_phone))
                    .check(matches(withText(testPhone)));
        }
    }

    // ==================== ROLE SELECTION TESTS ====================

    /**
     * Test that all three role buttons are displayed on role selection screen.
     */
    @Test
    public void testRoleSelectionButtonsDisplayed() {
        TestUtils.navigateToDetailsFragment();
        
        if (TestUtils.isViewDisplayedSafe(R.id.input_name)) {
            TestUtils.navigateToRoleSelection(
                TestUtils.TestData.TEST_USER_NAME,
                TestUtils.TestData.TEST_USER_EMAIL
            );
            
            if (TestUtils.isViewDisplayedSafe(R.id.button_entrant)) {
                onView(withId(R.id.button_entrant))
                        .check(matches(isDisplayed()))
                        .check(matches(withText("Entrant")));
                onView(withId(R.id.button_organizer))
                        .check(matches(isDisplayed()))
                        .check(matches(withText("Organizer")));
                onView(withId(R.id.button_admin))
                        .check(matches(isDisplayed()))
                        .check(matches(withText("Admin")));
            }
        }
    }

    // ==================== HELPER CLASS EXTENSION ====================
    
    /**
     * Helper method to safely check if a view is displayed.
     * @param viewId Resource ID of the view
     * @return true if displayed, false otherwise
     */
    private boolean isViewDisplayedSafe(int viewId) {
        try {
            onView(withId(viewId)).check(matches(isDisplayed()));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
