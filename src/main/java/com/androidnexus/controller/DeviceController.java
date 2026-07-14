package com.androidnexus.controller;

import com.androidnexus.exception.AdbException;

/**
 * High-level facade for all device control operations.
 *
 * Design pattern: Facade
 * ----------------------
 * DeviceController delegates every call to a specialized sub-controller.
 * This gives callers (Main, future JavaFX UI) a single entry point while
 * keeping each sub-controller focused on one responsibility:
 *
 *   DeviceController
 *     ├── ScreenshotController  — screen capture
 *     ├── NavigationController  — home, back, recent apps
 *     ├── PowerController       — lock / screen toggle
 *     ├── RecordingController   — screen recording
 *     └── AudioController       — volume control
 *
 * This separation was introduced in commit bfb6810 ("refactor: split device
 * controller into feature-specific controllers").
 */
public class DeviceController {

    // ── Screenshot ──────────────────────────────────────────────────────

    /**
     * Captures a screenshot with a timestamped filename.
     * @throws AdbException if the capture fails
     * @see ScreenshotController#takeScreenshot()
     */
    public static void takeScreenshot() throws AdbException {
        ScreenshotController.takeScreenshot();
    }

    // ── Power ───────────────────────────────────────────────────────────

    /**
     * Toggles screen on/off (simulates power button press).
     * @throws AdbException if the command fails
     */
    public static void lock() throws AdbException {
        PowerController.lock();
    }

    // ── Navigation ──────────────────────────────────────────────────────

    /** @throws AdbException if the command fails */
    public static void home() throws AdbException {
        NavigationController.home();
    }

    /** @throws AdbException if the command fails */
    public static void back() throws AdbException {
        NavigationController.back();
    }

    /** @throws AdbException if the command fails */
    public static void recentApps() throws AdbException {
        NavigationController.recentApps();
    }

    /** @throws AdbException if the command fails */
    public static void notifications() throws AdbException {
        NavigationController.notifications();
    }

    /** @throws AdbException if the command fails */
    public static void quickSettings() throws AdbException {
        NavigationController.quickSettings();
    }

    // ── Recording ───────────────────────────────────────────────────────

    /** @throws AdbException if the command fails */
    public static void startRecording() throws AdbException {
        RecordingController.startRecording();
    }

    /** @throws AdbException if the command fails */
    public static void stopRecording() throws AdbException {
        RecordingController.stopRecording();
    }

    // ── Audio ───────────────────────────────────────────────────────────

    /** @throws AdbException if the command fails */
    public static void volumeUp() throws AdbException {
        AudioController.volumeUp();
    }

    /** @throws AdbException if the command fails */
    public static void volumeDown() throws AdbException {
        AudioController.volumeDown();
    }

    /** @throws AdbException if the command fails */
    public static void mute() throws AdbException {
        AudioController.mute();
    }

    // ── Device Info ─────────────────────────────────────────────────────

    /**
     * Retrieves the connected device information including hardware metrics.
     */
    public static com.androidnexus.model.Device getDevice() throws AdbException {
        return com.androidnexus.service.DeviceService.getDeviceInformation();
    }

    /**
     * Retrieves the screen resolution (width x height) of the device.
     */
    public static String getScreenResolution() throws AdbException {
        return com.androidnexus.service.DeviceService.getScreenResolution();
    }

    /**
     * Retrieves storage information: [0] = total, [1] = available.
     */
    public static String[] getStorageInfo() throws AdbException {
        return com.androidnexus.service.DeviceService.getStorageInfo();
    }
}