package com.example.summit;

import com.example.summit.model.Admin;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for the Admin model class.
 * Tests cover constructor initialization, getters/setters, role behavior,
 * null handling, and inheritance from User class.
 */
public class AdminTest {
    private Admin admin;
    private static final String TEST_NAME = "Admin User";
    private static final String TEST_EMAIL = "admin@example.com";
    private static final String TEST_DEVICE_ID = "device123";
    private static final String TEST_PHONE = "555-1234";

    @Before
    public void setUp() {
        admin = new Admin(TEST_NAME, TEST_EMAIL, TEST_DEVICE_ID, TEST_PHONE);
    }

    // ========== Constructor Tests ==========

    @Test
    public void testConstructor_withValidData_createsAdmin() {
        Admin newAdmin = new Admin(TEST_NAME, TEST_EMAIL, TEST_DEVICE_ID, TEST_PHONE);

        assertNotNull("Admin should not be null", newAdmin);
        assertEquals("Name should match constructor parameter", TEST_NAME, newAdmin.getName());
        assertEquals("Email should match constructor parameter", TEST_EMAIL, newAdmin.getEmail());
        assertEquals("DeviceId should match constructor parameter", TEST_DEVICE_ID, newAdmin.getDeviceId());
        assertEquals("Phone should match constructor parameter", TEST_PHONE, newAdmin.getPhone());
    }

    @Test
    public void testEmptyConstructor_createsAdmin() {
        Admin emptyAdmin = new Admin();

        assertNotNull("Admin should not be null after empty constructor", emptyAdmin);
        assertNull("Name should be null after empty constructor", emptyAdmin.getName());
        assertNull("Email should be null after empty constructor", emptyAdmin.getEmail());
        assertNull("DeviceId should be null after empty constructor", emptyAdmin.getDeviceId());
        assertNull("Phone should be null after empty constructor", emptyAdmin.getPhone());
    }

    @Test
    public void testConstructor_withNullValues_createsAdmin() {
        Admin nullAdmin = new Admin(null, null, null, null);

        assertNotNull("Admin should not be null even with null parameters", nullAdmin);
        assertNull("Name should be null when null passed", nullAdmin.getName());
        assertNull("Email should be null when null passed", nullAdmin.getEmail());
        assertNull("DeviceId should be null when null passed", nullAdmin.getDeviceId());
        assertNull("Phone should be null when null passed", nullAdmin.getPhone());
        assertEquals("Role should still return Admin", "Admin", nullAdmin.getRole());
    }

    @Test
    public void testConstructor_withEmptyStrings_createsAdmin() {
        Admin emptyStringAdmin = new Admin("", "", "", "");

        assertNotNull("Admin should not be null with empty strings", emptyStringAdmin);
        assertEquals("Name should be empty string", "", emptyStringAdmin.getName());
        assertEquals("Email should be empty string", "", emptyStringAdmin.getEmail());
        assertEquals("DeviceId should be empty string", "", emptyStringAdmin.getDeviceId());
        assertEquals("Phone should be empty string", "", emptyStringAdmin.getPhone());
    }

    // ========== Role Tests ==========

    @Test
    public void testGetRole_always_returnsAdmin() {
        assertEquals("Role should be Admin", "Admin", admin.getRole());
    }

    @Test
    public void testGetRole_afterModifications_stillReturnsAdmin() {
        // Modify admin properties
        admin.setName("Different Name");
        admin.setEmail("different@example.com");
        admin.setPhone("999-9999");

        assertEquals("Role should still be Admin after modifications", "Admin", admin.getRole());
    }

    @Test
    public void testGetRole_withEmptyConstructor_returnsAdmin() {
        Admin emptyAdmin = new Admin();
        assertEquals("Role should be Admin even with empty constructor", "Admin", emptyAdmin.getRole());
    }

    // ========== Name Getter/Setter Tests ==========

    @Test
    public void testGetName_returnsCorrectValue() {
        assertEquals("getName should return constructor value", TEST_NAME, admin.getName());
    }

    @Test
    public void testSetName_updatesValue() {
        String newName = "New Admin Name";
        admin.setName(newName);
        assertEquals("getName should return updated value", newName, admin.getName());
    }

    @Test
    public void testSetName_toNull_acceptsNull() {
        admin.setName(null);
        assertNull("Name should be null after setting to null", admin.getName());
    }

    @Test
    public void testSetName_toEmptyString_acceptsEmptyString() {
        admin.setName("");
        assertEquals("Name should be empty string", "", admin.getName());
    }

    @Test
    public void testSetName_withSpecialCharacters_acceptsValue() {
        String specialName = "Admin O'Brien-Smith Jr. (Sr.)";
        admin.setName(specialName);
        assertEquals("Name should accept special characters", specialName, admin.getName());
    }

    // ========== Email Getter/Setter Tests ==========

