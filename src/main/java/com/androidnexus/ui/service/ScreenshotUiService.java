package com.androidnexus.ui.service;

import com.androidnexus.controller.DeviceController;
import com.androidnexus.utils.Constants;
import javafx.concurrent.Task;

import java.io.File;

/**
 * UI Service managing background task coordination for screenshot, mirroring,
 * and remote control actions.
 */
public class ScreenshotUiService {

    /**
     * Captures a screenshot via ADB and returns the newly generated local File.
     */
    public static Task<File> createCaptureScreenshotTask() {
        return new Task<>() {
            @Override
            protected File call() throws Exception {
                // Ensure target folder exists
                File folder = new File(com.androidnexus.ui.controller.SettingsController.getScreenshotsFolder());
                if (!folder.exists()) {
                    folder.mkdirs();
                }

                // 1. Capture screenshot on device
                DeviceController.takeScreenshot();

                // 2. Locate the most recently modified file in the screenshots folder
                File[] files = folder.listFiles();
                if (files == null || files.length == 0) {
                    throw new Exception("Screenshot file was not saved locally.");
                }

                File latest = files[0];
                for (File f : files) {
                    if (f.lastModified() > latest.lastModified()) {
                        latest = f;
                    }
                }
                return latest;
            }
        };
    }

    /**
     * Asynchronously launches scrcpy mirroring process.
     */
    public static Task<Boolean> createLaunchMirrorTask() {
        return new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                ProcessBuilder pb;
                File localScrcpy = new File(Constants.SCRCPY_PATH);
                
                if (localScrcpy.exists()) {
                    pb = new ProcessBuilder(localScrcpy.getAbsolutePath());
                } else {
                    // Fallback to path resolution
                    pb = new ProcessBuilder("scrcpy");
                }

                try {
                    pb.start();
                    return true;
                } catch (Exception e) {
                    throw new Exception("scrcpy executable not found on PATH or Constants.SCRCPY_PATH.\n" +
                            "Please install scrcpy to enable live mirroring.");
                }
            }
        };
    }

    public static Task<Void> createVolumeUpTask() {
        return new Task<>() {
            @Override
            protected Void call() throws Exception {
                DeviceController.volumeUp();
                return null;
            }
        };
    }

    public static Task<Void> createVolumeDownTask() {
        return new Task<>() {
            @Override
            protected Void call() throws Exception {
                DeviceController.volumeDown();
                return null;
            }
        };
    }

    public static Task<Void> createMuteTask() {
        return new Task<>() {
            @Override
            protected Void call() throws Exception {
                DeviceController.mute();
                return null;
            }
        };
    }

    public static Task<Void> createPowerTask() {
        return new Task<>() {
            @Override
            protected Void call() throws Exception {
                DeviceController.lock();
                return null;
            }
        };
    }
}
