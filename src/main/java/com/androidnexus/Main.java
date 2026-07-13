package com.androidnexus;

import com.androidnexus.adb.CommandExecutor;
import com.androidnexus.adb.DeviceDetector;
import com.androidnexus.model.Device;
import com.androidnexus.service.DeviceService;
import com.androidnexus.service.ScrcpyService;
import com.androidnexus.controller.DeviceController;
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

        System.out.println("Model         : " + device.getModel());
        System.out.println("Manufacturer  : " + device.getManufacturer());
        System.out.println("Android       : " + device.getAndroidVersion());
        System.out.println("Serial Number : " + device.getSerialNumber());
        System.out.println("Battery      : " + device.getBatteryLevel() + "%");

        ScrcpyService.launch();

        DeviceController.takeScreenshot();

        DeviceController.lock();
    }
}