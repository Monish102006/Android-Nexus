package com.androidnexus.parser;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BatteryParserTest {

    @Test
    public void testParseBatteryLevelSuccess() {
        String mockOutput = "Current Battery Service state:\n" +
                "  AC powered: false\n" +
                "  USB powered: true\n" +
                "  Wireless powered: false\n" +
                "  Max charging voltage: 5000000\n" +
                "  Max charging current: 500000\n" +
                "  status: 2\n" +
                "  health: 2\n" +
                "  present: true\n" +
                "  level: 85\n" +
                "  scale: 100\n" +
                "  voltage: 4200\n" +
                "  temperature: 250\n" +
                "  technology: Li-ion\n";

        int level = BatteryParser.parseBatteryLevel(mockOutput);
        assertEquals(85, level);
    }

    @Test
    public void testParseBatteryLevelZero() {
        String mockOutput = "level: 0\n";
        int level = BatteryParser.parseBatteryLevel(mockOutput);
        assertEquals(0, level);
    }

    @Test
    public void testParseBatteryLevelHundred() {
        String mockOutput = "level: 100\n";
        int level = BatteryParser.parseBatteryLevel(mockOutput);
        assertEquals(100, level);
    }

    @Test
    public void testParseBatteryLevelMissingLevel() {
        String mockOutput = "Current Battery Service state:\n" +
                "  AC powered: false\n" +
                "  USB powered: true\n";

        assertThrows(IllegalArgumentException.class, () -> {
            BatteryParser.parseBatteryLevel(mockOutput);
        });
    }

    @Test
    public void testParseBatteryLevelMalformedLevel() {
        String mockOutput = "level: abc\n";
        assertThrows(IllegalArgumentException.class, () -> {
            BatteryParser.parseBatteryLevel(mockOutput);
        });
    }

    @Test
    public void testParseBatteryLevelEmptyOutput() {
        assertThrows(IllegalArgumentException.class, () -> {
            BatteryParser.parseBatteryLevel("");
        });
    }

    @Test
    public void testParseBatteryLevelNullOutput() {
        assertThrows(IllegalArgumentException.class, () -> {
            BatteryParser.parseBatteryLevel(null);
        });
    }
}
