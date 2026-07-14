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

    /**
     * Retrieves detailed metadata for a single file or directory.
     *
     * @param remotePath the absolute path on the Android device (e.g. "/sdcard/Download/file.txt")
     * @return the AndroidFile object representing the file
     * @throws AdbException if the file does not exist or the ADB command fails
     */
    public static AndroidFile getFileDetails(String remotePath) throws AdbException {
        if (remotePath == null || remotePath.isEmpty()) {
            throw new IllegalArgumentException("remotePath cannot be null or empty");
        }

        // Extract parent directory
        int lastSlash = remotePath.lastIndexOf('/');
        String parentDir = "/";
        if (lastSlash > 0) {
            parentDir = remotePath.substring(0, lastSlash);
        }

        // Run ls -ld to get info of the path itself
        CommandResult result = CommandExecutor.executeCommand(
                "adb", "shell", "ls", "-ld", remotePath
        );

        result.requireSuccess();

        List<AndroidFile> parsed = AndroidFileParser.parse(result.getOutput(), parentDir);
        if (parsed.isEmpty()) {
            throw new AdbException("File metadata not found or failed to parse: " + remotePath);
        }

        return parsed.get(0);
    }

    /**
     * Downloads a file or directory from the Android device to the local machine.
     *
     * @param remotePath the absolute path on the Android device (e.g. "/sdcard/Download/file.txt")
     * @param localPath  the absolute path on the local PC (e.g. "C:\Downloads\file.txt")
     * @throws AdbException if the transfer fails
     */
    public static void downloadFile(String remotePath, String localPath) throws AdbException {
        if (remotePath == null || remotePath.isEmpty()) {
            throw new IllegalArgumentException("remotePath cannot be null or empty");
        }
        if (localPath == null || localPath.isEmpty()) {
            throw new IllegalArgumentException("localPath cannot be null or empty");
        }

        // ADB pull has a 5-minute (300s) timeout to allow large file transfers
        CommandResult result = CommandExecutor.executeCommand(
                300, "adb", "pull", remotePath, localPath
        );
        result.requireSuccess();
    }

    /**
     * Uploads a file or directory from the local machine to the Android device.
     *
     * @param localPath  the absolute path on the local PC (e.g. "C:\Downloads\file.txt")
     * @param remotePath the absolute path on the Android device (e.g. "/sdcard/Download/file.txt")
     * @throws AdbException if the transfer fails
     */
    public static void uploadFile(String localPath, String remotePath) throws AdbException {
        if (localPath == null || localPath.isEmpty()) {
            throw new IllegalArgumentException("localPath cannot be null or empty");
        }
        if (remotePath == null || remotePath.isEmpty()) {
            throw new IllegalArgumentException("remotePath cannot be null or empty");
        }

        // ADB push has a 5-minute (300s) timeout to allow large file transfers
        CommandResult result = CommandExecutor.executeCommand(
                300, "adb", "push", localPath, remotePath
        );
        result.requireSuccess();
    }

    /**
     * Deletes a file or directory from the device.
     *
     * @param remotePath absolute path of the file or directory to delete
     * @throws AdbException if the command fails
     */
    public static void deleteFile(String remotePath) throws AdbException {
        if (remotePath == null || remotePath.isEmpty()) {
            throw new IllegalArgumentException("remotePath cannot be null or empty");
        }
        if (remotePath.equals("/")) {
            throw new IllegalArgumentException("Cannot delete the root directory");
        }

        // rm -rf is used to recursively force delete directories and files
        CommandResult result = CommandExecutor.executeCommand(
                "adb", "shell", "rm", "-rf", remotePath
        );
        result.requireSuccess();
    }

    /**
     * Renames a file or directory on the device.
     *
     * @param remotePath current absolute path (e.g. "/sdcard/Download/test.txt")
     * @param newName    new filename ONLY, not a full path (e.g. "newname.txt")
     * @throws AdbException if the command fails
     */
    public static void renameFile(String remotePath, String newName) throws AdbException {
        if (remotePath == null || remotePath.isEmpty()) {
            throw new IllegalArgumentException("remotePath cannot be null or empty");
        }
        if (remotePath.equals("/")) {
            throw new IllegalArgumentException("Cannot rename the root directory");
        }
        if (newName == null || newName.isEmpty() || newName.contains("/")) {
            throw new IllegalArgumentException("newName must be a valid filename and cannot contain path separators");
        }

        // Construct new path in the same parent directory
        int lastSlash = remotePath.lastIndexOf('/');
        String parentDir = "/";
        if (lastSlash > 0) {
            parentDir = remotePath.substring(0, lastSlash);
        }

        String newPath = parentDir.endsWith("/") ? parentDir + newName : parentDir + "/" + newName;

        CommandResult result = CommandExecutor.executeCommand(
                "adb", "shell", "mv", remotePath, newPath
        );
        result.requireSuccess();
    }

    /**
     * Creates a directory on the device.
     *
     * @param remotePath absolute path of the directory to create
     * @throws AdbException if the command fails
     */
    public static void createFolder(String remotePath) throws AdbException {
        if (remotePath == null || remotePath.isEmpty()) {
            throw new IllegalArgumentException("remotePath cannot be null or empty");
        }

        // mkdir -p creates parent directories as needed and does not fail if directory exists
        CommandResult result = CommandExecutor.executeCommand(
                "adb", "shell", "mkdir", "-p", remotePath
        );
        result.requireSuccess();
    }

    /**
     * Copies a file or directory on the device.
     *
     * @param sourcePath      the absolute path of the file or directory to copy
     * @param destinationPath the absolute destination path
     * @throws AdbException if the command fails
     */
    public static void copyFile(String sourcePath, String destinationPath) throws AdbException {
        if (sourcePath == null || sourcePath.isEmpty()) {
            throw new IllegalArgumentException("sourcePath cannot be null or empty");
        }
        if (destinationPath == null || destinationPath.isEmpty()) {
            throw new IllegalArgumentException("destinationPath cannot be null or empty");
        }

        // cp -r is used to recursively copy files and folders
        // Timeout is 120s as copy can take some time
        CommandResult result = CommandExecutor.executeCommand(
                120, "adb", "shell", "cp", "-r", sourcePath, destinationPath
        );
        result.requireSuccess();
    }

    /**
     * Moves a file or directory on the device.
     *
     * @param sourcePath      the absolute path of the file or directory to move
     * @param destinationPath the absolute destination path
     * @throws AdbException if the command fails
     */
    public static void moveFile(String sourcePath, String destinationPath) throws AdbException {
        if (sourcePath == null || sourcePath.isEmpty()) {
            throw new IllegalArgumentException("sourcePath cannot be null or empty");
        }
        if (destinationPath == null || destinationPath.isEmpty()) {
            throw new IllegalArgumentException("destinationPath cannot be null or empty");
        }

        // mv moves directories and files instantly (unless across mount points)
        // Timeout is 60s
        CommandResult result = CommandExecutor.executeCommand(
                60, "adb", "shell", "mv", sourcePath, destinationPath
        );
        result.requireSuccess();
    }

    /**
     * Searches for files or directories under the given directory matching a query.
     *
     * @param directory the root directory to search in (e.g. "/sdcard")
     * @param query     the search query (case-insensitive name pattern)
     * @return a list of matching AndroidFile objects
     * @throws AdbException if the command fails
     */
    public static List<AndroidFile> searchFiles(String directory, String query) throws AdbException {
        if (directory == null || directory.isEmpty()) {
            throw new IllegalArgumentException("directory cannot be null or empty");
        }
        if (query == null || query.isEmpty()) {
            throw new IllegalArgumentException("query cannot be null or empty");
        }

        // We use find to locate the paths, and run ls -ld on each match using a shell loop
        // to return the detailed ls -l output in a single call.
        // -iname is case-insensitive name matching.
        String shellCommand = String.format(
                "find \"%s\" -iname \"*%s*\" | while read -r line; do ls -ld \"$line\"; done",
                directory, query
        );

        CommandResult result = CommandExecutor.executeCommand(
                60, "adb", "shell", shellCommand
        );

        // Search might return empty if no matches, which doesn't mean command failed.
        result.requireSuccess();

        return AndroidFileParser.parse(result.getOutput(), directory);
    }
}