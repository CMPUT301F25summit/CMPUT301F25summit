package com.example.summit;

import com.example.summit.model.QrCode;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for the QrCode model class.
 * Tests cover constructor initialization, getters/setters, timestamp auto-generation,
 * getEncodedData() business logic, and edge cases for QR code data handling.
 */
public class QrCodeTest {
    private QrCode qrCode;
    private static final String TEST_EVENT_ID = "event123";
    private static final String TEST_QR_URL = "https://example.com/event/event123";
    private static final String TEST_QR_DATA = "iVBORw0KGgoAAAANSUhEUgAAAAUA"; // Mock Base64 data

    @Before
    public void setUp() {
        qrCode = new QrCode(TEST_EVENT_ID);
    }

    // ========== Constructor Tests ==========

    @Test
    public void testEmptyConstructor_createsQrCode() {
        QrCode emptyQrCode = new QrCode();

        assertNotNull("QrCode should not be null", emptyQrCode);
        assertNull("EventId should be null", emptyQrCode.getEventId());
        assertNull("QrCodeData should be null", emptyQrCode.getQrCodeData());
        assertNull("QrCodeUrl should be null", emptyQrCode.getQrCodeUrl());
        assertEquals("GeneratedTimestamp should be 0", 0, emptyQrCode.getGeneratedTimestamp());
    }

    @Test
    public void testConstructor_withEventId_createsQrCode() {
        QrCode newQrCode = new QrCode(TEST_EVENT_ID);

        assertNotNull("QrCode should not be null", newQrCode);
        assertEquals("EventId should match", TEST_EVENT_ID, newQrCode.getEventId());
        assertNull("QrCodeData should be null initially", newQrCode.getQrCodeData());
        assertNull("QrCodeUrl should be null initially", newQrCode.getQrCodeUrl());
        assertTrue("GeneratedTimestamp should be set", newQrCode.getGeneratedTimestamp() > 0);
    }

    @Test
    public void testConstructor_withEventIdAndUrl_createsQrCode() {
        QrCode newQrCode = new QrCode(TEST_EVENT_ID, TEST_QR_URL);

        assertNotNull("QrCode should not be null", newQrCode);
        assertEquals("EventId should match", TEST_EVENT_ID, newQrCode.getEventId());
        assertEquals("QrCodeUrl should match", TEST_QR_URL, newQrCode.getQrCodeUrl());
        assertNull("QrCodeData should be null initially", newQrCode.getQrCodeData());
        assertTrue("GeneratedTimestamp should be set", newQrCode.getGeneratedTimestamp() > 0);
    }

    @Test
    public void testConstructor_withNullEventId_acceptsNull() {
        QrCode nullQrCode = new QrCode(null);

        assertNotNull("QrCode should not be null", nullQrCode);
        assertNull("EventId should be null", nullQrCode.getEventId());
        assertTrue("GeneratedTimestamp should still be set", nullQrCode.getGeneratedTimestamp() > 0);
    }

    @Test
    public void testConstructor_withNullUrl_acceptsNull() {
        QrCode nullUrlQrCode = new QrCode(TEST_EVENT_ID, null);

        assertNotNull("QrCode should not be null", nullUrlQrCode);
        assertEquals("EventId should be set", TEST_EVENT_ID, nullUrlQrCode.getEventId());
        assertNull("QrCodeUrl should be null", nullUrlQrCode.getQrCodeUrl());
    }

    @Test
    public void testConstructor_withBothNull_acceptsNull() {
        QrCode allNullQrCode = new QrCode(null, null);

        assertNotNull("QrCode should not be null", allNullQrCode);
        assertNull("EventId should be null", allNullQrCode.getEventId());
        assertNull("QrCodeUrl should be null", allNullQrCode.getQrCodeUrl());
        assertTrue("GeneratedTimestamp should still be set", allNullQrCode.getGeneratedTimestamp() > 0);
    }

    // ========== Timestamp Generation Tests ==========

    @Test
    public void testConstructor_withEventId_setsTimestamp() {
        long beforeTime = System.currentTimeMillis();
        QrCode newQrCode = new QrCode(TEST_EVENT_ID);
        long afterTime = System.currentTimeMillis();

        long timestamp = newQrCode.getGeneratedTimestamp();
        assertTrue("Timestamp should be >= beforeTime", timestamp >= beforeTime);
        assertTrue("Timestamp should be <= afterTime", timestamp <= afterTime);
    }

