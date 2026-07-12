package com.androidnexus.service;

import com.androidnexus.adb.CommandExecutor;
import com.androidnexus.model.Device;

public class DeviceService {

    public static Device getDeviceInformation() {

        Device device = new Device();
        String model = CommandExecutor.executeCommand(
                "adb",
                "shell",
                "getprop",
                "ro.product.model"
        );

        device.setModel(model.trim());

        String manufacturer = CommandExecutor.executeCommand(
                "adb",
                "shell",
                "getprop",
                "ro.product.manufacturer"
        );

        device.setManufacturer(manufacturer.trim());

        String androidVersion = CommandExecutor.executeCommand(
                "adb",
                "shell",
                "getprop",
                "ro.build.version.release"
        );

        device.setAndroidVersion(androidVersion.trim());

        String serialNumber = CommandExecutor.executeCommand(
                "adb",
                "get-serialno"
        );

        device.setSerialNumber(serialNumber.trim());

        String batteryInfo = CommandExecutor.executeCommand(
                "adb",
                "shell",
                "dumpsys",
                "battery"
        );

        String[] lines = batteryInfo.split("\n");

        for (String line : lines) {

            line = line.trim();

            if (line.startsWith("level:")) {

                String[] parts = line.split(":");

                int batteryLevel = Integer.parseInt(parts[1].trim());

                device.setBatteryLevel(batteryLevel);

                break;
            }
        }


        return device;
    }

}