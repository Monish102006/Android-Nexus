package com.androidnexus.controller;

import com.androidnexus.adb.CommandExecutor;
import com.androidnexus.exception.AdbException;
import com.androidnexus.utils.AndroidKeyCodes;

/**
 * Controls Android volume and audio state via ADB key events.
 *
 * Android volume model:
 * Android has multiple independent volume streams (media, ring, alarm, etc.).
 * The VOLUME_UP/DOWN key events adjust whichever stream is currently "active":
 *   - During media playback → adjusts media volume
 *   - While ringing → adjusts ringer volume
 *   - Otherwise → adjusts ringer/notification volume
 *
 * VOLUME_MUTE (keycode 164) toggles the master mute state.
 * On some devices/manufacturers, it may only mute the ringer.
 *
 * Previous bug fixed:
 * volumeUp() was sending keycode "3" (HOME) instead of "24" (VOLUME_UP)
 * due to a copy-paste from NavigationController during debugging.
 * It also bypassed CommandExecutor with a raw ProcessBuilder.
 */
public class AudioController {

    /**
     * Increases the active volume stream by one step.
     *
     * @throws AdbException if the command fails
     */
    public static void volumeUp() throws AdbException {

        CommandExecutor.executeCommand(
                "adb", "shell", "input", "keyevent",
                AndroidKeyCodes.VOLUME_UP
        );
    }

    /**
     * Decreases the active volume stream by one step.
     *
     * @throws AdbException if the command fails
     */
    public static void volumeDown() throws AdbException {

        CommandExecutor.executeCommand(
                "adb", "shell", "input", "keyevent",
                AndroidKeyCodes.VOLUME_DOWN
        );
    }

    /**
     * Toggles the master mute state.
     *
     * Note: Behavior varies by manufacturer. Some devices only mute
     * the ringer stream. This is a known Android fragmentation issue
     * and is NOT a bug in this code.
     *
     * @throws AdbException if the command fails
     */
    public static void mute() throws AdbException {

        CommandExecutor.executeCommand(
                "adb", "shell", "input", "keyevent",
                AndroidKeyCodes.VOLUME_MUTE
        );
    }
}