package com.androidnexus.ui.controller;

import com.androidnexus.ui.service.ScreenshotUiService;
import com.androidnexus.ui.utils.UiThreadExecutor;
import com.androidnexus.utils.Constants;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.io.File;

public class ScreenshotController extends BaseController {

    @FXML private ImageView imgPreview;
    @FXML private Label lblNoPreview;
    @FXML private Label lblSavePath;

    @FXML private Button btnCapture;
    @FXML private Button btnOpenFolder;
    @FXML private Button btnMirror;

    @FXML private Button btnPower;
    @FXML private Button btnVolumeUp;
    @FXML private Button btnVolumeDown;
    @FXML private Button btnMute;

    @FXML private HBox statusOverlay;
    @FXML private Label lblStatus;

    @FXML
    public void initialize() {
        lblSavePath.setText(Constants.LOCAL_SCREENSHOT_FOLDER);

        // Optional: Load the most recently captured screenshot if available
        loadLatestScreenshotPreview();
    }

    private void loadLatestScreenshotPreview() {
        File folder = new File(Constants.LOCAL_SCREENSHOT_FOLDER);
        if (folder.exists()) {
            File[] files = folder.listFiles();
            if (files != null && files.length > 0) {
                File latest = files[0];
                for (File f : files) {
                    if (f.lastModified() > latest.lastModified() && f.getName().toLowerCase().endsWith(".png")) {
                        latest = f;
                    }
                }
                if (latest.getName().toLowerCase().endsWith(".png")) {
                    Image img = new Image(latest.toURI().toString());
                    imgPreview.setImage(img);
                    lblNoPreview.setVisible(false);
                }
            }
        }
    }

    @FXML
    private void handleCapture(ActionEvent event) {
        showStatus("Capturing device display...");
        setButtonsDisable(true);

        Task<File> capTask = ScreenshotUiService.createCaptureScreenshotTask();

        capTask.setOnSucceeded(e -> {
            File file = capTask.getValue();
            if (file != null && file.exists()) {
                Image img = new Image(file.toURI().toString());
                imgPreview.setImage(img);
                lblNoPreview.setVisible(false);
            }
            hideStatus();
            setButtonsDisable(false);
        });

        capTask.setOnFailed(e -> {
            hideStatus();
            setButtonsDisable(false);
            showErrorAlert("Capture Failed", capTask.getException().getMessage());
        });

        UiThreadExecutor.runInBackground(capTask);
    }

    @FXML
    private void handleOpenFolder(ActionEvent event) {
        File folder = new File(Constants.LOCAL_SCREENSHOT_FOLDER);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        try {
            // Standard Java cross-platform way
            java.awt.Desktop.getDesktop().open(folder);
        } catch (Exception e) {
            // Fallback for Windows
            try {
                new ProcessBuilder("explorer.exe", folder.getAbsolutePath()).start();
            } catch (Exception ex) {
                showErrorAlert("Open Failed", "Could not open folder automatically: " + ex.getMessage());
            }
        }
    }

    @FXML
    private void handleMirror(ActionEvent event) {
        showStatus("Starting scrcpy mirror feed...");
        
        Task<Boolean> mirrorTask = ScreenshotUiService.createLaunchMirrorTask();
        
        mirrorTask.setOnSucceeded(e -> hideStatus());
        
        mirrorTask.setOnFailed(e -> {
            hideStatus();
            showErrorAlert("Mirroring Failed", mirrorTask.getException().getMessage());
        });

        UiThreadExecutor.runInBackground(mirrorTask);
    }

    @FXML
    private void handlePower(ActionEvent event) {
        executeControlTask(ScreenshotUiService.createPowerTask(), "Toggling power...");
    }

    @FXML
    private void handleVolumeUp(ActionEvent event) {
        executeControlTask(ScreenshotUiService.createVolumeUpTask(), "Raising volume...");
    }

    @FXML
    private void handleVolumeDown(ActionEvent event) {
        executeControlTask(ScreenshotUiService.createVolumeDownTask(), "Lowering volume...");
    }

    @FXML
    private void handleMute(ActionEvent event) {
        executeControlTask(ScreenshotUiService.createMuteTask(), "Muting audio...");
    }

    private void executeControlTask(Task<Void> task, String statusMsg) {
        showStatus(statusMsg);
        
        task.setOnSucceeded(e -> hideStatus());
        task.setOnFailed(e -> {
            hideStatus();
            showErrorAlert("Control Action Failed", task.getException().getMessage());
        });

        UiThreadExecutor.runInBackground(task);
    }

    private void setButtonsDisable(boolean disable) {
        btnCapture.setDisable(disable);
        btnOpenFolder.setDisable(disable);
        btnMirror.setDisable(disable);
        btnPower.setDisable(disable);
        btnVolumeUp.setDisable(disable);
        btnVolumeDown.setDisable(disable);
        btnMute.setDisable(disable);
    }

    private void showStatus(String text) {
        Platform.runLater(() -> {
            lblStatus.setText(text);
            statusOverlay.setVisible(true);
        });
    }

    private void hideStatus() {
        Platform.runLater(() -> statusOverlay.setVisible(false));
    }

    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
