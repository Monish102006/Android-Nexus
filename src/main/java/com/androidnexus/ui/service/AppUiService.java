package com.androidnexus.ui.service;

import com.androidnexus.controller.ApplicationController;
import com.androidnexus.model.AndroidApplication;
import javafx.concurrent.Task;

import java.io.File;
import java.util.List;

/**
 * UI Service managing background task coordination for APK manager actions.
 *
 * Adheres strictly to the UI concurrency rule (off-loading ADB and I/O to background workers).
 */
public class AppUiService {

    /**
     * Lists installed applications.
     */
    public static Task<List<AndroidApplication>> createListAppsTask() {
        return new Task<>() {
            @Override
            protected List<AndroidApplication> call() throws Exception {
                return ApplicationController.getInstalledApplications();
            }
        };
    }

    /**
     * Launches an application.
     */
    public static Task<Void> createLaunchAppTask(String packageName) {
        return new Task<>() {
            @Override
            protected Void call() throws Exception {
                ApplicationController.launchApplication(packageName);
                return null;
            }
        };
    }

    /**
     * Force stops an application.
     */
    public static Task<Void> createForceStopTask(String packageName) {
        return new Task<>() {
            @Override
            protected Void call() throws Exception {
                ApplicationController.forceStopApplication(packageName);
                return null;
            }
        };
    }

    /**
     * Clears application databases and cache data.
     */
    public static Task<Void> createClearDataTask(String packageName) {
        return new Task<>() {
            @Override
            protected Void call() throws Exception {
                ApplicationController.clearApplicationData(packageName);
                return null;
            }
        };
    }

    /**
     * Extracts an app's APK and downloads it locally.
     */
    public static Task<Void> createExtractApkTask(String packageName, File localTargetDir) {
        return new Task<>() {
            @Override
            protected Void call() throws Exception {
                ApplicationController.extractApk(packageName, localTargetDir.getAbsolutePath());
                return null;
            }
        };
    }

    /**
     * Installs an APK from the host machine to the connected device.
     */
    public static Task<Void> createInstallAppTask(File localApk) {
        return new Task<>() {
            @Override
            protected Void call() throws Exception {
                ApplicationController.installApplication(localApk.getAbsolutePath());
                return null;
            }
        };
    }

    /**
     * Uninstalls a package from the device.
     */
    public static Task<Void> createUninstallAppTask(String packageName) {
        return new Task<>() {
            @Override
            protected Void call() throws Exception {
                ApplicationController.uninstallApplication(packageName);
                return null;
            }
        };
    }
}
