package com.example.summit;

import com.example.summit.model.Organizer;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for the Organizer model class.
 * Tests cover constructor initialization, getters/setters, role behavior,
 * null handling, and inheritance from User class.
 */
public class OrganizerTest {
    private Organizer organizer;
    private static final String TEST_NAME = "Event Organizer";
    private static final String TEST_EMAIL = "organizer@example.com";
    private static final String TEST_DEVICE_ID = "device456";
    private static final String TEST_PHONE = "555-9876";

    @Before
    public void setUp() {
        organizer = new Organizer(TEST_NAME, TEST_EMAIL, TEST_DEVICE_ID, TEST_PHONE);
    }

    // ========== Constructor Tests ==========

    @Test
    public void testConstructor_withValidData_createsOrganizer() {
        Organizer newOrganizer = new Organizer(TEST_NAME, TEST_EMAIL, TEST_DEVICE_ID, TEST_PHONE);

        assertNotNull("Organizer should not be null", newOrganizer);
        assertEquals("Name should match constructor parameter", TEST_NAME, newOrganizer.getName());
        assertEquals("Email should match constructor parameter", TEST_EMAIL, newOrganizer.getEmail());
        assertEquals("DeviceId should match constructor parameter", TEST_DEVICE_ID, newOrganizer.getDeviceId());
        assertEquals("Phone should match constructor parameter", TEST_PHONE, newOrganizer.getPhone());
    }

    @Test
    public void testEmptyConstructor_createsOrganizer() {
        Organizer emptyOrganizer = new Organizer();

        assertNotNull("Organizer should not be null after empty constructor", emptyOrganizer);
        assertNull("Name should be null after empty constructor", emptyOrganizer.getName());
        assertNull("Email should be null after empty constructor", emptyOrganizer.getEmail());
        assertNull("DeviceId should be null after empty constructor", emptyOrganizer.getDeviceId());
        assertNull("Phone should be null after empty constructor", emptyOrganizer.getPhone());
    }

    @Test
    public void testConstructor_withNullValues_createsOrganizer() {
        Organizer nullOrganizer = new Organizer(null, null, null, null);

        assertNotNull("Organizer should not be null even with null parameters", nullOrganizer);
        assertNull("Name should be null when null passed", nullOrganizer.getName());
        assertNull("Email should be null when null passed", nullOrganizer.getEmail());
        assertNull("DeviceId should be null when null passed", nullOrganizer.getDeviceId());
        assertNull("Phone should be null when null passed", nullOrganizer.getPhone());
        assertEquals("Role should still return Organizer", "Organizer", nullOrganizer.getRole());
    }

    @Test
    public void testConstructor_withEmptyStrings_createsOrganizer() {
        Organizer emptyStringOrganizer = new Organizer("", "", "", "");

        assertNotNull("Organizer should not be null with empty strings", emptyStringOrganizer);
        assertEquals("Name should be empty string", "", emptyStringOrganizer.getName());
        assertEquals("Email should be empty string", "", emptyStringOrganizer.getEmail());
        assertEquals("DeviceId should be empty string", "", emptyStringOrganizer.getDeviceId());
        assertEquals("Phone should be empty string", "", emptyStringOrganizer.getPhone());
    }

    // ========== Role Tests ==========

    @Test
    public void testGetRole_always_returnsOrganizer() {
        assertEquals("Role should be Organizer", "Organizer", organizer.getRole());
    }

    @Test
    public void testGetRole_afterModifications_stillReturnsOrganizer() {
        // Modify organizer properties
        organizer.setName("Different Name");
        organizer.setEmail("different@example.com");
        organizer.setPhone("999-9999");

        assertEquals("Role should still be Organizer after modifications", "Organizer", organizer.getRole());
    }

    @Test
    public void testGetRole_withEmptyConstructor_returnsOrganizer() {
        Organizer emptyOrganizer = new Organizer();
        assertEquals("Role should be Organizer even with empty constructor", "Organizer", emptyOrganizer.getRole());
    }

