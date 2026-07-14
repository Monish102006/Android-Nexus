package com.androidnexus.ui.controller;

import com.androidnexus.adb.AdbManager;
import com.androidnexus.adb.DeviceDetector;
import com.androidnexus.exception.AdbException;
import com.androidnexus.ui.theme.ThemeManager;
import com.androidnexus.ui.utils.SvgIconFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class ShellController {

    @FXML private Button btnHome;
    @FXML private Button btnFiles;
    @FXML private Button btnApps;
    @FXML private Button btnNotifs;
    @FXML private Button btnScreen;
    @FXML private Button btnSettings;
    @FXML private TextField searchField;
    @FXML private StackPane contentArea;

    @FXML private Label lblConnectionStatus;
    @FXML private Label lblAdbVersion;
    @FXML private Label lblBackgroundTasks;
    @FXML private Label lblActiveTheme;

    private Button activeButton;

    @FXML
    public void initialize() {
        // 1. Assign SVG vector icons to the sidebar navigation buttons
        btnHome.setGraphic(SvgIconFactory.createIcon(SvgIconFactory.PATH_HOME));
        btnFiles.setGraphic(SvgIconFactory.createIcon(SvgIconFactory.PATH_FILES));
        btnApps.setGraphic(SvgIconFactory.createIcon(SvgIconFactory.PATH_APPS));
        btnNotifs.setGraphic(SvgIconFactory.createIcon(SvgIconFactory.PATH_NOTIFS));
        btnScreen.setGraphic(SvgIconFactory.createIcon(SvgIconFactory.PATH_SCREEN));
        btnSettings.setGraphic(SvgIconFactory.createIcon(SvgIconFactory.PATH_SETTINGS));

        // 2. Initialize Status Bar metadata
        updateStatusBar();

        // 3. Highlight the default Home navigation option and load its view
        selectNavigationButton(btnHome);
        navigateTo("/fxml/home.fxml");
    }

    @FXML
    private void handleNavigation(ActionEvent event) {
        Button source = (Button) event.getSource();
        if (source == activeButton) {
            return;
        }

        selectNavigationButton(source);

        String fxmlPath = "";
        if (source == btnHome) {
            fxmlPath = "/fxml/home.fxml";
        } else if (source == btnFiles) {
            fxmlPath = "/fxml/file_explorer.fxml";
        } else if (source == btnApps) {
            fxmlPath = "/fxml/app_manager.fxml";
        } else if (source == btnNotifs) {
            fxmlPath = "/fxml/notification_panel.fxml";
        } else if (source == btnScreen) {
            fxmlPath = "/fxml/screenshot_viewer.fxml";
        } else if (source == btnSettings) {
            fxmlPath = "/fxml/settings.fxml";
        }

        if (!fxmlPath.isEmpty()) {
            navigateTo(fxmlPath);
        }
    }

    private void selectNavigationButton(Button button) {
        // Clear active styling from previous selection
        if (activeButton != null) {
            activeButton.getStyleClass().remove("nav-button-active");
        }

        activeButton = button;
        activeButton.getStyleClass().add("nav-button-active");
    }

    private void navigateTo(String fxmlPath) {
        try {
            contentArea.getChildren().clear();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();

            // Link ShellController back to sub-controllers for context updates
            Object controller = loader.getController();
            if (controller instanceof BaseController) {
                ((BaseController) controller).setShellController(this);
            }

            contentArea.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace();
            Label errorLabel = new Label("Failed to load view: " + fxmlPath + "\nReason: " + e.getMessage());
            errorLabel.setStyle("-fx-text-fill: red; -fx-alignment: center;");
            contentArea.getChildren().add(errorLabel);
        }
    }

    /**
     * Updates connection, theme, and task metrics inside the bottom status bar.
     */
    public void updateStatusBar() {
        // 1. Connection Status
        try {
            if (DeviceDetector.isDeviceConnected()) {
                lblConnectionStatus.setText("Status: Connected");
                lblConnectionStatus.setStyle("-fx-text-fill: -accent-color;");
            } else {
                lblConnectionStatus.setText("Status: Disconnected");
                lblConnectionStatus.setStyle("-fx-text-fill: red;");
            }
        } catch (AdbException e) {
            lblConnectionStatus.setText("Status: ADB Error");
            lblConnectionStatus.setStyle("-fx-text-fill: red;");
        }

        // 2. ADB Version
        try {
            String adbVer = AdbManager.getAdbVersion();
            lblAdbVersion.setText("ADB: " + adbVer.replace("Android Debug Bridge version ", ""));
        } catch (Exception e) {
            lblAdbVersion.setText("ADB: Unknown");
        }

        // 3. Background tasks (static for now, dynamic in future tasks)
        lblBackgroundTasks.setText("Tasks: 0 Active");

        // 4. Current Theme name
        String themeName = ThemeManager.getActiveTheme().toString();
        lblActiveTheme.setText("Theme: " + themeName.charAt(0) + themeName.substring(1).toLowerCase());
    }

    /**
     * Public accessor to retrieve the main command palette search query.
     */
    public String getSearchQuery() {
        return searchField.getText();
    }
}
