package com.androidnexus.ui.controller;

import com.androidnexus.model.Device;
import com.androidnexus.model.DeviceCapabilities;
import com.androidnexus.ui.service.DeviceUiService;
import com.androidnexus.ui.utils.UiThreadExecutor;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

public class HomeController extends BaseController {

    @FXML private Label lblManufacturer;
    @FXML private Label lblModel;
    @FXML private Label lblAndroidVersion;
    @FXML private Label lblApiLevel;
    @FXML private Label lblScreenResolution;
    @FXML private Label lblSerialNumber;

    @FXML private Label lblBatteryPercent;
    @FXML private ProgressBar pbBattery;
    @FXML private Label lblPowerSource;
    @FXML private Label lblBatteryHealth;

    @FXML private Label lblStorageUsed;
    @FXML private ProgressBar pbStorage;
    @FXML private Label lblStorageTotal;
    @FXML private Label lblStorageFree;

    @FXML private Label badgeNotifs;
    @FXML private Label badgeRecording;
    @FXML private Label badgeFlashlight;
    @FXML private Label badgeMedia;

    @FXML private StackPane loadingOverlay;
    @FXML private GridPane contentGrid;
    @FXML private Button btnRefresh;

    @FXML
    public void initialize() {
        loadDeviceData(null);
    }

    @FXML
    private void loadDeviceData(ActionEvent event) {
        // Show loading progress overlays
        loadingOverlay.setVisible(true);
        contentGrid.setDisable(true);
        btnRefresh.setDisable(true);

        Task<Device> fetchTask = DeviceUiService.createFetchDeviceTask();

        fetchTask.setOnSucceeded(e -> {
            Device device = fetchTask.getValue();
            populateUi(device);

            loadingOverlay.setVisible(false);
            contentGrid.setDisable(false);
            btnRefresh.setDisable(false);

            // Notify outer ShellController to sync status bar connection status
            if (shellController != null) {
                shellController.updateStatusBar();
            }
        });

        fetchTask.setOnFailed(e -> {
            Throwable err = fetchTask.getException();
            err.printStackTrace();
            displayConnectionError(err.getMessage());

            loadingOverlay.setVisible(false);
            contentGrid.setDisable(false);
            btnRefresh.setDisable(false);

            if (shellController != null) {
                shellController.updateStatusBar();
            }
        });

        UiThreadExecutor.runInBackground(fetchTask);
    }

    private void populateUi(Device device) {
        // System Properties
        lblManufacturer.setText(device.getManufacturer());
        lblModel.setText(device.getModel());
        lblAndroidVersion.setText(device.getAndroidVersion());
        lblApiLevel.setText(device.getApiLevel() != null ? device.getApiLevel() : "Unknown");
        lblScreenResolution.setText(device.getScreenResolution() != null ? device.getScreenResolution() : "Unknown");
        lblSerialNumber.setText(device.getSerialNumber());

        // Battery
        int battery = device.getBatteryLevel();
        lblBatteryPercent.setText(battery + "%");
        pbBattery.setProgress(battery / 100.0);

        // Customize battery color bar by health ranges
        if (battery <= 15) {
            pbBattery.setStyle("-fx-accent: #d32f2f;"); // Red
        } else if (battery <= 30) {
            pbBattery.setStyle("-fx-accent: #f57c00;"); // Orange
        } else {
            pbBattery.setStyle("-fx-accent: #4caf50;"); // Green
        }

        // Storage
        String total = device.getStorageTotal();
        String free = device.getStorageAvailable();
        lblStorageTotal.setText(total != null ? total : "-- GB");
        lblStorageFree.setText(free != null ? free : "-- GB");

        double storageProgress = computeStorageProgress(total, free);
        pbStorage.setProgress(storageProgress);
        pbStorage.setStyle("-fx-accent: -accent-color;");

        if (total != null && free != null && !total.equals("Unknown") && !free.equals("Unknown")) {
            lblStorageUsed.setText(String.format("%.1f%%", storageProgress * 100.0));
        } else {
            lblStorageUsed.setText("Unknown");
        }

        // Capabilities Badges
        DeviceCapabilities caps = device.getCapabilities();
        if (caps != null) {
            applyBadgeStyle(badgeNotifs, caps.supportsNotificationAccess());
            applyBadgeStyle(badgeRecording, caps.supportsRecording());
            applyBadgeStyle(badgeFlashlight, caps.supportsFlashlight());
            applyBadgeStyle(badgeMedia, caps.supportsMediaControl());
        }
    }

    private void displayConnectionError(String msg) {
        lblManufacturer.setText("Disconnected");
        lblModel.setText("Disconnected");
        lblAndroidVersion.setText("N/A");
        lblApiLevel.setText("N/A");
        lblScreenResolution.setText("N/A");
        lblSerialNumber.setText("N/A");

        lblBatteryPercent.setText("--%");
        pbBattery.setProgress(0);
        lblStorageUsed.setText("N/A");
        pbStorage.setProgress(0);
        lblStorageTotal.setText("-- GB");
        lblStorageFree.setText("-- GB");

        // Set all badges to disabled states
        applyBadgeStyle(badgeNotifs, false);
        applyBadgeStyle(badgeRecording, false);
        applyBadgeStyle(badgeFlashlight, false);
        applyBadgeStyle(badgeMedia, false);
    }

    private void applyBadgeStyle(Label badge, boolean supported) {
        if (supported) {
            badge.setStyle("-fx-background-color: #2e7d32; -fx-text-fill: white;"); // Emerald green
        } else {
            badge.setStyle("-fx-background-color: #424242; -fx-text-fill: #9e9e9e;"); // Greyed out inactive
        }
    }

    private double computeStorageProgress(String totalStr, String freeStr) {
        if (totalStr == null || freeStr == null || totalStr.equals("Unknown") || freeStr.equals("Unknown")) {
            return 0.0;
        }
        try {
            double total = parseSizeToKb(totalStr);
            double free = parseSizeToKb(freeStr);
            if (total > 0) {
                return (total - free) / total;
            }
        } catch (Exception e) {
            // ignore parsing bugs
        }
        return 0.0;
    }

    private double parseSizeToKb(String sizeStr) {
        String cleaned = sizeStr.replaceAll("[^0-9.]", "").trim();
        double val = Double.parseDouble(cleaned);
        String upper = sizeStr.toUpperCase();
        if (upper.contains("G")) {
            return val * 1024 * 1024;
        }
        if (upper.contains("M")) {
            return val * 1024;
        }
        if (upper.contains("K")) {
            return val;
        }
        return val;
    }
}
