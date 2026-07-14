package com.androidnexus.adb;

import com.androidnexus.exception.AdbException;

/**
 * Detects whether an Android device is connected via ADB.
 *
 * How it works:
 * "adb devices" outputs a header line followed by one line per connected device:
 *
 *   List of devices attached
 *   SERIAL_NUMBER    device
 *
 * The tab character followed by "device" is the reliable indicator.
 * Other states like "unauthorized", "offline", or "no permissions"
 * appear in the same position but with different status words.
 *
 * Note: "unauthorized" means USB debugging is enabled but the RSA key
 * hasn't been accepted on the phone. We treat this as "not connected"
 * because commands will fail.
 */
public class DeviceDetector {

    /**
     * Checks if at least one authorized device is connected.
     *
     * @return true if a device with status "device" is present
     * @throws AdbException if the ADB binary cannot be executed
     */
    public static boolean isDeviceConnected() throws AdbException {

        CommandResult result = CommandExecutor.executeCommand("adb", "devices");

        return result.getOutput().contains("\tdevice");
    }
}