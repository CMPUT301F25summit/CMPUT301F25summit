package com.example.summit.interfaces;

/**
 * Callback interface for notification migration operations.
 * Used to report the status of migrating historical notifications from
 * the "notifications" collection to the "notification_logs" collection.
 */
public interface MigrationCallback {
    /**
     * Called when the migration completes successfully.
     *
     * @param successCount Number of notifications successfully migrated
     * @param failureCount Number of notifications that failed to migrate
     */
    void onMigrationComplete(int successCount, int failureCount);

    /**
     * Called when a critical error occurs during migration.
     *
     * @param error Error message describing what went wrong
     */
    void onMigrationError(String error);
}
