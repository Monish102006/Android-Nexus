package com.androidnexus.controller;

import com.androidnexus.adb.CommandExecutor;
import com.androidnexus.utils.Constants;

public class DeviceController {
    public static void takeScreenshot() {
        ScreenshotController.takeScreenshot();
    }

    public static void lock() {
        PowerController.lock();
    }
    public static void home() {
        NavigationController.home();
    }

    public static void back() {
        NavigationController.back();
    }

    public static void recentApps() {
        NavigationController.recentApps();
    }

    public static void notifications() {
        NavigationController.notifications();
    }

    public static void quickSettings() {
        NavigationController.quickSettings();
    }

    public static void startRecording() {
        RecordingController.startRecording();
    }

    public static void stopRecording() {
        RecordingController.stopRecording();
    }
}