package com.androidnexus.model;

/**
 * Represents a connected Android device and its system information.
 *
 * This is a data model (POJO) populated by {@link com.androidnexus.service.DeviceService}.
 * All fields are retrieved from Android system properties and system services:
 *
 *   model          ← ro.product.model         (e.g. "Pixel 7", "SM-S918B")
 *   manufacturer   ← ro.product.manufacturer  (e.g. "Google", "samsung")
 *   androidVersion ← ro.build.version.release  (e.g. "14", "13")
 *   serialNumber   ← adb get-serialno         (e.g. "28161JEGR07832")
 *   batteryLevel   ← dumpsys battery → level   (0-100)
 *
 * Future expansion (Module 2 enhancement):
 *   - Storage total / available
 *   - Screen resolution
 *   - API level (ro.build.version.sdk)
 *   - Build number (ro.build.display.id)
 *   - Security patch (ro.build.version.security_patch)
 */
public class Device {

    private String model;
    private String manufacturer;
    private String androidVersion;
    private String serialNumber;
    private int batteryLevel;

    private DeviceCapabilities capabilities;

    private String apiLevel;
    private String screenResolution;
    private String storageTotal;
    private String storageAvailable;

    public Device() {
    }

    public DeviceCapabilities getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(DeviceCapabilities capabilities) {
        this.capabilities = capabilities;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getAndroidVersion() {
        return androidVersion;
    }

    public void setAndroidVersion(String androidVersion) {
        this.androidVersion = androidVersion;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public int getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(int batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public String getApiLevel() {
        return apiLevel;
    }

    public void setApiLevel(String apiLevel) {
        this.apiLevel = apiLevel;
    }

    public String getScreenResolution() {
        return screenResolution;
    }

    public void setScreenResolution(String screenResolution) {
        this.screenResolution = screenResolution;
    }

    public String getStorageTotal() {
        return storageTotal;
    }

    public void setStorageTotal(String storageTotal) {
        this.storageTotal = storageTotal;
    }

    public String getStorageAvailable() {
        return storageAvailable;
    }

    public void setStorageAvailable(String storageAvailable) {
        this.storageAvailable = storageAvailable;
    }

    @Override
    public String toString() {
        return "Device{" +
                "model='" + model + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", androidVersion='" + androidVersion + '\'' +
                ", serialNumber='" + serialNumber + '\'' +
                ", batteryLevel=" + batteryLevel +
                ", capabilities=" + capabilities +
                ", apiLevel='" + apiLevel + '\'' +
                ", screenResolution='" + screenResolution + '\'' +
                ", storageTotal='" + storageTotal + '\'' +
                ", storageAvailable='" + storageAvailable + '\'' +
                '}';
    }
}