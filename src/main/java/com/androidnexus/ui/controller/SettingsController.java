package com.androidnexus.ui.controller;

import com.androidnexus.ui.theme.ThemeManager;
import com.androidnexus.ui.theme.ThemeType;
import com.androidnexus.utils.Constants;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;

import java.io.File;

public class SettingsController extends BaseController {

    @FXML private RadioButton rbDark;
    @FXML private RadioButton rbLight;
    @FXML private RadioButton rbDracula;
    @FXML private ToggleGroup themeGroup;

    @FXML private TextField txtSyncInterval;
    @FXML private TextField txtScreenshotsFolder;
    @FXML private TextField txtRecordingsFolder;

    @FXML private Button btnBrowseScreenshots;
    @FXML private Button btnBrowseRecordings;
    @FXML private Button btnSave;
    @FXML private Label lblSaveStatus;

    private static String customScreenshotsFolder = Constants.LOCAL_SCREENSHOT_FOLDER;
    private static String customRecordingsFolder = Constants.LOCAL_RECORDING_FOLDER;

    public static String getScreenshotsFolder() {
        return customScreenshotsFolder;
    }

    public static String getRecordingsFolder() {
        return customRecordingsFolder;
    }

    @FXML
    public void initialize() {
        // 1. Setup Theme Selection from active ThemeManager state
        ThemeType currentTheme = ThemeManager.getActiveTheme();
        switch (currentTheme) {
            case LIGHT -> rbLight.setSelected(true);
            case DRACULA -> rbDracula.setSelected(true);
            default -> rbDark.setSelected(true);
        }

        // 2. Load sync interval configurations
        txtSyncInterval.setText(String.valueOf(NotificationController.getSyncIntervalMs()));

        // 3. Load directories paths
        txtScreenshotsFolder.setText(customScreenshotsFolder);
        txtRecordingsFolder.setText(customRecordingsFolder);
    }

    @FXML
    private void handleBrowseScreenshots(ActionEvent event) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Choose Screenshots Directory");
        File selected = chooser.showDialog(btnBrowseScreenshots.getScene().getWindow());
        if (selected != null) {
            txtScreenshotsFolder.setText(selected.getAbsolutePath());
        }
    }

    @FXML
    private void handleBrowseRecordings(ActionEvent event) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Choose Recordings Directory");
        File selected = chooser.showDialog(btnBrowseRecordings.getScene().getWindow());
        if (selected != null) {
            txtRecordingsFolder.setText(selected.getAbsolutePath());
        }
    }

    @FXML
    private void handleSave(ActionEvent event) {
        lblSaveStatus.setVisible(false);

        // 1. Validate sync rate input
        int interval;
        try {
            interval = Integer.parseInt(txtSyncInterval.getText().trim());
            if (interval < 500) {
                showErrorAlert("Invalid Value", "Notification Sync Rate must be at least 500 milliseconds.");
                return;
            }
        } catch (NumberFormatException e) {
            showErrorAlert("Invalid Value", "Sync Rate must be a valid integer number.");
            return;
        }

        // 2. Save sync rate
        NotificationController.setSyncIntervalMs(interval);

        // 3. Save custom target paths
        customScreenshotsFolder = txtScreenshotsFolder.getText().trim();
        customRecordingsFolder = txtRecordingsFolder.getText().trim();

        // 4. Determine and apply theme changes dynamically
        ThemeType targetTheme = ThemeType.DARK;
        if (rbLight.isSelected()) {
            targetTheme = ThemeType.LIGHT;
        } else if (rbDracula.isSelected()) {
            targetTheme = ThemeType.DRACULA;
        }

        // Apply selected theme to active Scene
        ThemeManager.applyTheme(btnSave.getScene(), targetTheme);

        // 5. Update Shell Status Bar
        if (shellController != null) {
            shellController.updateStatusBar();
        }

        // Show feedback status
        lblSaveStatus.setText("Configuration saved and applied.");
        lblSaveStatus.setStyle("-fx-text-fill: -accent-color; -fx-font-weight: bold;");
        lblSaveStatus.setVisible(true);

        // Flash status out after 2 seconds
        new Thread(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ignored) {}
            Platform.runLater(() -> lblSaveStatus.setVisible(false));
        }).start();
    }

    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
