package com.androidnexus.controller;

import com.androidnexus.adb.CommandExecutor;
import com.androidnexus.exception.AdbException;
import com.androidnexus.utils.AndroidKeyCodes;

/**
 * Controls Android power-related actions via ADB.
 *
 * The Power key event (KEYCODE_POWER = 26) toggles the screen:
 *   - If screen is ON  → turns OFF and locks
 *   - If screen is OFF → turns ON (to lock screen, not unlocked)
 *
 * Note: Long-press power (for power menu) is NOT supported via keyevent
 * because ADB's "input keyevent" simulates a short press. Long-press
 * requires "input keyevent --longpress 26" which is available on
 * Android 7.0+ (API 24+).
 */
public class PowerController {

    /**
     * Toggles the device screen on/off by simulating the power button.
     *
     * @throws AdbException if the command fails
     */
    public static void lock() throws AdbException {

        CommandExecutor.executeCommand(
                "adb", "shell", "input", "keyevent",
                AndroidKeyCodes.POWER
        );
    }
}