    @Test
    public void testConstructor_withEventIdAndUrl_setsTimestamp() {
        long beforeTime = System.currentTimeMillis();
        QrCode newQrCode = new QrCode(TEST_EVENT_ID, TEST_QR_URL);
        long afterTime = System.currentTimeMillis();

        long timestamp = newQrCode.getGeneratedTimestamp();
        assertTrue("Timestamp should be >= beforeTime", timestamp >= beforeTime);
        assertTrue("Timestamp should be <= afterTime", timestamp <= afterTime);
    }

    @Test
    public void testEmptyConstructor_doesNotSetTimestamp() {
        QrCode emptyQrCode = new QrCode();
        assertEquals("Timestamp should be 0 for empty constructor", 0, emptyQrCode.getGeneratedTimestamp());
    }

    @Test
    public void testMultipleQrCodes_haveDifferentTimestamps() throws InterruptedException {
        QrCode qrCode1 = new QrCode(TEST_EVENT_ID);
        Thread.sleep(2); // Small delay to ensure different timestamps
        QrCode qrCode2 = new QrCode(TEST_EVENT_ID);

        assertTrue("QrCode2 timestamp should be >= QrCode1 timestamp",
                   qrCode2.getGeneratedTimestamp() >= qrCode1.getGeneratedTimestamp());
    }

    // ========== EventId Getter/Setter Tests ==========

    @Test
    public void testGetEventId_returnsCorrectValue() {
        assertEquals("EventId should match", TEST_EVENT_ID, qrCode.getEventId());
    }

    @Test
    public void testSetEventId_updatesValue() {
        String newEventId = "event456";
        qrCode.setEventId(newEventId);
        assertEquals("EventId should be updated", newEventId, qrCode.getEventId());
    }

    @Test
    public void testSetEventId_toNull_acceptsNull() {
        qrCode.setEventId(null);
        assertNull("EventId should be null", qrCode.getEventId());
    }

    @Test
    public void testSetEventId_toEmptyString_acceptsEmptyString() {
        qrCode.setEventId("");
        assertEquals("EventId should be empty string", "", qrCode.getEventId());
    }

    // ========== QrCodeData Getter/Setter Tests ==========

    @Test
    public void testGetQrCodeData_initiallyNull() {
        assertNull("QrCodeData should be null initially", qrCode.getQrCodeData());
    }

    @Test
    public void testSetQrCodeData_updatesValue() {
        qrCode.setQrCodeData(TEST_QR_DATA);
        assertEquals("QrCodeData should be updated", TEST_QR_DATA, qrCode.getQrCodeData());
    }

    @Test
    public void testSetQrCodeData_toNull_acceptsNull() {
        qrCode.setQrCodeData(TEST_QR_DATA);
        qrCode.setQrCodeData(null);
        assertNull("QrCodeData should be null", qrCode.getQrCodeData());
    }

    @Test
    public void testSetQrCodeData_toEmptyString_acceptsEmptyString() {
        qrCode.setQrCodeData("");
        assertEquals("QrCodeData should be empty string", "", qrCode.getQrCodeData());
    }

    @Test
    public void testSetQrCodeData_withBase64String_acceptsValue() {
        String base64Data = "iVBORw0KGgoAAAANSUhEUgAAAAUAAAAFCAYAAACNbyblAAAAHElEQVQI12P4//8/w38GIAXDIBKE0DHxgljNBAAO9TXL0Y4OHwAAAABJRU5ErkJggg==";
        qrCode.setQrCodeData(base64Data);
        assertEquals("QrCodeData should store Base64 string", base64Data, qrCode.getQrCodeData());
    }

    @Test
    public void testSetQrCodeData_withInvalidBase64_acceptsValue() {
        // Note: No validation exists, so invalid Base64 is accepted
        String invalidBase64 = "not-valid-base64!@#$";
        qrCode.setQrCodeData(invalidBase64);
        assertEquals("QrCodeData accepts invalid Base64 (no validation)", invalidBase64, qrCode.getQrCodeData());
    }

    // ========== QrCodeUrl Getter/Setter Tests ==========

    @Test
    public void testGetQrCodeUrl_initiallyNull_forEventIdConstructor() {
        assertNull("QrCodeUrl should be null initially", qrCode.getQrCodeUrl());
    }

    @Test
    public void testSetQrCodeUrl_updatesValue() {
        qrCode.setQrCodeUrl(TEST_QR_URL);
        assertEquals("QrCodeUrl should be updated", TEST_QR_URL, qrCode.getQrCodeUrl());
    }

    @Test
    public void testSetQrCodeUrl_toNull_acceptsNull() {
        qrCode.setQrCodeUrl(TEST_QR_URL);
        qrCode.setQrCodeUrl(null);
        assertNull("QrCodeUrl should be null", qrCode.getQrCodeUrl());
    }

