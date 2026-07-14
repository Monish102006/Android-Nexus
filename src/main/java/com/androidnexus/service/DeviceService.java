package com.androidnexus.service;

import com.androidnexus.adb.CommandExecutor;
import com.androidnexus.adb.CommandResult;
import com.androidnexus.exception.AdbException;
import com.androidnexus.model.Device;
import com.androidnexus.parser.BatteryParser;

/**
 * Retrieves device information by querying Android system properties via ADB.
 *
 * How Android system properties work:
 * -----------------------------------
 * Android stores system configuration in a key-value property store accessible
 * via {@code getprop}. Properties follow a naming convention:
 *
 *   ro.product.model        → Device model name (e.g. "Pixel 7")
 *   ro.product.manufacturer → Manufacturer name (e.g. "Google")
 *   ro.build.version.release → Android version (e.g. "14")
 *
 * "ro." prefix means "read-only" — set at build time, never changes at runtime.
 *
 * Serial number comes from {@code adb get-serialno}, not getprop, because it's
 * an ADB transport property, not an Android system property.
 *
 * Battery level comes from {@code dumpsys battery}, which queries the
 * BatteryService system service. Parsing is delegated to
 * {@link BatteryParser#parseBatteryLevel(String)}.
 */
public class DeviceService {

    /**
     * Queries the connected device for its hardware and software information.
     *
     * Makes 5 sequential ADB calls:
     *   1. getprop ro.product.model
     *   2. getprop ro.product.manufacturer
     *   3. getprop ro.build.version.release
     *   4. adb get-serialno
     *   5. dumpsys battery → parsed by BatteryParser
     *
     * @return a populated Device model
     * @throws AdbException if any ADB command fails
     */
    public static Device getDeviceInformation() throws AdbException {

        Device device = new Device();

        // ── Model ───────────────────────────────────────────────────────
        CommandResult modelResult = CommandExecutor.executeCommand(
                "adb", "shell", "getprop", "ro.product.model"
        );
        device.setModel(modelResult.getOutput().trim());

        // ── Manufacturer ────────────────────────────────────────────────
        CommandResult manufacturerResult = CommandExecutor.executeCommand(
                "adb", "shell", "getprop", "ro.product.manufacturer"
        );
        device.setManufacturer(manufacturerResult.getOutput().trim());

        // ── Android Version ─────────────────────────────────────────────
        CommandResult versionResult = CommandExecutor.executeCommand(
                "adb", "shell", "getprop", "ro.build.version.release"
        );
        device.setAndroidVersion(versionResult.getOutput().trim());

        // ── Serial Number ───────────────────────────────────────────────
        CommandResult serialResult = CommandExecutor.executeCommand(
                "adb", "get-serialno"
        );
        device.setSerialNumber(serialResult.getOutput().trim());

        // ── Battery Level ───────────────────────────────────────────────
        // Parsing delegated to BatteryParser (Rule 5: never parse in services)
        CommandResult batteryResult = CommandExecutor.executeCommand(
                "adb", "shell", "dumpsys", "battery"
        );
        int batteryLevel = BatteryParser.parseBatteryLevel(
                batteryResult.getOutput()
        );
        device.setBatteryLevel(batteryLevel);

        // ── Device Capabilities ─────────────────────────────────────────
        device.setCapabilities(detectCapabilities());

        return device;
    }

    private static com.androidnexus.model.DeviceCapabilities detectCapabilities() {
        com.androidnexus.model.DeviceCapabilities caps = new com.androidnexus.model.DeviceCapabilities();

        // 1. Notification access
        try {
            CommandResult result = CommandExecutor.executeCommand(
                    "adb", "shell", "dumpsys", "notification", "--short"
            );
            caps.setSupportsNotificationAccess(result.isSuccess() && !result.getOutput().contains("Permission Denial"));
        } catch (Exception e) {
            caps.setSupportsNotificationAccess(false);
        }

        // 2. Screen recording
        try {
            CommandResult result = CommandExecutor.executeCommand(
                    "adb", "shell", "which", "screenrecord"
            );
            caps.setSupportsRecording(result.isSuccess() && !result.getOutput().trim().isEmpty());
        } catch (Exception e) {
            caps.setSupportsRecording(false);
        }

        // 3. Flashlight (check for camera flash / flashlight commands or services)
        try {
            CommandResult result = CommandExecutor.executeCommand(
                    "adb", "shell", "cmd", "package", "list", "services"
            );
            caps.setSupportsFlashlight(result.isSuccess() && result.getOutput().contains("flashlight"));
        } catch (Exception e) {
            caps.setSupportsFlashlight(false);
        }

        // 4. Media control
        try {
            CommandResult result = CommandExecutor.executeCommand(
                    "adb", "shell", "cmd", "package", "list", "services"
            );
            caps.setSupportsMediaControl(result.isSuccess() && result.getOutput().contains("media_session"));
        } catch (Exception e) {
            caps.setSupportsMediaControl(false);
        }

        return caps;
    }
}