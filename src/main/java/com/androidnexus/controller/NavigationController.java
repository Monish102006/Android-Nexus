package com.androidnexus.controller;

import com.androidnexus.adb.CommandExecutor;
import com.androidnexus.exception.AdbException;
import com.androidnexus.utils.AndroidKeyCodes;

/**
 * Controls Android navigation buttons via ADB key events.
 *
 * Maps to the physical/virtual navigation bar on Android devices:
 *   - Home: Return to launcher home screen
 *   - Back: Navigate to previous screen (Activity back stack pop)
 *   - Recent Apps: Open the task switcher (Overview screen)
 *
 * Also controls status bar expansion, which isn't a key event but a
 * system service command (cmd statusbar).
 *
 * ADB command pattern: {@code adb shell input keyevent <KEYCODE>}
 */
public class NavigationController {

    /**
     * Presses the Home button.
     * Equivalent to pressing the home button on the navigation bar.
     * Returns to the default launcher activity.
     *
     * @throws AdbException if the command fails
     */
    public static void home() throws AdbException {

        CommandExecutor.executeCommand(
                "adb", "shell", "input", "keyevent",
                AndroidKeyCodes.HOME
        );
    }

    /**
     * Presses the Back button.
     * Pops the current Activity from the back stack.
     * In a dialog, dismisses the dialog.
     * On the home screen, does nothing on most launchers.
     *
     * @throws AdbException if the command fails
     */
    public static void back() throws AdbException {

        CommandExecutor.executeCommand(
                "adb", "shell", "input", "keyevent",
                AndroidKeyCodes.BACK
        );
    }

    /**
     * Presses the Recent Apps (Overview / App Switcher) button.
     * Opens the task switcher showing recent activities.
     *
     * @throws AdbException if the command fails
     */
    public static void recentApps() throws AdbException {

        CommandExecutor.executeCommand(
                "adb", "shell", "input", "keyevent",
                AndroidKeyCodes.APP_SWITCH
        );
    }

    /**
     * Expands the notification shade.
     * Uses the StatusBar system service rather than a key event.
     * Command: {@code adb shell cmd statusbar expand-notifications}
     *
     * @throws AdbException if the command fails
     */
    public static void notifications() throws AdbException {

        CommandExecutor.executeCommand(
                "adb", "shell", "cmd", "statusbar",
                "expand-notifications"
        );
    }

    /**
     * Expands the Quick Settings panel (full shade pull-down).
     * Command: {@code adb shell cmd statusbar expand-settings}
     *
     * @throws AdbException if the command fails
     */
    public static void quickSettings() throws AdbException {

        CommandExecutor.executeCommand(
                "adb", "shell", "cmd", "statusbar",
                "expand-settings"
        );
    }
}
