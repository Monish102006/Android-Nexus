package com.androidnexus.exception;

/**
 * Thrown when an ADB command fails because no Android device is connected
 * or the connected device is not authorized.
 *
 * ADB signals this condition in two ways:
 *   1. "adb devices" returns zero lines after the header.
 *   2. A shell command prints "error: no devices/emulators found" to stderr.
 *
 * This is the single most common failure mode in practice — the USB cable
 * is unplugged, USB debugging is off, or the authorization dialog was dismissed.
 * Giving it a dedicated exception type lets the UI show a helpful reconnect
 * prompt rather than a generic error message.
 */
public class DeviceNotFoundException extends AdbException {

    public DeviceNotFoundException() {
        super("No Android device connected. Check USB connection and USB debugging.");
    }

    public DeviceNotFoundException(String message) {
        super(message);
    }
}
