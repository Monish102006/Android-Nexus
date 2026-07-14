package com.androidnexus.parser;

/**
 * Parses the output of {@code adb shell dumpsys battery}.
 *
 * Sample output from Android:
 * <pre>
 *   Current Battery Service state:
 *     AC powered: false
 *     USB powered: true
 *     Wireless powered: false
 *     Max charging voltage: 5000000
 *     Max charging current: 500000
 *     status: 2
 *     health: 2
 *     present: true
 *     level: 85
 *     scale: 100
 *     voltage: 4200
 *     temperature: 250
 *     technology: Li-ion
 * </pre>
 *
 * Why a dedicated parser?
 * ----------------------
 * This parsing was previously inline in DeviceService.getDeviceInformation(),
 * violating Rule 5: "Never parse inside Services."
 *
 * Extracting it into a dedicated parser class gives us:
 *   1. Single Responsibility: DeviceService orchestrates, BatteryParser parses
 *   2. Testability: We can unit test parsing with sample strings, no device needed
 *   3. Reusability: Other services can parse battery data without duplicating code
 */
public class BatteryParser {

    /**
     * Extracts the battery level percentage from dumpsys battery output.
     *
     * @param dumpsysOutput the raw output of "adb shell dumpsys battery"
     * @return the battery level as an integer (0-100)
     * @throws IllegalArgumentException if the "level:" field is not found
     *                                  or cannot be parsed as an integer
     */
    public static int parseBatteryLevel(String dumpsysOutput) {

        if (dumpsysOutput == null || dumpsysOutput.isEmpty()) {
            throw new IllegalArgumentException(
                    "Battery dumpsys output is null or empty"
            );
        }

        String[] lines = dumpsysOutput.split("\n");

        for (String line : lines) {

            String trimmed = line.trim();

            if (trimmed.startsWith("level:")) {

                String[] parts = trimmed.split(":");

                if (parts.length < 2) {
                    throw new IllegalArgumentException(
                            "Malformed battery level line: " + trimmed
                    );
                }

                try {
                    return Integer.parseInt(parts[1].trim());
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException(
                            "Cannot parse battery level as integer: " + parts[1].trim(), e
                    );
                }
            }
        }

        throw new IllegalArgumentException(
                "Battery level not found in dumpsys output"
        );
    }
}
