package com.androidnexus.controller;

import com.androidnexus.exception.AdbException;
import com.androidnexus.model.AndroidFile;
import com.androidnexus.service.FileService;

import java.util.List;

/**
 * Controller for file system operations on the connected Android device.
 *
 * Architecture role:
 *   Main / UI  →  FileController  →  FileService  →  CommandExecutor  →  ADB
 *
 * The controller acts as the boundary between the presentation layer
 * (Main, future JavaFX Dashboard) and the service layer. In Module 5
 * expansion, this class will grow to include all file operations
 * (download, upload, delete, rename, create folder, etc.) with input
 * validation before delegating to FileService.
 */
public class FileController {

    /**
     * Lists files and directories at the given path on the device.
     *
     * @param directory absolute path on the Android device (e.g. "/sdcard/Download")
     * @return list of files and directories at the specified path
     * @throws AdbException if the ADB command fails
     */
    public static List<AndroidFile> listFiles(String directory) throws AdbException {

        return FileService.listFiles(directory);
    }

    /**
     * Retrieves detailed metadata for a single file or directory on the device.
     *
     * @param remotePath absolute path on the Android device (e.g. "/sdcard/Download/file.txt")
     * @return the file details
     * @throws AdbException if the ADB command fails
     */
    public static AndroidFile getFileDetails(String remotePath) throws AdbException {
        if (remotePath == null || remotePath.isEmpty()) {
            throw new IllegalArgumentException("remotePath cannot be null or empty");
        }
        return FileService.getFileDetails(remotePath);
    }

    /**
     * Downloads a file or directory from the Android device to the local machine.
     *
     * @param remotePath absolute path on the Android device (e.g. "/sdcard/Download/file.txt")
     * @param localPath  absolute path on the local PC (e.g. "C:\Downloads\file.txt")
     * @throws AdbException if the ADB command fails
     */
    public static void downloadFile(String remotePath, String localPath) throws AdbException {
        if (remotePath == null || remotePath.isEmpty()) {
            throw new IllegalArgumentException("remotePath cannot be null or empty");
        }
        if (localPath == null || localPath.isEmpty()) {
            throw new IllegalArgumentException("localPath cannot be null or empty");
        }
        FileService.downloadFile(remotePath, localPath);
    }

    /**
     * Uploads a file or directory from the local machine to the Android device.
     *
     * @param localPath  absolute path on the local PC (e.g. "C:\Downloads\file.txt")
     * @param remotePath absolute path on the Android device (e.g. "/sdcard/Download/file.txt")
     * @throws AdbException if the ADB command fails
     */
    public static void uploadFile(String localPath, String remotePath) throws AdbException {
        if (localPath == null || localPath.isEmpty()) {
            throw new IllegalArgumentException("localPath cannot be null or empty");
        }
        if (remotePath == null || remotePath.isEmpty()) {
            throw new IllegalArgumentException("remotePath cannot be null or empty");
        }
        FileService.uploadFile(localPath, remotePath);
    }

    /**
     * Deletes a file or directory from the device.
     *
     * @param remotePath absolute path of the file or directory to delete
     * @throws AdbException if the ADB command fails
     */
    public static void deleteFile(String remotePath) throws AdbException {
        if (remotePath == null || remotePath.isEmpty()) {
            throw new IllegalArgumentException("remotePath cannot be null or empty");
        }
        FileService.deleteFile(remotePath);
    }

    /**
     * Renames a file or directory on the device.
     *
     * @param remotePath current absolute path (e.g. "/sdcard/Download/test.txt")
     * @param newName    new filename ONLY, not a full path (e.g. "newname.txt")
     * @throws AdbException if the ADB command fails
     */
    public static void renameFile(String remotePath, String newName) throws AdbException {
        if (remotePath == null || remotePath.isEmpty()) {
            throw new IllegalArgumentException("remotePath cannot be null or empty");
        }
        if (newName == null || newName.isEmpty()) {
            throw new IllegalArgumentException("newName cannot be null or empty");
        }
        FileService.renameFile(remotePath, newName);
    }

    /**
     * Creates a directory on the device.
     *
     * @param remotePath absolute path of the directory to create
     * @throws AdbException if the ADB command fails
     */
    public static void createFolder(String remotePath) throws AdbException {
        if (remotePath == null || remotePath.isEmpty()) {
            throw new IllegalArgumentException("remotePath cannot be null or empty");
        }
        FileService.createFolder(remotePath);
    }

    /**
     * Copies a file or directory on the device.
     *
     * @param sourcePath      absolute path of the file or directory to copy
     * @param destinationPath absolute destination path
     * @throws AdbException if the ADB command fails
     */
    public static void copyFile(String sourcePath, String destinationPath) throws AdbException {
        if (sourcePath == null || sourcePath.isEmpty()) {
            throw new IllegalArgumentException("sourcePath cannot be null or empty");
        }
        if (destinationPath == null || destinationPath.isEmpty()) {
            throw new IllegalArgumentException("destinationPath cannot be null or empty");
        }
        FileService.copyFile(sourcePath, destinationPath);
    }

    /**
     * Moves a file or directory on the device.
     *
     * @param sourcePath      absolute path of the file or directory to move
     * @param destinationPath absolute destination path
     * @throws AdbException if the ADB command fails
     */
    public static void moveFile(String sourcePath, String destinationPath) throws AdbException {
        if (sourcePath == null || sourcePath.isEmpty()) {
            throw new IllegalArgumentException("sourcePath cannot be null or empty");
        }
        if (destinationPath == null || destinationPath.isEmpty()) {
            throw new IllegalArgumentException("destinationPath cannot be null or empty");
        }
        FileService.moveFile(sourcePath, destinationPath);
    }

    /**
     * Searches for files or directories under the given directory matching a query.
     *
     * @param directory the root directory to search in (e.g. "/sdcard")
     * @param query     the search query (case-insensitive name pattern)
     * @return a list of matching AndroidFile objects
     * @throws AdbException if the ADB command fails
     */
    public static List<AndroidFile> searchFiles(String directory, String query) throws AdbException {
        if (directory == null || directory.isEmpty()) {
            throw new IllegalArgumentException("directory cannot be null or empty");
        }
        if (query == null || query.isEmpty()) {
            throw new IllegalArgumentException("query cannot be null or empty");
        }
        return FileService.searchFiles(directory, query);
    }
}