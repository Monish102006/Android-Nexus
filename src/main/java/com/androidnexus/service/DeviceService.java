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


        return device;
    }

}