package com.androidnexus;

import com.androidnexus.adb.CommandExecutor;
import com.androidnexus.adb.DeviceDetector;
import com.androidnexus.model.Device;
import com.androidnexus.service.DeviceService;
import com.androidnexus.service.ScrcpyService;
import com.androidnexus.controller.RecordingController;
import com.androidnexus.controller.DeviceController;
import com.androidnexus.utils.FileNameGenerator;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {

        System.out.println("=================================");
        System.out.println("       Android Nexus");
        System.out.println("=================================\n");

        System.out.println("Checking ADB...\n");

        boolean connected = DeviceDetector.isDeviceConnected();

        if (connected) {
            System.out.println("🟢 Device Connected");
        } else {
            System.out.println("🔴 No Device Connected");
        }
        Device device = DeviceService.getDeviceInformation();

//        System.out.println("Model         : " + device.getModel());
//        System.out.println("Manufacturer  : " + device.getManufacturer());
//        System.out.println("Android       : " + device.getAndroidVersion());
//        System.out.println("Serial Number : " + device.getSerialNumber());
//        System.out.println("Battery      : " + device.getBatteryLevel() + "%");

//        ScrcpyService.launch();

//        DeviceController.lock();
//        DeviceController.recentApps();
//        DeviceController.home();
//        DeviceController.notifications();

//        RecordingController.startRecording();
//        try {
//            Thread.sleep(5000); // Pauses execution for 5 seconds
//        } catch (InterruptedException e) {
//            e.printStackTrace(); // Handles the interruption signal
//        }
//
//        RecordingController.stopRecording();

//        DeviceController.takeScreenshot();
//        System.out.println(FileNameGenerator.generateScreenshotName());

        DeviceController.volumeUp();
//        DeviceController.volumeDown();
//        DeviceController.mute();
    }
}