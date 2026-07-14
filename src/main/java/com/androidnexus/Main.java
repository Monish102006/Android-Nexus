package com.androidnexus;

import com.androidnexus.adb.AdbManager;
import com.androidnexus.adb.DeviceDetector;
import com.androidnexus.controller.FileController;
import com.androidnexus.exception.AdbException;
import com.androidnexus.model.AndroidFile;
import com.androidnexus.model.Device;
import com.androidnexus.service.DeviceService;

import java.util.List;

/**
 * Application entry point — console-mode test harness.
 *
 * This class serves as a manual integration test during development.
 * Each module's features are tested here via direct method calls before
 * being wired into the JavaFX Dashboard (Module 8).
 *
 * Startup sequence:
 *   1. Verify ADB is available on PATH
 *   2. Check for connected device
 *   3. Retrieve device information
 *   4. Run current module's test code
 */
public class Main {

    public static void main(String[] args) {
        // Boot the JavaFX desktop application
        com.androidnexus.ui.AppLoader.launchApp(args);
    }
}