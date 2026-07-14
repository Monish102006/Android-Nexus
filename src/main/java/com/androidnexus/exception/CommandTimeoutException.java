package com.androidnexus.exception;

/**
 * Thrown when an ADB command exceeds its allowed execution time.
 *
 * Why this matters:
 * ADB can hang indefinitely in several real-world scenarios:
 *   - Device disconnected mid-command (USB cable pulled)
 *   - ADB server deadlock (known issue with concurrent adb calls)
 *   - Device entered deep sleep during a long file transfer
 *   - Shell command produced infinite output (e.g. logcat without -d)
 *
 * Without a timeout, a single hung command blocks the entire application.
 * CommandExecutor enforces a configurable timeout and throws this exception
 * when it fires, allowing the caller to retry or notify the user.
 */
public class CommandTimeoutException extends AdbException {

    private final int timeoutSeconds;

    /**
     * @param command        the command that timed out (for diagnostics)
     * @param timeoutSeconds the timeout that was exceeded
     */
    public CommandTimeoutException(String command, int timeoutSeconds) {
        super("ADB command timed out after " + timeoutSeconds + "s: " + command);
        this.timeoutSeconds = timeoutSeconds;
    }

    /**
     * @return the timeout value in seconds that was exceeded
     */
    public int getTimeoutSeconds() {
        return timeoutSeconds;
    }
}
