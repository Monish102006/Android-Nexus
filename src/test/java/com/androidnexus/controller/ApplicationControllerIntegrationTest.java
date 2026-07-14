package com.androidnexus.controller;

import com.androidnexus.adb.DeviceDetector;
import com.androidnexus.exception.AdbException;
import com.androidnexus.model.AndroidApplication;
import com.androidnexus.model.ApplicationType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assumptions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ApplicationControllerIntegrationTest {

    private Path localTempDir;

    @BeforeEach
    public void setUp() throws Exception {
        boolean isConnected = false;
        try {
            isConnected = DeviceDetector.isDeviceConnected();
        } catch (AdbException e) {
            // ignore
        }
        Assumptions.assumeTrue(isConnected, "ADB device not connected. Skipping integration tests.");
        localTempDir = Files.createTempDirectory("nexus_app_integration_");
    }

    @AfterEach
    public void tearDown() {
        if (localTempDir != null) {
            try {
                Files.walk(localTempDir)
                        .map(Path::toFile)
                        .forEach(java.io.File::delete);
            } catch (IOException e) {
                // Ignore
            }
        }
    }

    @Test
    public void testAppLifecycleIntegration() throws Exception {
        // 1. Get all installed applications
        List<AndroidApplication> apps = ApplicationController.getInstalledApplications();
        assertNotNull(apps);
        assertTrue(apps.size() > 0, "Device should have installed apps");

        // 2. Find a common system app that exists on almost all Android devices (e.g. android / com.android.settings / etc.)
        AndroidApplication settingsApp = null;
        for (AndroidApplication app : apps) {
            if (app.getPackageName().equals("com.android.settings") || 
                app.getPackageName().equals("android") ||
                app.getPackageName().contains("settings")) {
                settingsApp = app;
                break;
            }
        }

        assertNotNull(settingsApp, "Could not find a settings or core android package");

        // Verify basic properties
        assertNotNull(settingsApp.getPackageName());
        assertNotNull(settingsApp.getApkPath());

        // 3. Get application details
        AndroidApplication details = ApplicationController.getApplicationDetails(settingsApp.getPackageName());
        assertNotNull(details);
        assertEquals(settingsApp.getPackageName(), details.getPackageName());
        assertNotNull(details.getVersionName(), "Version name should be retrieved");
        assertTrue(details.getVersionCode() >= 0, "Version code should be non-negative");
        
        // 4. Extract APK
        ApplicationController.extractApk(settingsApp.getPackageName(), localTempDir.toAbsolutePath().toString());
        Path extractedApk = localTempDir.resolve(settingsApp.getPackageName() + ".apk");
        
        assertTrue(Files.exists(extractedApk), "Extracted APK should exist locally");
        assertTrue(Files.size(extractedApk) > 0, "Extracted APK size should be greater than 0");
    }
}
