package com.androidnexus.ui.service;

import com.androidnexus.controller.NotificationController;
import com.androidnexus.model.Notification;
import com.androidnexus.service.NotificationService;
import javafx.concurrent.Task;

import java.util.List;

/**
 * UI Service managing background task coordination for notifications.
 *
 * Safe offloader for notifications lists and dismissal actions.
 */
public class NotificationUiService {

    /**
     * Fetches currently active notifications on a background thread.
     */
    public static Task<List<Notification>> createFetchNotificationsTask() {
        return new Task<>() {
            @Override
            protected List<Notification> call() throws Exception {
                return NotificationController.getActiveNotifications();
            }
        };
    }

    /**
     * Dismisses a single notification on a background thread.
     */
    public static Task<Void> createDismissNotificationTask(String key) {
        return new Task<>() {
            @Override
            protected Void call() throws Exception {
                NotificationController.dismissNotification(key);
                return null;
            }
        };
    }

    /**
     * Dismisses all clearable notifications on a background thread.
     */
    public static Task<Void> createDismissAllNotificationsTask() {
        return new Task<>() {
            @Override
            protected Void call() throws Exception {
                NotificationController.dismissAllNotifications();
                return null;
            }
        };
    }

    /**
     * Registers a listener to receive live updates from the polling thread.
     */
    public static void startLiveSync(NotificationService.NotificationListener listener, int intervalMs) {
        NotificationController.startMonitoring(listener, intervalMs);
    }

    /**
     * Discontinues active background sync.
     */
    public static void stopLiveSync() {
        NotificationController.stopMonitoring();
    }
}