    @Test
    public void testGetEmail_returnsCorrectValue() {
        assertEquals("getEmail should return constructor value", TEST_EMAIL, admin.getEmail());
    }

    @Test
    public void testSetEmail_updatesValue() {
        String newEmail = "newemail@example.com";
        admin.setEmail(newEmail);
        assertEquals("getEmail should return updated value", newEmail, admin.getEmail());
    }

    @Test
    public void testSetEmail_toNull_acceptsNull() {
        admin.setEmail(null);
        assertNull("Email should be null after setting to null", admin.getEmail());
    }

    @Test
    public void testSetEmail_toEmptyString_acceptsEmptyString() {
        admin.setEmail("");
        assertEquals("Email should be empty string", "", admin.getEmail());
    }

    @Test
    public void testSetEmail_withInvalidFormat_acceptsValue() {
        // Note: No validation exists, so invalid formats are accepted
        String invalidEmail = "not-an-email";
        admin.setEmail(invalidEmail);
        assertEquals("Email accepts invalid format (no validation)", invalidEmail, admin.getEmail());
    }

    // ========== DeviceId Getter Tests ==========

    @Test
    public void testGetDeviceId_returnsCorrectValue() {
        assertEquals("getDeviceId should return constructor value", TEST_DEVICE_ID, admin.getDeviceId());
    }

    @Test
    public void testDeviceId_isImmutable_noSetterExists() {
        // This test documents that deviceId cannot be changed after construction
        // Attempting to use reflection to verify no setter exists
        try {
            Admin.class.getMethod("setDeviceId", String.class);
            fail("setDeviceId method should not exist - deviceId should be immutable");
        } catch (NoSuchMethodException e) {
            // Expected - no setter should exist
            assertTrue("DeviceId is immutable (no setter exists)", true);
        }
    }

    // ========== Phone Getter/Setter Tests ==========

    @Test
    public void testGetPhone_returnsCorrectValue() {
        assertEquals("getPhone should return constructor value", TEST_PHONE, admin.getPhone());
    }

    @Test
    public void testSetPhone_updatesValue() {
        String newPhone = "555-5678";
        admin.setPhone(newPhone);
        assertEquals("getPhone should return updated value", newPhone, admin.getPhone());
    }

    @Test
    public void testSetPhone_toNull_acceptsNull() {
        admin.setPhone(null);
        assertNull("Phone should be null after setting to null", admin.getPhone());
    }

    @Test
    public void testSetPhone_toEmptyString_acceptsEmptyString() {
        admin.setPhone("");
        assertEquals("Phone should be empty string", "", admin.getPhone());
    }

    @Test
    public void testSetPhone_withInvalidFormat_acceptsValue() {
        // Note: No validation exists, so any string is accepted
        String invalidPhone = "abc-defg";
        admin.setPhone(invalidPhone);
        assertEquals("Phone accepts invalid format (no validation)", invalidPhone, admin.getPhone());
    }

    // ========== Inheritance and Type Tests ==========

    @Test
    public void testAdmin_isInstanceOfUser() {
        assertTrue("Admin should be instance of User", admin instanceof com.example.summit.model.User);
    }

    @Test
    public void testAdmin_isSerializable() {
        assertTrue("Admin should be Serializable (through User)", admin instanceof java.io.Serializable);
    }

    // ========== Edge Cases and Boundary Tests ==========

    @Test
    public void testConstructor_withVeryLongStrings_acceptsValues() {
        String longString = "a".repeat(10000);
        Admin longAdmin = new Admin(longString, longString, longString, longString);

        assertNotNull("Admin should handle very long strings", longAdmin);
        assertEquals("Long name should be stored", longString, longAdmin.getName());
        assertEquals("Long email should be stored", longString, longAdmin.getEmail());
        assertEquals("Long deviceId should be stored", longString, longAdmin.getDeviceId());
        assertEquals("Long phone should be stored", longString, longAdmin.getPhone());
    }

    @Test
    public void testMultipleAdmins_areIndependent() {
        Admin admin1 = new Admin("Admin1", "admin1@test.com", "device1", "111-1111");
        Admin admin2 = new Admin("Admin2", "admin2@test.com", "device2", "222-2222");

        // Modify admin1
        admin1.setName("Modified Admin");

        // Verify admin2 is unchanged
        assertEquals("Admin2 name should be unchanged", "Admin2", admin2.getName());
        assertNotEquals("Admins should be independent", admin1.getName(), admin2.getName());
    }

    @Test
    public void testSetters_canBeChained_throughReassignment() {
        // Document that setters return void (no method chaining)
        admin.setName("New Name");
        admin.setEmail("new@email.com");
        admin.setPhone("999-9999");

        assertEquals("Name should be updated", "New Name", admin.getName());
        assertEquals("Email should be updated", "new@email.com", admin.getEmail());
        assertEquals("Phone should be updated", "999-9999", admin.getPhone());
    }
}
