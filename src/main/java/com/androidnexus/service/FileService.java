package com.androidnexus.service;

import com.androidnexus.adb.CommandExecutor;
import com.androidnexus.adb.CommandResult;
import com.androidnexus.exception.AdbException;
import com.androidnexus.model.AndroidFile;
import com.androidnexus.parser.AndroidFileParser;

import java.util.List;

/**
 * Provides file system operations on the connected Android device.
 *
 * All operations use ADB shell commands to interact with the device file system.
 * This service is the single point of contact with the device file system —
 * FileController delegates here, and this class delegates parsing to
 * {@link AndroidFileParser}.
 *
 * Architecture flow:
 *   FileController → FileService → CommandExecutor → ADB → Android
 *                                → AndroidFileParser (for parsing output)
 *
 * Important: Android uses a Linux kernel, so the file system is Unix-style:
 *   - Paths use forward slashes: /sdcard/Download/
 *   - File permissions are Unix rwxrwxrwx
 *   - Most user-accessible storage is under /sdcard/ (symlink to /storage/emulated/0/)
 */
public class FileService {

    /**
     * Lists files and directories at the given path on the device.
     *
     * Uses {@code adb shell ls -l} to get detailed file information including
     * permissions, size, owner, group, and modification date.
     *
     * Previous bug fixed:
     * The command was running plain "ls" without "-l", but AndroidFileParser
     * expected "ls -l" output (8+ columns). This caused the parser to skip
     * every line because parts.length was always < 8.
     *
     * @param directory the absolute path on the Android device (e.g. "/sdcard/Download")
     * @return list of AndroidFile objects representing the directory contents
     * @throws AdbException if the ADB command fails
     */
    public static List<AndroidFile> listFiles(String directory) throws AdbException {

        CommandResult result = CommandExecutor.executeCommand(
                "adb", "shell", "ls", "-l", directory
        );

        return AndroidFileParser.parse(result.getOutput(), directory);
    }
}