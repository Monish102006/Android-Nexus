package com.androidnexus.service;

import com.androidnexus.adb.CommandExecutor;
import com.androidnexus.adb.CommandResult;
import com.androidnexus.exception.AdbException;
import com.androidnexus.model.Notification;
import com.androidnexus.parser.NotificationParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Service to manage and read Android status bar notifications.
 *
 * Utilizes "dumpsys notification" for reading active notifications, and
 * standard shell "cmd notification" commands for dismissal.
 */
public class NotificationService {

    private static Thread monitoringThread;
    private static volatile boolean monitoringActive = false;

    /**
     * Listener interface for background live notification updates.
     */
    public interface NotificationListener {
        void onNotificationsUpdated(List<Notification> activeNotifications);
        void onError(Throwable throwable);
    }

    /**
     * Retrieves all currently active status bar notifications on the device.
     *
     * @return a list of active notifications
     * @throws AdbException if the command execution fails
     */
    public static List<Notification> getActiveNotifications() throws AdbException {
        // We run dumpsys notification with --noredact to read message contents.
        // Fallback to standard dumpsys if --noredact is rejected on older APIs.
        CommandResult result;
        try {
            result = CommandExecutor.executeCommand(
                    "adb", "shell", "dumpsys", "notification", "--noredact"
            );
        } catch (AdbException e) {
            // Fallback for older APIs without --noredact support
            result = CommandExecutor.executeCommand(
                    "adb", "shell", "dumpsys", "notification"
            );
        }

        result.requireSuccess();
        return NotificationParser.parse(result.getOutput());
    }

    /**
     * Filters a list of notifications by package name or category.
     *
     * Category keywords: "whatsapp", "gmail", "telegram", "system".
     */
    public static List<Notification> filterNotifications(List<Notification> list, String filterType) {
        if (list == null || filterType == null || filterType.isEmpty()) {
            return list;
        }

        List<Notification> filtered = new ArrayList<>();
        String normalized = filterType.toLowerCase();

        for (Notification n : list) {
            String pkg = n.getPackageName().toLowerCase();

            switch (normalized) {
                case "whatsapp":
                    if (pkg.contains("com.whatsapp")) filtered.add(n);
                    break;
                case "gmail":
                    if (pkg.contains("com.google.android.gm")) filtered.add(n);
                    break;
                case "telegram":
                    if (pkg.contains("org.telegram.messenger") || pkg.contains("org.telegram")) filtered.add(n);
                    break;
                case "system":
                    if (pkg.equals("android") || pkg.startsWith("com.android.") || pkg.startsWith("com.google.android.system")) {
                        filtered.add(n);
                    }
                    break;
                default:
                    // Fallback to custom package name query matching
                    if (pkg.contains(normalized)) {
                        filtered.add(n);
                    }
                    break;
            }
        }
        return filtered;
    }

    /**
     * Dismisses (cancels) a notification by its unique key.
     *
     * @param notificationKey the notification key (e.g. "0|com.whatsapp|1001|null|10123")
     * @throws AdbException if the command fails
     */
    public static void dismissNotification(String notificationKey) throws AdbException {
        if (notificationKey == null || notificationKey.isEmpty()) {
            throw new IllegalArgumentException("notificationKey cannot be null or empty");
        }

        // Use 'cmd notification snooze' to dismiss the notification, wrapping in single quotes to escape pipes (|)
        String escapedKey = "'" + notificationKey + "'";
        CommandResult result = CommandExecutor.executeCommand(
                "adb", "shell", "cmd", "notification", "snooze", "--for", "31536000000000", escapedKey
        );
        result.requireSuccess();
    }

    /**
     * Dismisses all clearable notifications currently active on the device.
     *
     * @throws AdbException if the command fails
     */
    public static void dismissAllNotifications() throws AdbException {
        List<Notification> active = getActiveNotifications();
        for (Notification n : active) {
            if (n.isClearable()) {
                dismissNotification(n.getKey());
            }
        }
    }

    /**
     * Starts a background polling executor thread to monitor live notification updates.
     *
     * @param listener   the listener to receive updates or errors
     * @param intervalMs the refresh interval in milliseconds
     */
    public static synchronized void startMonitoring(NotificationListener listener, int intervalMs) {
        if (monitoringActive) {
            return;
        }
        monitoringActive = true;
        monitoringThread = new Thread(() -> {
            while (monitoringActive) {
                try {
                    List<Notification> active = getActiveNotifications();
                    listener.onNotificationsUpdated(active);
                } catch (Exception e) {
                    listener.onError(e);
                }
                try {
                    Thread.sleep(intervalMs);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }, "notification-monitor");
        monitoringThread.setDaemon(true);
        monitoringThread.start();
    }

    /**
     * Stops the live notification monitoring thread.
     */
    public static synchronized void stopMonitoring() {
        monitoringActive = false;
        if (monitoringThread != null) {
            monitoringThread.interrupt();
            monitoringThread = null;
        }
    }
}
