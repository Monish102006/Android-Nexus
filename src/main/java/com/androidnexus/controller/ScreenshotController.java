package com.androidnexus.controller;

import com.androidnexus.adb.CommandExecutor;
import com.androidnexus.utils.Constants;

public class ScreenshotController {
    public static void takeScreenshot() {
        CommandExecutor.executeCommand(
                "adb",
                "shell",
                "screencap",
                "-p",
                Constants.PHONE_SCREENSHOT_PATH
        );

        CommandExecutor.executeCommand(
                "adb",
                "pull",
                Constants.PHONE_SCREENSHOT_PATH,
                Constants.LOCAL_SCREENSHOT_FOLDER
        );

        CommandExecutor.executeCommand(
                "adb",
                "shell",
                "rm",
                Constants.PHONE_SCREENSHOT_PATH
        );
    }
}
