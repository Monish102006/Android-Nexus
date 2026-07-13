package com.androidnexus.controller;

import com.androidnexus.adb.CommandExecutor;
import com.androidnexus.utils.Constants;

public class RecordingController {

    private static Process recordingProcess;

    public static void startRecording() {

        recordingProcess = CommandExecutor.executeProcess(
                "adb",
                "shell",
                "screenrecord",
                "/sdcard/demo.mp4"
        );

    }

    public static void stopRecording() {

        if (recordingProcess != null) {

            recordingProcess.destroy();

            recordingProcess = null;

            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            CommandExecutor.executeCommand(
                    "adb",
                    "pull",
                    Constants.PHONE_RECORDING_PATH,
                    Constants.LOCAL_RECORDING_FOLDER
            );

            CommandExecutor.executeCommand(
                    "adb",
                    "shell",
                    "rm",
                    Constants.PHONE_RECORDING_PATH
            );
        }

    }

}