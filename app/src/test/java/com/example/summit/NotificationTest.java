package com.example.summit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.example.summit.model.Notification;

import org.junit.Test;

public class NotificationTest {
    @Test
    public void testFullConstructorAndGetters() {
        String id = "notif-001";
        String eventId = "event-abc";
        String recipientId = "user-xyz";
        String message = "Your event is starting soon!";
        long timestamp = 1678886400000L;

        Notification notification = new Notification(id, eventId, recipientId, message, timestamp);

        assertEquals(id, notification.getId());
        assertEquals(eventId, notification.getEventId());
        assertEquals(recipientId, notification.getRecipientId());
        assertEquals(message, notification.getMessage());
        assertEquals(timestamp, notification.getTimestamp());
    }

    @Test
    public void testEmptyConstructor() {
        Notification notification = new Notification();

        assertNotNull(notification);

        assertNull(notification.getId());
        assertNull(notification.getEventId());
        assertNull(notification.getRecipientId());
        assertNull(notification.getMessage());

        assertEquals(0L, notification.getTimestamp());
    }
}
