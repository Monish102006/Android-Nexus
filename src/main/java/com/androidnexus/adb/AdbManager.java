package com.androidnexus.adb;

import com.androidnexus.exception.AdbException;

/**
 * Manages the ADB binary lifecycle: verification, version, server restart.
 *
 * This is the first class that should be called when the application starts.
 * Before any device operations can work, we need to verify:
 *   1. The ADB binary exists and is executable
 *   2. The ADB server is running (or can be started)
 *   3. The ADB version is known (for compatibility decisions)
 *
 * ADB Server Architecture:
 * ------------------------
 * ADB operates as a client-server system:
 *
 *   [Our App] → [ADB Client] → [ADB Server (port 5037)] → [adbd on device]
 *
 * The ADB server is a background daemon that manages communication with
 * all connected devices. It starts automatically on the first ADB command,
 * but sometimes needs a manual restart when:
 *   - Device connections become stale
 *   - Multiple ADB instances conflict (e.g. Android Studio + our app)
 *   - The server enters a broken state after sleep/resume
 *
 * "adb kill-server" stops the daemon, and the next command auto-restarts it.
 * We explicitly call "adb start-server" for predictability.
 */
public class AdbManager {

    /**
     * Verifies that ADB is available on the system PATH and executable.
     *
     * Runs "adb version" as a smoke test. If this fails, nothing else will work.
     * This should be called once at application startup before any other ADB operations.
     *
     * @return true if ADB is available and responds to commands
     */
    public static boolean verifyAdb() {

        try {
            CommandResult result = CommandExecutor.executeCommand("adb", "version");
            return result.isSuccess()
                    && result.getOutput().contains("Android Debug Bridge");
        } catch (AdbException e) {
            return false;
        }
    }

    /**
     * Returns the ADB version string.
     *
     * Example output: "Android Debug Bridge version 1.0.41"
     * The version string includes the ADB protocol version and revision.
     *
     * @return the first line of "adb version" output (the version string)
     * @throws AdbException if ADB cannot be executed
     */
    public static String getAdbVersion() throws AdbException {

        CommandResult result = CommandExecutor.executeCommand("adb", "version");

        String output = result.getOutput().trim();

        // Return just the first line (the version string)
        // Full output includes revision and install path which we don't need here
        String[] lines = output.split("\n");
        return lines[0].trim();
    }

    /**
     * Restarts the ADB server.
     *
     * This kills the running ADB server daemon and starts a fresh one.
     * Useful when:
     *   - Device connections are stale or broken
     *   - The server is in an inconsistent state
     *   - After USB mode changes on the device
     *
     * Note: This will briefly disconnect ALL devices from ALL ADB clients
     * on this machine (including Android Studio, other tools).
     *
     * @throws AdbException if the server cannot be restarted
     */
    public static void restartServer() throws AdbException {

        CommandExecutor.executeCommand("adb", "kill-server");
        CommandExecutor.executeCommand("adb", "start-server");
    }
}
