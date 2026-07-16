package com.androidnexus.controller;

import com.androidnexus.adb.CommandExecutor;
import com.androidnexus.adb.CommandResult;
import com.androidnexus.adb.DeviceDetector;
import com.androidnexus.exception.AdbException;
import com.androidnexus.model.Notification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assumptions;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class NotificationControllerIntegrationTest {

    @BeforeEach
    public void setUp() {
        boolean isConnected = false;
        try {
            isConnected = DeviceDetector.isDeviceConnected();
        } catch (AdbException e) {
            // ignore
        }
        Assumptions.assumeTrue(isConnected, "ADB device not connected. Skipping integration tests.");
    }

    @Test
    public void testNotificationLifecycleIntegration() throws Exception {
        // 1. Post a test notification via adb cmd notification post
        String title = "NexusIntegrationTestTitle_" + System.currentTimeMillis();
        String text = "TestBody_" + System.currentTimeMillis();
        // Use dynamic tag to avoid collisions with any previously snoozed notification keys!
        String tag = "NexusTag_" + System.currentTimeMillis();

        // cmd notification post [-t title] <tag> <text>
        CommandResult postResult = CommandExecutor.executeCommand(
                "adb", "shell", "cmd", "notification", "post",
                "-t", title, tag, text
        );
        postResult.requireSuccess();

        // Give the device status bar half a second to register and render the notification
        Thread.sleep(500);

        // 2. Fetch all active notifications and find our test entry
        List<Notification> active = NotificationController.getActiveNotifications();
        assertNotNull(active);

        Notification target = null;
        for (Notification n : active) {
            if (n.getPackageName().equals("com.android.shell") && title.equals(n.getTitle())) {
                target = n;
                break;
            }
        }

        assertNotNull(target, "Could not find the posted test notification");
        assertEquals(text, target.getText());
        assertTrue(target.isClearable());
        assertFalse(target.isOngoing());

        // 3. Dismiss the posted notification
        NotificationController.dismissNotification(target.getKey());

        // Give it a brief moment to clear
        Thread.sleep(300);

        // 4. Verify it has been cleared
        List<Notification> activeAfter = NotificationController.getActiveNotifications();
        boolean foundAfter = false;
        for (Notification n : activeAfter) {
            if (n.getKey().equals(target.getKey())) {
                foundAfter = true;
                break;
            }
        }

        assertFalse(foundAfter, "Notification should have been dismissed and cleared");
    }
}
