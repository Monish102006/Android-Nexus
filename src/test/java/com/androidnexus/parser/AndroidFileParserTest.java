package com.androidnexus.parser;

import com.androidnexus.model.AndroidFile;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class AndroidFileParserTest {

    @Test
    public void testParseDirectoryListing() {
        String mockOutput = "total 16\n" +
                "-rw-rw---- 1 root sdcard_rw 1048576 2024-01-15 10:30 document.pdf\n" +
                "drwxrwx--x 2 root sdcard_rw     4096 2024-01-15 10:31 Photos\n" +
                "lrwxrwxrwx 1 root root            21 2024-01-15 10:32 sdcard -> /storage/emulated/0\n";

        List<AndroidFile> files = AndroidFileParser.parse(mockOutput, "/sdcard");

        assertEquals(3, files.size());

        // Test File
        AndroidFile file = files.get(0);
        assertEquals("document.pdf", file.getName());
        assertEquals("/sdcard/document.pdf", file.getPath());
        assertFalse(file.isDirectory());
        assertFalse(file.isSymlink());
        assertEquals(1048576, file.getSize());
        assertEquals("1.0 MB", file.getHumanReadableSize());
        assertEquals("-rw-rw----", file.getPermissions());
        assertEquals("root", file.getOwner());
        assertEquals("sdcard_rw", file.getGroup());
        assertEquals("2024-01-15 10:30", file.getLastModified());
        assertEquals("pdf", file.getExtension());

        // Test Directory
        AndroidFile dir = files.get(1);
        assertEquals("Photos", dir.getName());
        assertEquals("/sdcard/Photos", dir.getPath());
        assertTrue(dir.isDirectory());
        assertFalse(dir.isSymlink());
        assertEquals(4096, dir.getSize());
        assertEquals("4.0 KB", dir.getHumanReadableSize());
        assertEquals("drwxrwx--x", dir.getPermissions());
        assertEquals("", dir.getExtension());

        // Test Symlink
        AndroidFile link = files.get(2);
        assertEquals("sdcard", link.getName());
        assertEquals("/sdcard/sdcard", link.getPath());
        assertFalse(link.isDirectory());
        assertTrue(link.isSymlink());
        assertEquals("/storage/emulated/0", link.getSymlinkTarget());
    }

    @Test
    public void testParseFilenameWithSpaces() {
        String mockOutput = "-rw-rw---- 1 root sdcard_rw 1024 2024-01-15 10:30 My Document File.pdf\n";
        List<AndroidFile> files = AndroidFileParser.parse(mockOutput, "/sdcard");

        assertEquals(1, files.size());
        AndroidFile file = files.get(0);
        assertEquals("My Document File.pdf", file.getName());
        assertEquals("/sdcard/My Document File.pdf", file.getPath());
        assertEquals("pdf", file.getExtension());
    }

    @Test
    public void testParseAbsolutePathOutput() {
        // Output from ls -ld /sdcard/Download/test.txt
        String mockOutput = "-rw-rw---- 1 root sdcard_rw 1048576 2024-01-15 10:30 /sdcard/Download/test.txt\n";
        List<AndroidFile> files = AndroidFileParser.parse(mockOutput, "/sdcard/Download");

        assertEquals(1, files.size());
        AndroidFile file = files.get(0);
        assertEquals("test.txt", file.getName());
        assertEquals("/sdcard/Download/test.txt", file.getPath());
    }

    @Test
    public void testParseAbsoluteSymlinkOutput() {
        // Output from ls -ld /sdcard/Download/link
        String mockOutput = "lrwxrwxrwx 1 root root 21 2024-01-15 10:30 /sdcard/Download/link -> /sdcard/target\n";
        List<AndroidFile> files = AndroidFileParser.parse(mockOutput, "/sdcard/Download");

        assertEquals(1, files.size());
        AndroidFile file = files.get(0);
        assertEquals("link", file.getName());
        assertEquals("/sdcard/Download/link", file.getPath());
        assertTrue(file.isSymlink());
        assertEquals("/sdcard/target", file.getSymlinkTarget());
    }

    @Test
    public void testParseMalformedLines() {
        String mockOutput = "total 16\n" +
                "some random error warning line\n" +
                "-rw-rw---- 1 root sdcard_rw 1024 2024-01-15 10:30 document.pdf\n" +
                "another malformed line with not enough parts\n";

        List<AndroidFile> files = AndroidFileParser.parse(mockOutput, "/sdcard");
        assertEquals(1, files.size());
        assertEquals("document.pdf", files.get(0).getName());
    }
}