    @Test
    public void testGetRole_isDifferentFromAdmin() {
        // Document that Organizer role is distinct from Admin role
        assertEquals("Organizer role should be 'Organizer'", "Organizer", organizer.getRole());
        assertNotEquals("Organizer role should not be 'Admin'", "Admin", organizer.getRole());
    }

    // ========== Name Getter/Setter Tests ==========

    @Test
    public void testGetName_returnsCorrectValue() {
        assertEquals("getName should return constructor value", TEST_NAME, organizer.getName());
    }

    @Test
    public void testSetName_updatesValue() {
        String newName = "Updated Organizer Name";
        organizer.setName(newName);
        assertEquals("getName should return updated value", newName, organizer.getName());
    }

    @Test
    public void testSetName_toNull_acceptsNull() {
        organizer.setName(null);
        assertNull("Name should be null after setting to null", organizer.getName());
    }

    @Test
    public void testSetName_toEmptyString_acceptsEmptyString() {
        organizer.setName("");
        assertEquals("Name should be empty string", "", organizer.getName());
    }

    @Test
    public void testSetName_withSpecialCharacters_acceptsValue() {
        String specialName = "Organizer O'Brien-Smith Jr. & Co.";
        organizer.setName(specialName);
        assertEquals("Name should accept special characters", specialName, organizer.getName());
    }

    // ========== Email Getter/Setter Tests ==========

    @Test
    public void testGetEmail_returnsCorrectValue() {
        assertEquals("getEmail should return constructor value", TEST_EMAIL, organizer.getEmail());
    }

    @Test
    public void testSetEmail_updatesValue() {
        String newEmail = "newemail@organizer.com";
        organizer.setEmail(newEmail);
        assertEquals("getEmail should return updated value", newEmail, organizer.getEmail());
    }

    @Test
    public void testSetEmail_toNull_acceptsNull() {
        organizer.setEmail(null);
        assertNull("Email should be null after setting to null", organizer.getEmail());
    }

    @Test
    public void testSetEmail_toEmptyString_acceptsEmptyString() {
        organizer.setEmail("");
        assertEquals("Email should be empty string", "", organizer.getEmail());
    }

    @Test
    public void testSetEmail_withInvalidFormat_acceptsValue() {
        // Note: No validation exists, so invalid formats are accepted
        String invalidEmail = "not-a-valid-email";
        organizer.setEmail(invalidEmail);
        assertEquals("Email accepts invalid format (no validation)", invalidEmail, organizer.getEmail());
    }

    // ========== DeviceId Getter Tests ==========

    @Test
    public void testGetDeviceId_returnsCorrectValue() {
        assertEquals("getDeviceId should return constructor value", TEST_DEVICE_ID, organizer.getDeviceId());
    }

    @Test
    public void testDeviceId_isImmutable_noSetterExists() {
        // This test documents that deviceId cannot be changed after construction
        // Attempting to use reflection to verify no setter exists
        try {
            Organizer.class.getMethod("setDeviceId", String.class);
            fail("setDeviceId method should not exist - deviceId should be immutable");
        } catch (NoSuchMethodException e) {
            // Expected - no setter should exist
            assertTrue("DeviceId is immutable (no setter exists)", true);
        }
    }

    // ========== Phone Getter/Setter Tests ==========

    @Test
    public void testGetPhone_returnsCorrectValue() {
        assertEquals("getPhone should return constructor value", TEST_PHONE, organizer.getPhone());
    }

    @Test
    public void testSetPhone_updatesValue() {
        String newPhone = "555-1111";
        organizer.setPhone(newPhone);
        assertEquals("getPhone should return updated value", newPhone, organizer.getPhone());
    }

    @Test
    public void testSetPhone_toNull_acceptsNull() {
        organizer.setPhone(null);
        assertNull("Phone should be null after setting to null", organizer.getPhone());
    }

