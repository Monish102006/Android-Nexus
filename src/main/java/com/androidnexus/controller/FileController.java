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
}