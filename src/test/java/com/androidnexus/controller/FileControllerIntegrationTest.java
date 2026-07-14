package com.androidnexus.controller;

import com.androidnexus.adb.DeviceDetector;
import com.androidnexus.exception.AdbException;
import com.androidnexus.model.AndroidFile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assumptions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileControllerIntegrationTest {

    private String testRootDir;
    private Path localTempDir;

    @BeforeEach
    public void setUp() throws Exception {
        // Skip integration tests if no device is connected to ADB
        boolean isConnected = false;
        try {
            isConnected = DeviceDetector.isDeviceConnected();
        } catch (AdbException e) {
            // ADB not running or error
        }
        Assumptions.assumeTrue(isConnected, "ADB device not connected. Skipping integration tests.");

        // Setup unique directories for isolation
        long timestamp = System.currentTimeMillis();
        testRootDir = "/sdcard/Download/AndroidNexusTest_" + timestamp;
        localTempDir = Files.createTempDirectory("nexus_integration_");

        // Ensure clean test root on device
        FileController.createFolder(testRootDir);
    }

    @AfterEach
    public void tearDown() {
        // Cleanup device files
        try {
            if (DeviceDetector.isDeviceConnected()) {
                FileController.deleteFile(testRootDir);
            }
        } catch (Exception e) {
            // Ignore teardown cleanup errors
        }

        // Cleanup local temp files
        if (localTempDir != null) {
            try {
                Files.walk(localTempDir)
                        .map(Path::toFile)
                        .forEach(java.io.File::delete);
            } catch (IOException e) {
                // Ignore
            }
        }
    }

    @Test
    public void testFileLifecycleIntegration() throws Exception {
        // 1. Create a subfolder
        String subFolderPath = testRootDir + "/SubDir";
        FileController.createFolder(subFolderPath);

        // Verify folder listing
        List<AndroidFile> files = FileController.listFiles(testRootDir);
        assertEquals(1, files.size());
        AndroidFile folder = files.get(0);
        assertEquals("SubDir", folder.getName());
        assertTrue(folder.isDirectory());

        // 2. Upload a file
        Path localFile = localTempDir.resolve("test_upload.txt");
        String content = "Hello Android Nexus Integration Test!";
        Files.writeString(localFile, content);

        String remoteFilePath = testRootDir + "/test_file.txt";
        FileController.uploadFile(localFile.toAbsolutePath().toString(), remoteFilePath);

        // Verify uploaded file metadata
        AndroidFile remoteFile = FileController.getFileDetails(remoteFilePath);
        assertEquals("test_file.txt", remoteFile.getName());
        assertFalse(remoteFile.isDirectory());
        assertEquals(content.length(), remoteFile.getSize());

        // 3. Download the file
        Path localDownload = localTempDir.resolve("test_download.txt");
        FileController.downloadFile(remoteFilePath, localDownload.toAbsolutePath().toString());

        assertTrue(Files.exists(localDownload));
        assertEquals(content, Files.readString(localDownload));

        // 4. Rename the file
        FileController.renameFile(remoteFilePath, "renamed_file.txt");
        String renamedFilePath = testRootDir + "/renamed_file.txt";

        // Verify renamed file details
        AndroidFile renamedFile = FileController.getFileDetails(renamedFilePath);
        assertEquals("renamed_file.txt", renamedFile.getName());
        assertEquals(content.length(), renamedFile.getSize());

        // Verify old path is gone
        assertThrows(AdbException.class, () -> {
            FileController.getFileDetails(remoteFilePath);
        });

        // 5. Copy the file
        String copyFilePath = testRootDir + "/copy_file.txt";
        FileController.copyFile(renamedFilePath, copyFilePath);

        // Verify both source and destination exist
        assertNotNull(FileController.getFileDetails(renamedFilePath));
        assertNotNull(FileController.getFileDetails(copyFilePath));

        // 6. Move the file
        String moveFilePath = testRootDir + "/moved_file.txt";
        FileController.moveFile(copyFilePath, moveFilePath);

        // Verify moved file exists
        assertNotNull(FileController.getFileDetails(moveFilePath));

        // Verify source path is gone
        assertThrows(AdbException.class, () -> {
            FileController.getFileDetails(copyFilePath);
        });

        // 7. Search for files
        List<AndroidFile> searchResults = FileController.searchFiles(testRootDir, "moved");
        assertEquals(1, searchResults.size());
        assertEquals("moved_file.txt", searchResults.get(0).getName());
    }
}