    @Test
    public void testSetPhone_toEmptyString_acceptsEmptyString() {
        organizer.setPhone("");
        assertEquals("Phone should be empty string", "", organizer.getPhone());
    }

    @Test
    public void testSetPhone_withInvalidFormat_acceptsValue() {
        // Note: No validation exists, so any string is accepted
        String invalidPhone = "invalid-phone-123";
        organizer.setPhone(invalidPhone);
        assertEquals("Phone accepts invalid format (no validation)", invalidPhone, organizer.getPhone());
    }

    // ========== Inheritance and Type Tests ==========

    @Test
    public void testOrganizer_isInstanceOfUser() {
        assertTrue("Organizer should be instance of User", organizer instanceof com.example.summit.model.User);
    }

    @Test
    public void testOrganizer_isSerializable() {
        assertTrue("Organizer should be Serializable (through User)", organizer instanceof java.io.Serializable);
    }

    // ========== Edge Cases and Boundary Tests ==========

    @Test
    public void testConstructor_withVeryLongStrings_acceptsValues() {
        String longString = "a".repeat(10000);
        Organizer longOrganizer = new Organizer(longString, longString, longString, longString);

        assertNotNull("Organizer should handle very long strings", longOrganizer);
        assertEquals("Long name should be stored", longString, longOrganizer.getName());
        assertEquals("Long email should be stored", longString, longOrganizer.getEmail());
        assertEquals("Long deviceId should be stored", longString, longOrganizer.getDeviceId());
        assertEquals("Long phone should be stored", longString, longOrganizer.getPhone());
    }

    @Test
    public void testMultipleOrganizers_areIndependent() {
        Organizer organizer1 = new Organizer("Organizer1", "org1@test.com", "device1", "111-1111");
        Organizer organizer2 = new Organizer("Organizer2", "org2@test.com", "device2", "222-2222");

        // Modify organizer1
        organizer1.setName("Modified Organizer");

        // Verify organizer2 is unchanged
        assertEquals("Organizer2 name should be unchanged", "Organizer2", organizer2.getName());
        assertNotEquals("Organizers should be independent", organizer1.getName(), organizer2.getName());
    }

    @Test
    public void testSetters_canBeChained_throughReassignment() {
        // Document that setters return void (no method chaining)
        organizer.setName("New Name");
        organizer.setEmail("new@email.com");
        organizer.setPhone("999-9999");

        assertEquals("Name should be updated", "New Name", organizer.getName());
        assertEquals("Email should be updated", "new@email.com", organizer.getEmail());
        assertEquals("Phone should be updated", "999-9999", organizer.getPhone());
    }

    @Test
    public void testOrganizer_canHaveSameDeviceId_asAnotherUser() {
        // Document that no uniqueness validation exists
        Organizer organizer1 = new Organizer("Org1", "org1@test.com", "sameDevice", "111-1111");
        Organizer organizer2 = new Organizer("Org2", "org2@test.com", "sameDevice", "222-2222");

        assertEquals("Both organizers can have same deviceId",
                     organizer1.getDeviceId(), organizer2.getDeviceId());
    }

    @Test
    public void testEmptyConstructor_isCompatibleWithFirebase() {
        // Test that empty constructor creates valid object for Firebase deserialization
        Organizer firebaseOrganizer = new Organizer();

        assertNotNull("Organizer should not be null", firebaseOrganizer);
        assertEquals("Role should be set correctly", "Organizer", firebaseOrganizer.getRole());

        // After Firebase populates fields via setters
        firebaseOrganizer.setName("Firebase User");
        firebaseOrganizer.setEmail("firebase@test.com");
        firebaseOrganizer.setPhone("555-0000");

        assertEquals("Name should be set", "Firebase User", firebaseOrganizer.getName());
        assertEquals("Email should be set", "firebase@test.com", firebaseOrganizer.getEmail());
        assertEquals("Phone should be set", "555-0000", firebaseOrganizer.getPhone());
    }
}
