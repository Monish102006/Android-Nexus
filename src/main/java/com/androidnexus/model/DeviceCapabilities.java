package com.androidnexus.model;

/**
 * Represents the detected hardware and software capabilities of the connected Android device.
 *
 * This helps the application dynamically enable/disable UI elements and handle
 * different OEM limitations gracefully.
 */
public class DeviceCapabilities {

    private boolean supportsNotificationAccess;
    private boolean supportsFlashlight;
    private boolean supportsRecording;
    private boolean supportsMediaControl;

    public DeviceCapabilities() {
    }

    public boolean supportsNotificationAccess() {
        return supportsNotificationAccess;
    }

    public void setSupportsNotificationAccess(boolean supportsNotificationAccess) {
        this.supportsNotificationAccess = supportsNotificationAccess;
    }

    public boolean supportsFlashlight() {
        return supportsFlashlight;
    }

    public void setSupportsFlashlight(boolean supportsFlashlight) {
        this.supportsFlashlight = supportsFlashlight;
    }

    public boolean supportsRecording() {
        return supportsRecording;
    }

    public void setSupportsRecording(boolean supportsRecording) {
        this.supportsRecording = supportsRecording;
    }

    public boolean supportsMediaControl() {
        return supportsMediaControl;
    }

    public void setSupportsMediaControl(boolean supportsMediaControl) {
        this.supportsMediaControl = supportsMediaControl;
    }

    @Override
    public String toString() {
        return "DeviceCapabilities{" +
                "supportsNotificationAccess=" + supportsNotificationAccess +
                ", supportsFlashlight=" + supportsFlashlight +
                ", supportsRecording=" + supportsRecording +
                ", supportsMediaControl=" + supportsMediaControl +
                '}';
    }
}
