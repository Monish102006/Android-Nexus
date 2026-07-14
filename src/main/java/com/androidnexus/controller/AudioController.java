package com.androidnexus.controller;

import com.androidnexus.adb.CommandExecutor;

public class AudioController {

    public static void volumeUp() {

        try {

            ProcessBuilder pb = new ProcessBuilder(
                    "adb",
                    "shell",
                    "input",
                    "keyevent",
                    "3"
            );

            Process p = pb.start();

            int exitCode = p.waitFor();

            System.out.println("Exit Code = " + exitCode);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void volumeDown() {

        CommandExecutor.executeCommand(
                "adb",
                "shell",
                "input",
                "keyevent",
                "25"
        );

    }

    public static void mute() {

        CommandExecutor.executeCommand(
                "adb",
                "shell",
                "input",
                "keyevent",
                "164"
        );

    }

}