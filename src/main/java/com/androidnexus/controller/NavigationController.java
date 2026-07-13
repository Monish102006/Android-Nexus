package com.androidnexus.controller;

import com.androidnexus.adb.CommandExecutor;

public class NavigationController {
    public static void home() {

        CommandExecutor.executeCommand(
                "adb",
                "shell",
                "input",
                "keyevent",
                "3"
        );

    }
    public static void back() {

        CommandExecutor.executeCommand(
                "adb",
                "shell",
                "input",
                "keyevent",
                "4"
        );

    }

    public static void recentApps() {

        CommandExecutor.executeCommand(
                "adb",
                "shell",
                "input",
                "keyevent",
                "187"
        );

    }

    public static void notifications() {

        CommandExecutor.executeCommand(
                "adb",
                "shell",
                "cmd",
                "statusbar",
                "expand-notifications"
        );

    }

    public static void quickSettings() {

        CommandExecutor.executeCommand(
                "adb",
                "shell",
                "cmd",
                "statusbar",
                "expand-settings"
        );

    }
}
