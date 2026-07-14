package com.androidnexus.ui.service;

import com.androidnexus.controller.DeviceController;
import com.androidnexus.model.Device;
import javafx.concurrent.Task;

/**
 * UI Service managing background task coordination for device details.
 *
 * Adheres strictly to the UI concurrency rule (off-loading ADB and I/O to background workers).
 */
public class DeviceUiService {

    /**
     * Creates a JavaFX Task to fetch device info, storage, and capabilities.
     *
     * @return a Task returning a populated Device model
     */
    public static Task<Device> createFetchDeviceTask() {
        return new Task<>() {
            @Override
            protected Device call() throws Exception {
                // Queries ADB in background thread
                return DeviceController.getDevice();
            }
        };
    }
}
