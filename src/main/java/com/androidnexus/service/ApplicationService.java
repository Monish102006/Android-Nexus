package com.androidnexus.service;

import com.androidnexus.adb.CommandExecutor;
import com.androidnexus.adb.CommandResult;
import com.androidnexus.exception.AdbException;
import com.androidnexus.model.AndroidApplication;
import com.androidnexus.parser.PackageParser;

import java.util.List;

/**
 * Provides operations to manage applications on the connected Android device.
 *
 * This service communicates with the package manager daemon (pm) and activity manager (am)
 * on the device via ADB command execution.
 */
public class ApplicationService {

    /**
     * Lists all installed applications on the device (both system and user apps).
     *
     * @return a list of AndroidApplication objects
     * @throws AdbException if the command fails
     */
    public static List<AndroidApplication> getInstalledApplications() throws AdbException {
        CommandResult result = CommandExecutor.executeCommand(
                "adb", "shell", "pm", "list", "packages", "-f"
        );
        result.requireSuccess();
        return PackageParser.parsePackageList(result.getOutput());
    }

    /**
     * Lists only user-installed (third-party) applications.
     *
     * @return a list of user applications
     * @throws AdbException if the command fails
     */
    public static List<AndroidApplication> getUserApplications() throws AdbException {
        CommandResult result = CommandExecutor.executeCommand(
                "adb", "shell", "pm", "list", "packages", "-f", "-3"
        );
        result.requireSuccess();
        return PackageParser.parsePackageList(result.getOutput());
    }

    /**
     * Retrieves detailed information for a single application package.
     *
     * @param packageName the unique package name of the app (e.g. "com.whatsapp")
     * @return enriched AndroidApplication object
     * @throws AdbException if the package does not exist or querying details fails
     */
    public static AndroidApplication getApplicationDetails(String packageName) throws AdbException {
        if (packageName == null || packageName.isEmpty()) {
            throw new IllegalArgumentException("packageName cannot be null or empty");
        }

        // Find basic package details first to get the APK path
        List<AndroidApplication> allApps = getInstalledApplications();
        AndroidApplication app = null;
        for (AndroidApplication a : allApps) {
            if (a.getPackageName().equals(packageName)) {
                app = a;
                break;
            }
        }

        if (app == null) {
            throw new AdbException("Package not found: " + packageName);
        }

        // 1. Enrich details via dumpsys package
        CommandResult detailsResult = CommandExecutor.executeCommand(
                "adb", "shell", "dumpsys", "package", packageName
        );
        detailsResult.requireSuccess();
        PackageParser.parsePackageDetails(detailsResult.getOutput(), app);

        // 2. Query APK size via stat
        try {
            CommandResult sizeResult = CommandExecutor.executeCommand(
                    "adb", "shell", "stat", "-c", "%s", app.getApkPath()
            );
            if (sizeResult.isSuccess()) {
                app.setApkSize(Long.parseLong(sizeResult.getOutput().trim()));
            }
        } catch (Exception e) {
            // Stat might fail on some customized/locked partitions, ignore size
        }

        return app;
    }

    /**
     * Installs an APK file from the local PC onto the connected device.
     *
     * @param localApkPath absolute path to the .apk file on the local machine
     * @throws AdbException if installation fails
     */
    public static void installApplication(String localApkPath) throws AdbException {
        if (localApkPath == null || localApkPath.isEmpty()) {
            throw new IllegalArgumentException("localApkPath cannot be null or empty");
        }

        // 5-minute timeout for large APKs
        CommandResult result = CommandExecutor.executeCommand(
                300, "adb", "install", "-r", localApkPath
        );
        result.requireSuccess();
    }

    /**
     * Uninstalls an application from the device by its package name.
     *
     * @param packageName the package name of the app to remove
     * @throws AdbException if uninstallation fails
     */
    public static void uninstallApplication(String packageName) throws AdbException {
        if (packageName == null || packageName.isEmpty()) {
            throw new IllegalArgumentException("packageName cannot be null or empty");
        }

        CommandResult result = CommandExecutor.executeCommand(
                60, "adb", "uninstall", packageName
        );
        result.requireSuccess();
    }

    /**
     * Extracts (pulls) the APK file from the device to a local PC folder.
     *
     * @param packageName    package name of the app to extract
     * @param localDirectory absolute path of local folder where the APK should be saved
     * @throws AdbException if extraction fails
     */
    public static void extractApk(String packageName, String localDirectory) throws AdbException {
        if (packageName == null || packageName.isEmpty()) {
            throw new IllegalArgumentException("packageName cannot be null or empty");
        }
        if (localDirectory == null || localDirectory.isEmpty()) {
            throw new IllegalArgumentException("localDirectory cannot be null or empty");
        }

        // 1. Get the path of the APK on the device
        CommandResult pathResult = CommandExecutor.executeCommand(
                "adb", "shell", "pm", "path", packageName
        );
        pathResult.requireSuccess();

        String output = pathResult.getOutput().trim();
        if (output.isEmpty() || !output.startsWith("package:")) {
            throw new AdbException("Failed to find APK path for package: " + packageName);
        }

        String remoteApkPath = output.substring("package:".length()).trim();

        // 2. Construct local destination filename: <localDirectory>/<packageName>.apk
        String separator = localDirectory.endsWith("\\") || localDirectory.endsWith("/") ? "" : "\\";
        String localApkPath = localDirectory + separator + packageName + ".apk";

        // 3. Pull the APK to local machine (5-minute timeout)
        CommandResult pullResult = CommandExecutor.executeCommand(
                300, "adb", "pull", remoteApkPath, localApkPath
        );
        pullResult.requireSuccess();
    }

    /**
     * Launches the application by starting its main launcher activity.
     *
     * Uses monkey package launcher as a portable way to launch main activity.
     *
     * @param packageName package name of the app to launch
     * @throws AdbException if launching fails
     */
    public static void launchApplication(String packageName) throws AdbException {
        if (packageName == null || packageName.isEmpty()) {
            throw new IllegalArgumentException("packageName cannot be null or empty");
        }

        CommandResult result = CommandExecutor.executeCommand(
                "adb", "shell", "monkey", "-p", packageName,
                "-c", "android.intent.category.LAUNCHER", "1"
        );
        result.requireSuccess();
    }

    /**
     * Force-stops all running processes of the specified application.
     *
     * @param packageName package name of the app to stop
     * @throws AdbException if stopping fails
     */
    public static void forceStopApplication(String packageName) throws AdbException {
        if (packageName == null || packageName.isEmpty()) {
            throw new IllegalArgumentException("packageName cannot be null or empty");
        }

        CommandResult result = CommandExecutor.executeCommand(
                "adb", "shell", "am", "force-stop", packageName
        );
        result.requireSuccess();
    }

    /**
     * Clears all app data and cache (equivalent to "Clear Data" in settings).
     *
     * @param packageName package name of the app to clear
     * @throws AdbException if clearing fails
     */
    public static void clearApplicationData(String packageName) throws AdbException {
        if (packageName == null || packageName.isEmpty()) {
            throw new IllegalArgumentException("packageName cannot be null or empty");
        }

        CommandResult result = CommandExecutor.executeCommand(
                "adb", "shell", "pm", "clear", packageName
        );
        result.requireSuccess();
    }
}
