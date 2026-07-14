package com.androidnexus.service;

import com.androidnexus.adb.CommandExecutor;
import com.androidnexus.exception.AdbException;
import com.androidnexus.utils.Constants;

/**
 * Manages the scrcpy screen mirroring process.
 *
 * What is scrcpy?
 * ---------------
 * scrcpy (Screen Copy) is an open-source tool that displays and controls
 * Android devices connected via USB or TCP/IP. It works by:
 *   1. Pushing a small server JAR to the device
 *   2. The server captures the screen using the MediaCodec API
 *   3. Encoded video frames are sent over ADB to the desktop client
 *   4. The desktop client renders them using SDL
 *
 * scrcpy does NOT require root or any app installation.
 * It uses the hidden Android API (android.media.MediaCodec) via the server.
 *
 * Current limitations (Technical Backlog):
 *   - No stop() method (the user closes the scrcpy window manually)
 *   - No configuration (resolution, bitrate, crop)
 *   - No TCP/IP wireless support
 */
public class ScrcpyService {

    /**
     * Launches scrcpy as a background process.
     * The scrcpy window opens and mirrors the device screen.
     *
     * @throws AdbException if scrcpy cannot be started (e.g. binary not found)
     */
    public static void launch() throws AdbException {

        CommandExecutor.executeProcess(
                Constants.SCRCPY_PATH
        );
    }
}