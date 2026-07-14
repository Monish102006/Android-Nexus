package com.androidnexus.adb;

import com.androidnexus.exception.AdbException;
import com.androidnexus.exception.CommandTimeoutException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

/**
 * Centralized process executor for all ADB commands.
 *
 * Architecture role:
 * -----------------
 * This is the ONLY class that spawns OS processes. Every Service, Controller,
 * and Manager goes through here. This single chokepoint gives us:
 *   1. Consistent error handling  — no more swallowed IOExceptions
 *   2. Timeout enforcement        — no more infinite hangs
 *   3. stderr capture             — ADB error messages are no longer lost
 *   4. Structured results         — CommandResult instead of raw String
 *
 * Previous problems fixed:
 *   - IOException was caught and returned as "ERROR: " string. No caller checked.
 *   - stderr was never read. If ADB wrote enough to stderr to fill the OS pipe
 *     buffer (typically 64 KB), the process would deadlock.
 *   - No timeout. A disconnected device would hang executeCommand() forever.
 *   - executeProcess() returned null on failure. Callers had to null-check.
 *
 * Thread safety:
 *   All methods are stateless and thread-safe. Each call creates its own
 *   ProcessBuilder and reads its own streams.
 */
public class CommandExecutor {

    /** Default timeout for ADB commands that should complete quickly. */
    private static final int DEFAULT_TIMEOUT_SECONDS = 30;

    /**
     * Executes an ADB command and waits for it to complete with the default timeout.
     *
     * Captures both stdout and stderr. Does NOT throw on non-zero exit codes —
     * the caller decides how to handle failure by inspecting CommandResult
     * or calling {@link CommandResult#requireSuccess()}.
     *
     * @param command the command and arguments (e.g. "adb", "shell", "ls", "-l")
     * @return a CommandResult with stdout, stderr, and exit code
     * @throws AdbException            if the process cannot be started (e.g. adb not on PATH)
     * @throws CommandTimeoutException if the command exceeds the default timeout
     */
    public static CommandResult executeCommand(String... command) throws AdbException {
        return executeCommand(DEFAULT_TIMEOUT_SECONDS, command);
    }

    /**
     * Executes an ADB command with a custom timeout.
     *
     * Use this overload for commands known to take longer than the default:
     *   - File transfers (adb pull / push large files)
     *   - Package installation
     *   - Database dumps
     *
     * @param timeoutSeconds maximum time to wait for the process to finish
     * @param command        the command and arguments
     * @return a CommandResult with stdout, stderr, and exit code
     * @throws AdbException            if the process cannot be started
     * @throws CommandTimeoutException if the command exceeds the specified timeout
     */
    public static CommandResult executeCommand(int timeoutSeconds, String... command)
            throws AdbException {

        String commandString = String.join(" ", command);

        try {

            ProcessBuilder processBuilder = new ProcessBuilder(command);

            /*
             * Why NOT redirectErrorStream(true)?
             * We want stderr separate from stdout so that:
             *   1. Parsers (AndroidFileParser, BatteryParser) receive clean data
             *      without ADB warnings mixed in.
             *   2. Error messages are available in CommandResult.getErrorOutput()
             *      for diagnostics without polluting the parsed output.
             *
             * We read both streams to prevent the pipe buffer deadlock that
             * occurred when stderr was ignored.
             */
            Process process = processBuilder.start();

            /*
             * Read stdout and stderr concurrently.
             *
             * Why a separate thread for stderr?
             * If stdout fills the OS pipe buffer while we're waiting to read stderr
             * (or vice versa), the child process blocks on write() and we block on
             * read() — classic deadlock. Reading one stream on a background thread
             * prevents this.
             */
            StringBuilder stdoutBuilder = new StringBuilder();
            StringBuilder stderrBuilder = new StringBuilder();

            Thread stderrThread = new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getErrorStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        stderrBuilder.append(line).append("\n");
                    }
                } catch (IOException e) {
                    stderrBuilder.append("Failed to read stderr: ")
                            .append(e.getMessage());
                }
            }, "stderr-reader");
            stderrThread.setDaemon(true);
            stderrThread.start();

            // Read stdout on the calling thread
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    stdoutBuilder.append(line).append("\n");
                }
            }

            // Wait for the process with timeout
            boolean finished = process.waitFor(timeoutSeconds, TimeUnit.SECONDS);

            if (!finished) {
                process.destroyForcibly();
                throw new CommandTimeoutException(commandString, timeoutSeconds);
            }

            // Wait for stderr thread to finish (it should be done since the process ended)
            stderrThread.join(2000);

            return new CommandResult(
                    stdoutBuilder.toString(),
                    stderrBuilder.toString(),
                    process.exitValue()
            );

        } catch (CommandTimeoutException e) {
            // Re-throw — don't wrap a timeout inside a generic AdbException
            throw e;

        } catch (IOException e) {
            throw new AdbException(
                    "Failed to execute command: " + commandString, e
            );

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AdbException(
                    "Command interrupted: " + commandString, e
            );
        }
    }

    /**
     * Starts a long-running ADB process without waiting for it to finish.
     *
     * Used for processes that run indefinitely until explicitly stopped:
     *   - scrcpy (screen mirroring)
     *   - screenrecord (video capture)
     *   - logcat (continuous log streaming)
     *
     * The caller is responsible for:
     *   1. Storing the returned Process reference
     *   2. Calling process.destroy() or process.destroyForcibly() when done
     *   3. Reading stdout/stderr if needed (to prevent pipe buffer deadlock)
     *
     * @param command the command and arguments
     * @return the running Process instance (never null)
     * @throws AdbException if the process cannot be started
     */
    public static Process executeProcess(String... command) throws AdbException {

        try {

            ProcessBuilder processBuilder = new ProcessBuilder(command);

            return processBuilder.start();

        } catch (IOException e) {
            throw new AdbException(
                    "Failed to start process: " + String.join(" ", command), e
            );
        }
    }
}