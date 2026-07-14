package com.androidnexus.exception;

/**
 * Base exception for all ADB-related failures.
 *
 * Why a dedicated hierarchy instead of raw IOException / RuntimeException?
 * -----------------------------------------------------------------------
 * 1. Callers can distinguish "ADB failed" from "disk full" or "null pointer"
 *    with a single catch clause.
 * 2. We carry structured context (exitCode, stderr) that IOException cannot.
 * 3. Subclasses (DeviceNotFoundException, CommandTimeoutException) let callers
 *    react differently to "no device" vs. "command hung" without parsing strings.
 *
 * Design decision: checked exception.
 * ADB failures are recoverable (reconnect device, retry command) and must not
 * silently propagate up the stack.  A checked exception forces every caller to
 * make a conscious decision: handle, wrap, or declare.
 */
public class AdbException extends Exception {

    private final int exitCode;
    private final String errorOutput;

    /**
     * Constructs an AdbException with a human-readable message.
     *
     * @param message describes what went wrong in plain English
     */
    public AdbException(String message) {
        super(message);
        this.exitCode = -1;
        this.errorOutput = "";
    }

    /**
     * Constructs an AdbException wrapping a lower-level cause.
     *
     * @param message describes what went wrong in plain English
     * @param cause   the underlying exception (usually IOException)
     */
    public AdbException(String message, Throwable cause) {
        super(message, cause);
        this.exitCode = -1;
        this.errorOutput = "";
    }

    /**
     * Constructs an AdbException with full process failure context.
     *
     * @param message     describes what went wrong
     * @param exitCode    the process exit code returned by ADB
     * @param errorOutput the content of stderr
     */
    public AdbException(String message, int exitCode, String errorOutput) {
        super(message);
        this.exitCode = exitCode;
        this.errorOutput = errorOutput != null ? errorOutput : "";
    }

    /**
     * @return the ADB process exit code, or -1 if the process never started
     */
    public int getExitCode() {
        return exitCode;
    }

    /**
     * @return the content written to stderr by the ADB process, never null
     */
    public String getErrorOutput() {
        return errorOutput;
    }
}
