package com.androidnexus.adb;

import com.androidnexus.exception.AdbException;

/**
 * Immutable value object representing the outcome of a single ADB command execution.
 *
 * Why not just return a String?
 * ----------------------------
 * The previous approach returned stdout as a String and encoded errors as
 * "ERROR: <message>" — a stringly-typed API that no caller ever checked.
 * CommandResult gives callers structured access to:
 *   - stdout  (the actual data)
 *   - stderr  (ADB diagnostics — previously lost entirely)
 *   - exitCode (machine-readable success/failure)
 *
 * Usage patterns:
 *   // Pattern 1: I need the output, fail loudly if something went wrong
 *   String output = CommandExecutor.executeCommand("adb", "devices")
 *                                  .requireSuccess()
 *                                  .getOutput();
 *
 *   // Pattern 2: I want to check success myself
 *   CommandResult result = CommandExecutor.executeCommand("adb", "shell", "ls", path);
 *   if (result.isSuccess()) { ... }
 *
 *   // Pattern 3: Fire-and-forget (navigation keys, etc.)
 *   CommandExecutor.executeCommand("adb", "shell", "input", "keyevent", "3");
 */
public class CommandResult {

    private final String output;
    private final String errorOutput;
    private final int exitCode;

    /**
     * @param output      content captured from stdout (never null)
     * @param errorOutput content captured from stderr (never null)
     * @param exitCode    process exit code (0 = success by Unix convention)
     */
    public CommandResult(String output, String errorOutput, int exitCode) {
        this.output = output != null ? output : "";
        this.errorOutput = errorOutput != null ? errorOutput : "";
        this.exitCode = exitCode;
    }

    /**
     * @return the full stdout content, never null
     */
    public String getOutput() {
        return output;
    }

    /**
     * @return the full stderr content, never null
     */
    public String getErrorOutput() {
        return errorOutput;
    }

    /**
     * @return the process exit code; 0 indicates success
     */
    public int getExitCode() {
        return exitCode;
    }

    /**
     * @return true if the process exited with code 0
     */
    public boolean isSuccess() {
        return exitCode == 0;
    }

    /**
     * Fluent guard: returns this result if successful, throws if not.
     * Designed for call-chaining:
     *   result.requireSuccess().getOutput()
     *
     * @return this instance (for chaining)
     * @throws AdbException if the exit code is non-zero
     */
    public CommandResult requireSuccess() throws AdbException {
        if (!isSuccess()) {
            String message = "ADB command failed with exit code " + exitCode;
            if (!errorOutput.isEmpty()) {
                message += ": " + errorOutput.trim();
            }
            throw new AdbException(message, exitCode, errorOutput);
        }
        return this;
    }

    @Override
    public String toString() {
        return "CommandResult{" +
                "exitCode=" + exitCode +
                ", output='" + (output.length() > 100
                        ? output.substring(0, 100) + "..."
                        : output) + '\'' +
                ", errorOutput='" + errorOutput + '\'' +
                '}';
    }
}
