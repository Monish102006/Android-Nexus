package com.androidnexus.controller;

import com.androidnexus.exception.AdbException;
import com.androidnexus.model.Notification;
import com.androidnexus.service.NotificationService;

import java.util.List;

/**
 * Controller exposing Android status bar notification operations.
 *
 * Provides functions to fetch notifications, apply filters, dismiss entries,
 * and attach background polling monitors.
 */
public class NotificationController {

    /**
     * Retrieves all active notifications from the status bar.
     *
     * @return a list of active notifications
     * @throws AdbException if the command fails
     */
    public static List<Notification> getActiveNotifications() throws AdbException {
        return NotificationService.getActiveNotifications();
    }

    /**
     * Filters a list of active notifications by a category ("whatsapp", "gmail", "telegram", "system").
     */
    public static List<Notification> getNotificationsFiltered(String filterType) throws AdbException {
        List<Notification> active = getActiveNotifications();
        return NotificationService.filterNotifications(active, filterType);
    }

    /**
     * Dismisses a single notification by its key.
     *
     * @param notificationKey the notification key
     * @throws AdbException if the command fails
     */
    public static void dismissNotification(String notificationKey) throws AdbException {
        if (notificationKey == null || notificationKey.isEmpty()) {
            throw new IllegalArgumentException("notificationKey cannot be null or empty");
        }
        NotificationService.dismissNotification(notificationKey);
    }

    /**
     * Dismisses all swipable/clearable notifications.
     *
     * @throws AdbException if the command fails
     */
    public static void dismissAllNotifications() throws AdbException {
        NotificationService.dismissAllNotifications();
    }

    /**
     * Registers a listener and starts background periodic notification updates.
     */
    public static void startMonitoring(NotificationService.NotificationListener listener, int intervalMs) {
        if (listener == null) {
            throw new IllegalArgumentException("listener cannot be null");
        }
        NotificationService.startMonitoring(listener, intervalMs);
    }

    /**
     * Discontinues background periodic notification updates.
     */
    public static void stopMonitoring() {
        NotificationService.stopMonitoring();
    }
}