    @Test
    public void testSetQrCodeUrl_toEmptyString_acceptsEmptyString() {
        qrCode.setQrCodeUrl("");
        assertEquals("QrCodeUrl should be empty string", "", qrCode.getQrCodeUrl());
    }

    @Test
    public void testSetQrCodeUrl_withInvalidUrl_acceptsValue() {
        // Note: No URL validation exists
        String invalidUrl = "not-a-valid-url";
        qrCode.setQrCodeUrl(invalidUrl);
        assertEquals("QrCodeUrl accepts invalid format (no validation)", invalidUrl, qrCode.getQrCodeUrl());
    }

    @Test
    public void testSetQrCodeUrl_withHttpsUrl_acceptsValue() {
        String httpsUrl = "https://secure-example.com/qr/event123";
        qrCode.setQrCodeUrl(httpsUrl);
        assertEquals("QrCodeUrl should accept HTTPS URL", httpsUrl, qrCode.getQrCodeUrl());
    }

    // ========== GeneratedTimestamp Getter/Setter Tests ==========

    @Test
    public void testGetGeneratedTimestamp_returnsAutoGeneratedValue() {
        assertTrue("GeneratedTimestamp should be > 0", qrCode.getGeneratedTimestamp() > 0);
    }

    @Test
    public void testSetGeneratedTimestamp_updatesValue() {
        long customTimestamp = 1234567890L;
        qrCode.setGeneratedTimestamp(customTimestamp);
        assertEquals("GeneratedTimestamp should be updated", customTimestamp, qrCode.getGeneratedTimestamp());
    }

    @Test
    public void testSetGeneratedTimestamp_toZero_acceptsZero() {
        qrCode.setGeneratedTimestamp(0);
        assertEquals("GeneratedTimestamp should be 0", 0, qrCode.getGeneratedTimestamp());
    }

    @Test
    public void testSetGeneratedTimestamp_toNegative_acceptsNegative() {
        // Note: No validation prevents negative timestamps
        qrCode.setGeneratedTimestamp(-1000L);
        assertEquals("GeneratedTimestamp accepts negative values", -1000L, qrCode.getGeneratedTimestamp());
    }

    @Test
    public void testSetGeneratedTimestamp_toMaxValue_acceptsMaxValue() {
        qrCode.setGeneratedTimestamp(Long.MAX_VALUE);
        assertEquals("GeneratedTimestamp accepts Long.MAX_VALUE", Long.MAX_VALUE, qrCode.getGeneratedTimestamp());
    }

    // ========== getEncodedData() Business Logic Tests ==========

    @Test
    public void testGetEncodedData_returnsEventId_whenUrlIsNull() {
        QrCode qrCodeNoUrl = new QrCode(TEST_EVENT_ID);

        assertEquals("EncodedData should return eventId when URL is null",
                     TEST_EVENT_ID, qrCodeNoUrl.getEncodedData());
    }

    @Test
    public void testGetEncodedData_returnsUrl_whenUrlIsSet() {
        QrCode qrCodeWithUrl = new QrCode(TEST_EVENT_ID, TEST_QR_URL);

        assertEquals("EncodedData should return URL when URL is set",
                     TEST_QR_URL, qrCodeWithUrl.getEncodedData());
    }

    @Test
    public void testGetEncodedData_returnsEventId_whenUrlIsEmpty() {
        QrCode qrCodeEmptyUrl = new QrCode(TEST_EVENT_ID, "");

        assertEquals("EncodedData should return eventId when URL is empty string",
                     TEST_EVENT_ID, qrCodeEmptyUrl.getEncodedData());
    }

    @Test
    public void testGetEncodedData_prioritizesUrl_overEventId() {
        QrCode qrCodeBoth = new QrCode(TEST_EVENT_ID, TEST_QR_URL);

        String encodedData = qrCodeBoth.getEncodedData();
        assertEquals("EncodedData should return URL, not eventId", TEST_QR_URL, encodedData);
        assertNotEquals("EncodedData should not return eventId when URL is set", TEST_EVENT_ID, encodedData);
    }

    @Test
    public void testGetEncodedData_afterSettingUrl_returnsUrl() {
        // Initially no URL
        assertEquals("EncodedData should be eventId initially", TEST_EVENT_ID, qrCode.getEncodedData());

        // Set URL
        qrCode.setQrCodeUrl(TEST_QR_URL);
        assertEquals("EncodedData should be URL after setting", TEST_QR_URL, qrCode.getEncodedData());
    }

    @Test
    public void testGetEncodedData_afterClearingUrl_returnsEventId() {
        qrCode.setQrCodeUrl(TEST_QR_URL);
        assertEquals("EncodedData should be URL", TEST_QR_URL, qrCode.getEncodedData());

        // Clear URL
        qrCode.setQrCodeUrl(null);
        assertEquals("EncodedData should return to eventId after clearing URL",
                     TEST_EVENT_ID, qrCode.getEncodedData());
    }

