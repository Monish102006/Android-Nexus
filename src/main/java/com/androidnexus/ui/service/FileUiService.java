package com.androidnexus.ui.service;

import com.androidnexus.controller.FileController;
import com.androidnexus.model.AndroidFile;
import javafx.concurrent.Task;

import java.io.File;
import java.util.List;

/**
 * UI Service managing background task coordination for file system operations.
 *
 * Prevents UI hangs by offloading ADB file list fetching and transfers
 * to background tasks.
 */
public class FileUiService {

    /**
     * Lists directories/files on the device for a given remote directory.
     */
    public static Task<List<AndroidFile>> createListFilesTask(String remotePath) {
        return new Task<>() {
            @Override
            protected List<AndroidFile> call() throws Exception {
                return FileController.listFiles(remotePath);
            }
        };
    }

    /**
     * Downloads (adb pull) a remote file/folder from the device to local storage.
     */
    public static Task<Void> createDownloadTask(String remotePath, File localTarget) {
        return new Task<>() {
            @Override
            protected Void call() throws Exception {
                FileController.downloadFile(remotePath, localTarget.getAbsolutePath());
                return null;
            }
        };
    }

    /**
     * Uploads (adb push) a local file/folder to a remote directory on the device.
     */
    public static Task<Void> createUploadTask(File localFile, String remoteTargetDir) {
        return new Task<>() {
            @Override
            protected Void call() throws Exception {
                FileController.uploadFile(localFile.getAbsolutePath(), remoteTargetDir);
                return null;
            }
        };
    }

    /**
     * Creates a new folder (mkdir -p) on the device.
     */
    public static Task<Void> createCreateFolderTask(String remotePath) {
        return new Task<>() {
            @Override
            protected Void call() throws Exception {
                FileController.createFolder(remotePath);
                return null;
            }
        };
    }

    /**
     * Renames a remote file/folder.
     */
    public static Task<Void> createRenameTask(String remotePath, String newName) {
        return new Task<>() {
            @Override
            protected Void call() throws Exception {
                FileController.renameFile(remotePath, newName);
                return null;
            }
        };
    }

    /**
     * Deletes a remote file/folder.
     */
    public static Task<Void> createDeleteTask(String remotePath) {
        return new Task<>() {
            @Override
            protected Void call() throws Exception {
                FileController.deleteFile(remotePath);
                return null;
            }
        };
    }
}
