package com.androidnexus.controller;

import com.androidnexus.adb.CommandExecutor;
import com.androidnexus.utils.Constants;
import com.androidnexus.utils.FileNameGenerator;

public class ScreenshotController {
    public static void takeScreenshot() {

        String fileName = FileNameGenerator.generateScreenshotName();

        String phonePath =
                Constants.PHONE_STORAGE_DIRECTORY + fileName;

        CommandExecutor.executeCommand(
                "adb",
                "shell",
                "screencap",
                "-p",
                phonePath
        );

        CommandExecutor.executeCommand(
                "adb",
                "pull",
                phonePath,
                Constants.LOCAL_SCREENSHOT_FOLDER + "\\" + fileName
        );

        CommandExecutor.executeCommand(
                "adb",
                "shell",
                "rm",
                phonePath
        );
    }
}
