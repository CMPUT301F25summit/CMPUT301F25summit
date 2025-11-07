package com.example.summit;

import org.junit.Test;
import static org.junit.Assert.*;

import com.example.summit.model.Entrant;

/**
 * Unit tests for the Entrant class
 * These tests also cover the concrete methods inherited from the abstract User class.
 */
public class EntrantTest {

    @Test
    public void testFullConstructorAndDefaults() {
        Entrant entrant = new Entrant("Test User", "test@example.com", "device-123", "555-1234");

        assertEquals("Test User", entrant.getName());
        assertEquals("test@example.com", entrant.getEmail());
        assertEquals("device-123", entrant.getDeviceId());
        assertEquals("555-1234", entrant.getPhone());

        assertTrue("Notifications should be enabled by default on full constructor", entrant.isNotificationsEnabled());
        assertNull("Invitation status should be null by default", entrant.getInvitationStatus());
    }

    @Test
    public void testLotteryConstructorDefaults() {
        Entrant entrant = new Entrant("lotto-device-id");

        assertEquals("lotto-device-id", entrant.getDeviceId());
        assertEquals("Name should default to 'Unknown'", "Unknown", entrant.getName());
        assertEquals("Email should default to empty string", "", entrant.getEmail());
        assertEquals("Phone should default to empty string", "", entrant.getPhone());
    }

    @Test
    public void testDefaultConstructor() {
        Entrant entrant = new Entrant();

        assertNull(entrant.getName());
        assertNull(entrant.getEmail());
        assertNull(entrant.getDeviceId());
        assertNull(entrant.getPhone());

        assertFalse("Primitive boolean 'notificationsEnabled' should default to false", entrant.isNotificationsEnabled());
        assertNull("Boolean object 'invitationAccepted' should default to null", entrant.getInvitationStatus());
    }

    @Test
    public void testGetRole() {
        Entrant entrant = new Entrant();

        assertEquals("Entrant", entrant.getRole());
    }

    @Test
    public void testOptOutNotifications() {
        Entrant entrant = new Entrant("Test User", "test@example.com", "dev-1", "555");
        assertTrue("Pre-condition: Notifications should be on", entrant.isNotificationsEnabled());

        entrant.optOutNotifications();

        assertFalse(entrant.isNotificationsEnabled());
    }

    @Test
    public void testOptInNotifications() {
        Entrant entrant = new Entrant();
        assertFalse("Pre-condition: Notifications should be off", entrant.isNotificationsEnabled());

        entrant.optInNotifications();

        assertTrue(entrant.isNotificationsEnabled());
    }

    @Test
    public void testAcceptInvitation() {
        // Arrange
        Entrant entrant = new Entrant();
        assertNull("Pre-condition: Status should be null", entrant.getInvitationStatus());

        entrant.acceptInvitation();

        assertTrue(entrant.getInvitationStatus());
    }

    @Test
    public void testDeclineInvitation() {
        Entrant entrant = new Entrant();
        assertNull("Pre-condition: Status should be null", entrant.getInvitationStatus());

        entrant.declineInvitation();

        assertFalse(entrant.getInvitationStatus());
    }

    @Test
    public void testInheritedSetters() {
        Entrant entrant = new Entrant(); // Start with a blank Entrant

        entrant.setName("New Name");
        entrant.setEmail("new@example.com");
        entrant.setPhone("555-9876");

        assertEquals("New Name", entrant.getName());
        assertEquals("new@example.com", entrant.getEmail());
        assertEquals("555-9876", entrant.getPhone());
    }
}