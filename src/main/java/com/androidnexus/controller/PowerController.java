package com.androidnexus.controller;

import com.androidnexus.adb.CommandExecutor;

public class PowerController {

    public static void lock() {

        CommandExecutor.executeCommand(
                "adb",
                "shell",
                "input",
                "keyevent",
                "26"
        );

    }

}