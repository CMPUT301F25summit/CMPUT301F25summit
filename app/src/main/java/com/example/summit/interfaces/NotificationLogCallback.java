package com.example.summit.interfaces;

import com.example.summit.model.NotificationLogItem;
import java.util.List;

/**
 * Callback for loading notification logs from Firestore.
 */
public interface NotificationLogCallback {
    void onNotificationsLoaded(List<NotificationLogItem> notifications);
    void onLoadFailure(String error);
}
