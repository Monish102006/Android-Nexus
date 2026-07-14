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

        System.out.println("=================================");
        System.out.println("       Android Nexus");
        System.out.println("=================================\n");

        // ── Step 1: Verify ADB Installation ─────────────────────────────
        System.out.println("Checking ADB...\n");

        if (!AdbManager.verifyAdb()) {
            System.out.println("🔴 ADB not found. Please install ADB and add it to PATH.");
            return;
        }

        try {
            String version = AdbManager.getAdbVersion();
            System.out.println("✅ " + version);
        } catch (AdbException e) {
            System.out.println("⚠️  ADB found but version check failed: " + e.getMessage());
        }

        // ── Step 2: Check Device Connection ─────────────────────────────
        try {
            boolean connected = DeviceDetector.isDeviceConnected();

            if (connected) {
                System.out.println("🟢 Device Connected\n");
            } else {
                System.out.println("🔴 No Device Connected");
                return;
            }

            // ── Step 3: Device Information ──────────────────────────────
            Device device = DeviceService.getDeviceInformation();
            System.out.println(device);
            System.out.println();

            // ── Step 4: File Manager Test (Module 5) ────────────────────
            System.out.println("── File Listing: /sdcard/Download ──\n");

            List<AndroidFile> files = FileController.listFiles("/sdcard/Download");

            for (AndroidFile file : files) {
                System.out.printf(
                        "%-6s %-10d %s%n",
                        file.isDirectory() ? "[DIR]" : "[FILE]",
                        file.getSize(),
                        file.getName()
                );
            }

        } catch (AdbException e) {
            System.out.println("🔴 ADB Error: " + e.getMessage());
            if (!e.getErrorOutput().isEmpty()) {
                System.out.println("   stderr: " + e.getErrorOutput().trim());
            }
        }
    }
}