package com.androidnexus.controller;

import com.androidnexus.exception.AdbException;
import com.androidnexus.model.AndroidApplication;
import com.androidnexus.service.ApplicationService;

import java.util.List;

/**
 * Controller exposing Android application management functionality.
 *
 * Validates inputs at the controller level before delegating to the service layer.
 */
public class ApplicationController {

    /**
     * Lists all installed applications on the device.
     */
    public static List<AndroidApplication> getInstalledApplications() throws AdbException {
        return ApplicationService.getInstalledApplications();
    }

    /**
     * Lists only third-party user-installed applications.
     */
    public static List<AndroidApplication> getUserApplications() throws AdbException {
        return ApplicationService.getUserApplications();
    }

    /**
     * Retrieves detailed metadata of a single application.
     */
    public static AndroidApplication getApplicationDetails(String packageName) throws AdbException {
        if (packageName == null || packageName.isEmpty()) {
            throw new IllegalArgumentException("packageName cannot be null or empty");
        }
        return ApplicationService.getApplicationDetails(packageName);
    }

    /**
     * Installs an APK from the PC onto the device.
     */
    public static void installApplication(String localApkPath) throws AdbException {
        if (localApkPath == null || localApkPath.isEmpty()) {
            throw new IllegalArgumentException("localApkPath cannot be null or empty");
        }
        ApplicationService.installApplication(localApkPath);
    }

    /**
     * Uninstalls an application by package name.
     */
    public static void uninstallApplication(String packageName) throws AdbException {
        if (packageName == null || packageName.isEmpty()) {
            throw new IllegalArgumentException("packageName cannot be null or empty");
        }
        ApplicationService.uninstallApplication(packageName);
    }

    /**
     * Launches the application's launcher activity.
     */
    public static void launchApplication(String packageName) throws AdbException {
        if (packageName == null || packageName.isEmpty()) {
            throw new IllegalArgumentException("packageName cannot be null or empty");
        }
        ApplicationService.launchApplication(packageName);
    }

    /**
     * Force-stops the application's processes.
     */
    public static void forceStopApplication(String packageName) throws AdbException {
        if (packageName == null || packageName.isEmpty()) {
            throw new IllegalArgumentException("packageName cannot be null or empty");
        }
        ApplicationService.forceStopApplication(packageName);
    }

    /**
     * Clears all database, cache, and preference data of the application.
     */
    public static void clearApplicationData(String packageName) throws AdbException {
        if (packageName == null || packageName.isEmpty()) {
            throw new IllegalArgumentException("packageName cannot be null or empty");
        }
        ApplicationService.clearApplicationData(packageName);
    }

    /**
     * Pulls the APK file of the package to the local machine directory.
     */
    public static void extractApk(String packageName, String localDirectory) throws AdbException {
        if (packageName == null || packageName.isEmpty()) {
            throw new IllegalArgumentException("packageName cannot be null or empty");
        }
        if (localDirectory == null || localDirectory.isEmpty()) {
            throw new IllegalArgumentException("localDirectory cannot be null or empty");
        }
        ApplicationService.extractApk(packageName, localDirectory);
    }
}
