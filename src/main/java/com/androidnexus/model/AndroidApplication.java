package com.androidnexus.model;

/**
 * Represents an Android application installed on the connected device.
 *
 * This data model holds metadata queried from the Android Package Manager (pm)
 * and detailed dump diagnostics (dumpsys package).
 */
public class AndroidApplication {

    private String packageName;
    private String appName;
    private String versionName;
    private int versionCode;
    private String apkPath;
    private long apkSize;
    private ApplicationType type;
    private boolean enabled;
    private String installer;
    private String minSdk;
    private String targetSdk;

    public AndroidApplication() {
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getApkPath() {
        return apkPath;
    }

    public void setApkPath(String apkPath) {
        this.apkPath = apkPath;
    }

    public long getApkSize() {
        return apkSize;
    }

    public void setApkSize(long apkSize) {
        this.apkSize = apkSize;
    }

    public ApplicationType getType() {
        return type;
    }

    public void setType(ApplicationType type) {
        this.type = type;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getInstaller() {
        return installer;
    }

    public void setInstaller(String installer) {
        this.installer = installer;
    }

    public String getMinSdk() {
        return minSdk;
    }

    public void setMinSdk(String minSdk) {
        this.minSdk = minSdk;
    }

    public String getTargetSdk() {
        return targetSdk;
    }

    public void setTargetSdk(String targetSdk) {
        this.targetSdk = targetSdk;
    }

    @Override
    public String toString() {
        return "AndroidApplication{" +
                "packageName='" + packageName + '\'' +
                ", appName='" + appName + '\'' +
                ", versionName='" + versionName + '\'' +
                ", versionCode=" + versionCode +
                ", apkPath='" + apkPath + '\'' +
                ", apkSize=" + apkSize +
                ", type=" + type +
                ", enabled=" + enabled +
                ", installer='" + installer + '\'' +
                ", minSdk='" + minSdk + '\'' +
                ", targetSdk='" + targetSdk + '\'' +
                '}';
    }
}
