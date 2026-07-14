package com.androidnexus.ui.controller;

import com.androidnexus.model.AndroidApplication;
import com.androidnexus.model.ApplicationType;
import com.androidnexus.ui.service.AppUiService;
import com.androidnexus.ui.utils.UiThreadExecutor;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AppManagerController extends BaseController {

    @FXML private TextField txtSearch;
    @FXML private RadioButton rbAll;
    @FXML private RadioButton rbUser;
    @FXML private RadioButton rbSystem;
    @FXML private ToggleGroup appFilterGroup;
    @FXML private Button btnRefresh;

    @FXML private TableView<AndroidApplication> tableApps;
    @FXML private TableColumn<AndroidApplication, String> colName;
    @FXML private TableColumn<AndroidApplication, String> colPackage;
    @FXML private TableColumn<AndroidApplication, String> colVersion;
    @FXML private TableColumn<AndroidApplication, ApplicationType> colType;

    @FXML private Button btnLaunch;
    @FXML private Button btnForceStop;
    @FXML private Button btnClearData;
    @FXML private Button btnUninstall;
    @FXML private Button btnExtract;
    @FXML private Button btnSideload;

    @FXML private HBox statusOverlay;
    @FXML private Label lblStatus;

    private final List<AndroidApplication> allApplications = new ArrayList<>();

    @FXML
    public void initialize() {
        // 1. Configure Table Columns
        colPackage.setCellValueFactory(new PropertyValueFactory<>("packageName"));
        
        colName.setCellValueFactory(cellData -> {
            AndroidApplication app = cellData.getValue();
            // Fallback to package name if application label is null/empty
            String label = app.getAppName();
            if (label == null || label.trim().isEmpty()) {
                label = app.getPackageName();
            }
            return new javafx.beans.property.SimpleStringProperty(label);
        });

        colVersion.setCellValueFactory(cellData -> {
            AndroidApplication app = cellData.getValue();
            String ver = app.getVersionName();
            if (ver == null) ver = "N/A";
            if (app.getVersionCode() > 0) {
                ver += " (" + app.getVersionCode() + ")";
            }
            return new javafx.beans.property.SimpleStringProperty(ver);
        });

        // 2. Custom Type Badge rendering cell (USER -> Green, SYSTEM -> Orange)
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colType.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(ApplicationType item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label badge = new Label(item.toString());
                    if (item == ApplicationType.USER) {
                        badge.setStyle("-fx-background-color: #2e7d32; -fx-text-fill: white; -fx-padding: 3 8 3 8; -fx-background-radius: 4px; -fx-font-weight: bold; -fx-font-size: 11px;");
                    } else {
                        badge.setStyle("-fx-background-color: #f57c00; -fx-text-fill: white; -fx-padding: 3 8 3 8; -fx-background-radius: 4px; -fx-font-weight: bold; -fx-font-size: 11px;");
                    }
                    setGraphic(badge);
                    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                }
            }
        });

        // 3. Bind Radio Button Toggle Filters
        appFilterGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> applyFilters());

        // 4. Load initial apps list
        loadApplications();
    }

    private void loadApplications() {
        showStatus("Reading application manager registry...");
        btnRefresh.setDisable(true);

        Task<List<AndroidApplication>> listTask = AppUiService.createListAppsTask();

        listTask.setOnSucceeded(e -> {
            allApplications.clear();
            allApplications.addAll(listTask.getValue());

            applyFilters();
            hideStatus();
            btnRefresh.setDisable(false);
        });

        listTask.setOnFailed(e -> {
            hideStatus();
            btnRefresh.setDisable(false);
            showErrorAlert("Listing Failed", "Could not query packages: " + listTask.getException().getMessage());
        });

        UiThreadExecutor.runInBackground(listTask);
    }

    private void applyFilters() {
        String query = txtSearch.getText().toLowerCase().trim();
        Toggle selectedFilter = appFilterGroup.getSelectedToggle();

        List<AndroidApplication> filtered = new ArrayList<>();
        for (AndroidApplication app : allApplications) {
            // Filter by Type
            if (selectedFilter == rbUser && app.getType() != ApplicationType.USER) {
                continue;
            }
            if (selectedFilter == rbSystem && app.getType() != ApplicationType.SYSTEM) {
                continue;
            }

            // Filter by Name / Package Match
            String name = app.getAppName() != null ? app.getAppName().toLowerCase() : "";
            String pkg = app.getPackageName().toLowerCase();

            if (query.isEmpty() || name.contains(query) || pkg.contains(query)) {
                filtered.add(app);
            }
        }

        tableApps.setItems(FXCollections.observableArrayList(filtered));
    }

    @FXML
    private void handleSearchKey() {
        applyFilters();
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        loadApplications();
    }

    @FXML
    private void handleLaunch(ActionEvent event) {
        AndroidApplication selected = tableApps.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarningAlert("No Selection", "Please select an application to launch.");
            return;
        }

        showStatus("Launching " + selected.getPackageName() + "...");
        Task<Void> launchTask = AppUiService.createLaunchAppTask(selected.getPackageName());
        
        launchTask.setOnSucceeded(e -> hideStatus());
        launchTask.setOnFailed(e -> {
            hideStatus();
            showErrorAlert("Launch Failed", launchTask.getException().getMessage());
        });

        UiThreadExecutor.runInBackground(launchTask);
    }

    @FXML
    private void handleForceStop(ActionEvent event) {
        AndroidApplication selected = tableApps.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarningAlert("No Selection", "Please select an application to stop.");
            return;
        }

        showStatus("Stopping " + selected.getPackageName() + "...");
        Task<Void> stopTask = AppUiService.createForceStopTask(selected.getPackageName());

        stopTask.setOnSucceeded(e -> {
            hideStatus();
            showInfoAlert("Application Stopped", "Successfully stopped " + selected.getPackageName());
        });
        stopTask.setOnFailed(e -> {
            hideStatus();
            showErrorAlert("Stop Failed", stopTask.getException().getMessage());
        });

        UiThreadExecutor.runInBackground(stopTask);
    }

    @FXML
    private void handleClearData(ActionEvent event) {
        AndroidApplication selected = tableApps.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarningAlert("No Selection", "Please select an application to clear.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Clear Data");
        alert.setHeaderText("Clear data for " + selected.getPackageName() + "?");
        alert.setContentText("This will wipe all databases, files, and settings for this app.");

        Optional<ButtonType> res = alert.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.OK) {
            showStatus("Clearing data...");
            Task<Void> clearTask = AppUiService.createClearDataTask(selected.getPackageName());

            clearTask.setOnSucceeded(e -> {
                hideStatus();
                showInfoAlert("Wipe Finished", "Successfully cleared data.");
            });
            clearTask.setOnFailed(e -> {
                hideStatus();
                showErrorAlert("Wipe Failed", clearTask.getException().getMessage());
            });

            UiThreadExecutor.runInBackground(clearTask);
        }
    }

    @FXML
    private void handleUninstall(ActionEvent event) {
        AndroidApplication selected = tableApps.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarningAlert("No Selection", "Please select an application to uninstall.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Uninstall");
        alert.setHeaderText("Uninstall " + selected.getPackageName() + "?");
        alert.setContentText("Are you sure you want to completely remove this package?");

        Optional<ButtonType> res = alert.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.OK) {
            showStatus("Uninstalling package...");
            Task<Void> uninstallTask = AppUiService.createUninstallAppTask(selected.getPackageName());

            uninstallTask.setOnSucceeded(e -> {
                hideStatus();
                showInfoAlert("Uninstall Finished", "Successfully uninstalled package.");
                loadApplications(); // Refresh list
            });
            uninstallTask.setOnFailed(e -> {
                hideStatus();
                showErrorAlert("Uninstall Failed", uninstallTask.getException().getMessage());
            });

            UiThreadExecutor.runInBackground(uninstallTask);
        }
    }

    @FXML
    private void handleExtract(ActionEvent event) {
        AndroidApplication selected = tableApps.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarningAlert("No Selection", "Please select an application to extract.");
            return;
        }

        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("Select Extraction Destination");
        File destDir = dirChooser.showDialog(tableApps.getScene().getWindow());
        if (destDir == null) {
            return;
        }

        showStatus("Extracting APK for " + selected.getPackageName() + "...");
        Task<Void> extTask = AppUiService.createExtractApkTask(selected.getPackageName(), destDir);

        extTask.setOnSucceeded(e -> {
            hideStatus();
            showInfoAlert("Extraction Finished", "APK successfully extracted and saved to: " + destDir.getAbsolutePath());
        });
        extTask.setOnFailed(e -> {
            hideStatus();
            showErrorAlert("Extraction Failed", extTask.getException().getMessage());
        });

        UiThreadExecutor.runInBackground(extTask);
    }

    @FXML
    private void handleSideload(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select APK to Install");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Android APK Files (*.apk)", "*.apk"));
        File localApk = fileChooser.showOpenDialog(tableApps.getScene().getWindow());
        if (localApk == null) {
            return;
        }

        showStatus("Sideloading " + localApk.getName() + "...");
        Task<Void> installTask = AppUiService.createInstallAppTask(localApk);

        installTask.setOnSucceeded(e -> {
            hideStatus();
            showInfoAlert("Installation Finished", "Successfully installed package: " + localApk.getName());
            loadApplications(); // Refresh
        });
        installTask.setOnFailed(e -> {
            hideStatus();
            showErrorAlert("Installation Failed", installTask.getException().getMessage());
        });

        UiThreadExecutor.runInBackground(installTask);
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

    private void showWarningAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showInfoAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
