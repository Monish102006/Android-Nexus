package com.androidnexus.parser;

/**
 * Parses the output of "adb shell df /data" or "adb shell df -h /data"
 * to extract device storage specifications.
 */
public class StorageParser {

    /**
     * Parses the output of "df" command.
     *
     * Sample output:
     * Filesystem     1K-blocks     Used Available Use% Mounted on
     * /dev/block/sda  11565432  8123456   3441976  71% /data
     *
     * Returns: String[2] where [0] = total size, [1] = available size.
     */
    public static String[] parseStorage(String dfOutput) {
        String[] result = new String[]{"Unknown", "Unknown"};
        if (dfOutput == null || dfOutput.isEmpty()) {
            return result;
        }

        String[] lines = dfOutput.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("Filesystem")) {
                continue;
            }

            String[] parts = line.split("\\s+");
            if (parts.length >= 4) {
                // If it is in 1K-blocks (numeric only), let's format it.
                // If it already has letters (G, M, K from -h flag), use it directly.
                result[0] = formatStorageSize(parts[1]);
                result[1] = formatStorageSize(parts[3]);
                break;
            }
        }
        return result;
    }

    private static String formatStorageSize(String raw) {
        if (raw == null || raw.isEmpty()) {
            return "Unknown";
        }

        // If it ends with G, M, K, etc. (already human readable)
        char lastChar = raw.charAt(raw.length() - 1);
        if (Character.isLetter(lastChar)) {
            return raw + "B"; // Convert e.g., 120G to 120GB
        }

        try {
            long kb = Long.parseLong(raw);
            double gb = kb / (1024.0 * 1024.0);
            if (gb >= 1) {
                return String.format("%.1f GB", gb);
            }
            double mb = kb / 1024.0;
            return String.format("%.1f MB", mb);
        } catch (NumberFormatException e) {
            return raw;
        }
    }
}
