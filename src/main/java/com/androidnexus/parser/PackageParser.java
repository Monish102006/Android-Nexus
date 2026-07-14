package com.androidnexus.parser;

import com.androidnexus.model.AndroidApplication;
import com.androidnexus.model.ApplicationType;

import java.util.ArrayList;
import java.util.List;

public class PackageParser {

    /**
     * Parses the output of "adb shell pm list packages -f" into a list of AndroidApplication objects.
     *
     * Expected format per line:
     *   package:<apkPath>=<packageName>
     * e.g.:
     *   package:/data/app/~~u2g==/com.whatsapp-Ww==/base.apk=com.whatsapp
     */
    public static List<AndroidApplication> parsePackageList(String pmListOutput) {
        List<AndroidApplication> apps = new ArrayList<>();
        if (pmListOutput == null || pmListOutput.isEmpty()) {
            return apps;
        }

        String[] lines = pmListOutput.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || !line.startsWith("package:")) {
                continue;
            }

            try {
                // Remove "package:" prefix
                String content = line.substring("package:".length());
                int lastEquals = content.lastIndexOf('=');
                if (lastEquals == -1) {
                    continue;
                }

                String apkPath = content.substring(0, lastEquals).trim();
                String packageName = content.substring(lastEquals + 1).trim();

                AndroidApplication app = new AndroidApplication();
                app.setPackageName(packageName);
                app.setApkPath(apkPath);
                
                // Fallback app name
                app.setAppName(extractAppNameFallback(packageName));
                
                // Set default states
                app.setEnabled(true);
                app.setType(apkPath.startsWith("/system/") ? ApplicationType.SYSTEM : ApplicationType.USER);

                apps.add(app);
            } catch (Exception e) {
                // Skip malformed lines
            }
        }
        return apps;
    }

    /**
     * Enriches an existing AndroidApplication object by parsing "adb shell dumpsys package <packageName>" output.
     *
     * Expected patterns in dumpsys:
     *   versionCode=24057803 minSdk=24 targetSdk=34
     *   versionName=2.24.5.78
     *   installerPackageName=com.android.vending
     */
    public static AndroidApplication parsePackageDetails(String dumpsysOutput, AndroidApplication app) {
        if (dumpsysOutput == null || dumpsysOutput.isEmpty() || app == null) {
            return app;
        }

        String[] lines = dumpsysOutput.split("\n");
        for (String line : lines) {
            line = line.trim();

            // 1. Parse versionName
            if (line.startsWith("versionName=")) {
                app.setVersionName(line.substring("versionName=".length()).trim());
            }
            // 2. Parse versionCode
            else if (line.contains("versionCode=")) {
                // e.g. "versionCode=24057803 targetSdk=34" or "versionCode=123 minSdk=21..."
                // Let's parse versionCode value safely
                String code = extractValue(line, "versionCode=");
                if (!code.isEmpty()) {
                    try {
                        // Extract digits only as versionCode can have details inside like "versionCode=123 minSdk=21"
                        int firstSpace = code.indexOf(' ');
                        String digits = firstSpace == -1 ? code : code.substring(0, firstSpace);
                        app.setVersionCode(Integer.parseInt(digits.trim()));
                    } catch (NumberFormatException e) {
                        // Ignore
                    }
                }
            }
            // 3. Parse minSdk
            if (line.contains("minSdk=")) {
                app.setMinSdk(extractValue(line, "minSdk="));
            }
            // 4. Parse targetSdk
            if (line.contains("targetSdk=")) {
                app.setTargetSdk(extractValue(line, "targetSdk="));
            }
            // 5. Parse installerPackageName
            if (line.startsWith("installerPackageName=")) {
                String installer = line.substring("installerPackageName=".length()).trim();
                if (installer.equals("null") || installer.isEmpty()) {
                    app.setInstaller("Sideloaded/Direct");
                } else {
                    app.setInstaller(installer);
                }
            }
        }
        return app;
    }

    private static String extractValue(String line, String key) {
        int index = line.indexOf(key);
        if (index == -1) {
            return "";
        }
        String sub = line.substring(index + key.length()).trim();
        int space = sub.indexOf(' ');
        return space == -1 ? sub : sub.substring(0, space);
    }

    private static String extractAppNameFallback(String packageName) {
        int lastDot = packageName.lastIndexOf('.');
        if (lastDot == -1 || lastDot == packageName.length() - 1) {
            return packageName;
        }
        String lastPart = packageName.substring(lastDot + 1);
        if (lastPart.length() > 0) {
            // Capitalize first letter
            return Character.toUpperCase(lastPart.charAt(0)) + lastPart.substring(1);
        }
        return packageName;
    }
}
