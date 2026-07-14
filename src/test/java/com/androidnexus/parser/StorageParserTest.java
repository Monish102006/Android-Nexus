package com.androidnexus.parser;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StorageParserTest {

    @Test
    public void testParseStorageWithHeader() {
        String mockDf = "Filesystem     1K-blocks     Used Available Use% Mounted on\n" +
                "/dev/block/sda  11565432  8123456   3441976  71% /data\n";

        String[] parsed = StorageParser.parseStorage(mockDf);

        assertEquals(2, parsed.length);
        // Total size: 11565432 KB = ~11.0 GB
        assertEquals("11.0 GB", parsed[0]);
        // Available size: 3441976 KB = ~3.3 GB
        assertEquals("3.3 GB", parsed[1]);
    }

    @Test
    public void testParseStorageHumanReadable() {
        String mockDf = "Filesystem      Size  Used Avail Use% Mounted on\n" +
                "/dev/block/sda  120G   78G   42G  65% /data\n";

        String[] parsed = StorageParser.parseStorage(mockDf);

        assertEquals(2, parsed.length);
        assertEquals("120GB", parsed[0]);
        assertEquals("42GB", parsed[1]);
    }

    @Test
    public void testParseStorageEmpty() {
        String[] parsed = StorageParser.parseStorage("");
        assertEquals("Unknown", parsed[0]);
        assertEquals("Unknown", parsed[1]);
    }

    @Test
    public void testParseStorageNull() {
        String[] parsed = StorageParser.parseStorage(null);
        assertEquals("Unknown", parsed[0]);
        assertEquals("Unknown", parsed[1]);
    }
}
