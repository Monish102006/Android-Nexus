package com.androidnexus.adb;

public class DeviceDetector {

    public static boolean isDeviceConnected() {

        String output = CommandExecutor.executeCommand("adb", "devices");
        return output.contains("\tdevice");
    }

}