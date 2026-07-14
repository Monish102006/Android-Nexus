package com.androidnexus.controller;

import com.androidnexus.adb.CommandExecutor;
import com.androidnexus.exception.AdbException;
import com.androidnexus.utils.Constants;

/**
 * Controls screen recording on the connected Android device.
 *
 * How Android screen recording works:
 *   {@code adb shell screenrecord <path>} starts recording to a file on the
 *   device. The process runs until one of:
 *     - 180 seconds elapse (Android hard limit)
 *     - The process is killed (Process.destroy())
 *     - Ctrl+C is sent
 *
 * This controller stores the Process reference and destroys it when
 * stopRecording() is called, then pulls the video file to the local machine.
 *
 * Known limitation:
 * The recording path is currently hardcoded to "/sdcard/demo.mp4".
 * This is tracked in the Technical Backlog — a future improvement will
 * use FileNameGenerator to create timestamped recording names.
 *
 * Android requirements:
 *   - API 19+ (Android 4.4 KitKat)
 *   - Some devices limit recording to specific resolutions
 *   - Maximum duration: 180 seconds (3 minutes)
 */
public class RecordingController {

    private static Process recordingProcess;

    /**
     * Starts screen recording on the device.
     * The recording runs until {@link #stopRecording()} is called
     * or 180 seconds elapse (Android hard limit).
     *
     * @throws AdbException       if the screenrecord process cannot be started
     * @throws IllegalStateException if a recording is already in progress
     */
    public static void startRecording() throws AdbException {

        if (recordingProcess != null && recordingProcess.isAlive()) {
            throw new IllegalStateException(
                    "A recording is already in progress. Stop it before starting a new one."
            );
        }

        recordingProcess = CommandExecutor.executeProcess(
                "adb", "shell", "screenrecord",
                Constants.PHONE_RECORDING_PATH
        );
    }

    /**
     * Stops the current screen recording, pulls the file to the local machine,
     * and deletes the temp file from the device.
     *
     * The 1500ms sleep after process.destroy() is necessary because Android's
     * screenrecord needs time to finalize the MP4 container (write the moov atom).
     * Without this delay, the pulled file may be corrupted/unplayable.
     *
     * @throws AdbException if pulling or deleting the recording file fails
     */
    public static void stopRecording() throws AdbException {

        if (recordingProcess == null) {
            return;
        }

        recordingProcess.destroy();
        recordingProcess = null;

        // Wait for screenrecord to finalize the MP4 container
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Pull recording to local machine
        CommandExecutor.executeCommand(
                "adb", "pull",
                Constants.PHONE_RECORDING_PATH,
                Constants.LOCAL_RECORDING_FOLDER
        );

        // Clean up temp file on device
        CommandExecutor.executeCommand(
                "adb", "shell", "rm",
                Constants.PHONE_RECORDING_PATH
        );
    }
}