    @Test
    public void testGetEncodedData_afterSettingEmptyUrl_returnsEventId() {
        qrCode.setQrCodeUrl(TEST_QR_URL);
        assertEquals("EncodedData should be URL", TEST_QR_URL, qrCode.getEncodedData());

        // Set to empty string
        qrCode.setQrCodeUrl("");
        assertEquals("EncodedData should return eventId when URL is empty",
                     TEST_EVENT_ID, qrCode.getEncodedData());
    }

    @Test
    public void testGetEncodedData_withNullEventId_returnsNull() {
        QrCode qrCodeNullEvent = new QrCode(null);

        assertNull("EncodedData should be null when eventId is null and URL is null",
                   qrCodeNullEvent.getEncodedData());
    }

    @Test
    public void testGetEncodedData_withNullEventId_butValidUrl_returnsUrl() {
        QrCode qrCodeNullEvent = new QrCode(null, TEST_QR_URL);

        assertEquals("EncodedData should return URL even when eventId is null",
                     TEST_QR_URL, qrCodeNullEvent.getEncodedData());
    }

    @Test
    public void testGetEncodedData_withBothNull_returnsNull() {
        QrCode qrCodeBothNull = new QrCode(null, null);

        assertNull("EncodedData should be null when both eventId and URL are null",
                   qrCodeBothNull.getEncodedData());
    }

    @Test
    public void testGetEncodedData_withWhitespaceUrl_returnsUrl() {
        // Whitespace is not considered "empty" by isEmpty()
        String whitespaceUrl = "   ";
        qrCode.setQrCodeUrl(whitespaceUrl);

        assertEquals("EncodedData should return whitespace URL (not considered empty)",
                     whitespaceUrl, qrCode.getEncodedData());
    }

    // ========== Edge Cases and Integration Tests ==========

    @Test
    public void testMultipleQrCodes_areIndependent() {
        QrCode qrCode1 = new QrCode("event1");
        QrCode qrCode2 = new QrCode("event2");

        qrCode1.setQrCodeUrl("url1");
        qrCode2.setQrCodeUrl("url2");

        assertEquals("QrCode1 should have url1", "url1", qrCode1.getEncodedData());
        assertEquals("QrCode2 should have url2", "url2", qrCode2.getEncodedData());
        assertNotEquals("QrCodes should be independent", qrCode1.getEncodedData(), qrCode2.getEncodedData());
    }

    @Test
    public void testVeryLongEventId_isAccepted() {
        String longEventId = "event_" + "a".repeat(10000);
        QrCode longQrCode = new QrCode(longEventId);

        assertEquals("Long eventId should be stored", longEventId, longQrCode.getEventId());
        assertEquals("EncodedData should return long eventId", longEventId, longQrCode.getEncodedData());
    }

    @Test
    public void testVeryLongUrl_isAccepted() {
        String longUrl = "https://example.com/" + "a".repeat(10000);
        QrCode longQrCode = new QrCode(TEST_EVENT_ID, longUrl);

        assertEquals("Long URL should be stored", longUrl, longQrCode.getQrCodeUrl());
        assertEquals("EncodedData should return long URL", longUrl, longQrCode.getEncodedData());
    }

    @Test
    public void testSpecialCharacters_inEventId() {
        String specialEventId = "event-123_abc!@#$%^&*()";
        QrCode specialQrCode = new QrCode(specialEventId);

        assertEquals("EventId with special characters should be stored", specialEventId, specialQrCode.getEventId());
        assertEquals("EncodedData should return eventId with special characters",
                     specialEventId, specialQrCode.getEncodedData());
    }

    @Test
    public void testUnicodeCharacters_inUrl() {
        String unicodeUrl = "https://example.com/event/événement-🎉";
        qrCode.setQrCodeUrl(unicodeUrl);

        assertEquals("Unicode URL should be stored", unicodeUrl, qrCode.getQrCodeUrl());
        assertEquals("EncodedData should return Unicode URL", unicodeUrl, qrCode.getEncodedData());
    }

    @Test
    public void testQrCodeData_doesNotAffect_getEncodedData() {
        // Document that qrCodeData (Base64 image) is separate from encoded data
        qrCode.setQrCodeData(TEST_QR_DATA);

        assertEquals("EncodedData should still return eventId, not qrCodeData",
                     TEST_EVENT_ID, qrCode.getEncodedData());
        assertNotEquals("EncodedData should not be affected by qrCodeData",
                        TEST_QR_DATA, qrCode.getEncodedData());
    }
}
