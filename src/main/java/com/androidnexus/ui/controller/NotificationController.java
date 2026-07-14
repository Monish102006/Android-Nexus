package com.androidnexus.ui.controller;

import com.androidnexus.model.Notification;
import com.androidnexus.service.NotificationService;
import com.androidnexus.ui.service.NotificationUiService;
import com.androidnexus.ui.utils.UiThreadExecutor;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import java.util.ArrayList;
import java.util.List;

public class NotificationController extends BaseController {

    @FXML private Button btnRefresh;
    @FXML private ToggleButton tbAll;
    @FXML private ToggleButton tbWhatsApp;
    @FXML private ToggleButton tbGmail;
    @FXML private ToggleButton tbTelegram;
    @FXML private ToggleButton tbSystem;
    @FXML private ToggleGroup notificationFilters;

    @FXML private TableView<Notification> tableNotifications;
    @FXML private TableColumn<Notification, String> colPackage;
    @FXML private TableColumn<Notification, String> colTitle;
    @FXML private TableColumn<Notification, String> colText;
    @FXML private TableColumn<Notification, String> colTime;
    @FXML private TableColumn<Notification, Notification> colAction;

    @FXML private CheckBox cbLiveSync;
    @FXML private Button btnDismissAll;

    @FXML private HBox statusOverlay;
    @FXML private Label lblStatus;

    private final List<Notification> allNotifications = new ArrayList<>();
    private boolean isMonitoring = false;

    // Polling interval config (defaults to 2000, can be dynamically configured in Settings later)
    private static int syncIntervalMs = 2000;

    public static void setSyncIntervalMs(int ms) {
        if (ms >= 500) {
            syncIntervalMs = ms;
        }
    }

    public static int getSyncIntervalMs() {
        return syncIntervalMs;
    }

    @FXML
    public void initialize() {
        // 1. Bind Table Columns
        colPackage.setCellValueFactory(new PropertyValueFactory<>("packageName"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colText.setCellValueFactory(new PropertyValueFactory<>("text"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("timestamp"));

        // 2. Custom action cell rendering (shows "Dismiss" button if clearable)
        colAction.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue()));
        colAction.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Notification item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    if (item.isClearable()) {
                        Button dismissBtn = new Button("Dismiss");
                        dismissBtn.getStyleClass().add("nav-button-active");
                        dismissBtn.setStyle("-fx-font-size: 11px; -fx-padding: 3 8 3 8;");
                        dismissBtn.setOnAction(event -> dismissSingleNotification(item));
                        setGraphic(dismissBtn);
                        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    } else {
                        Label ongoingLabel = new Label("Ongoing");
                        ongoingLabel.setStyle("-fx-text-fill: -text-secondary; -fx-font-size: 11px;");
                        setGraphic(ongoingLabel);
                        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    }
                }
            }
        });

        // 3. Add Filter Listeners
        notificationFilters.selectedToggleProperty().addListener((obs, oldVal, newVal) -> applyFilters());

        // 4. Initial load
        loadNotifications();
    }

    private void loadNotifications() {
        showStatus("Fetching active notifications...");
        btnRefresh.setDisable(true);

        Task<List<Notification>> fetchTask = NotificationUiService.createFetchNotificationsTask();

        fetchTask.setOnSucceeded(e -> {
            allNotifications.clear();
            allNotifications.addAll(fetchTask.getValue());

            applyFilters();
            hideStatus();
            btnRefresh.setDisable(false);
        });

        fetchTask.setOnFailed(e -> {
            hideStatus();
            btnRefresh.setDisable(false);
            showErrorAlert("Fetch Failed", "Could not fetch notifications: " + fetchTask.getException().getMessage());
        });

        UiThreadExecutor.runInBackground(fetchTask);
    }

    private void applyFilters() {
        Toggle selectedToggle = notificationFilters.getSelectedToggle();
        String category = "";

        if (selectedToggle == tbWhatsApp) {
            category = "whatsapp";
        } else if (selectedToggle == tbGmail) {
            category = "gmail";
        } else if (selectedToggle == tbTelegram) {
            category = "telegram";
        } else if (selectedToggle == tbSystem) {
            category = "system";
        }

        List<Notification> filtered;
        if (category.isEmpty()) {
            filtered = allNotifications;
        } else {
            filtered = NotificationService.filterNotifications(allNotifications, category);
        }

        tableNotifications.setItems(FXCollections.observableArrayList(filtered));
    }

    private void dismissSingleNotification(Notification notif) {
        showStatus("Dismissing notification...");
        
        Task<Void> dismissTask = NotificationUiService.createDismissNotificationTask(notif.getKey());
        
        dismissTask.setOnSucceeded(e -> {
            hideStatus();
            // Remove locally to update list instantly before next poll
            allNotifications.remove(notif);
            applyFilters();
        });
        
        dismissTask.setOnFailed(e -> {
            hideStatus();
            showErrorAlert("Dismiss Failed", dismissTask.getException().getMessage());
        });

        UiThreadExecutor.runInBackground(dismissTask);
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        loadNotifications();
    }

    @FXML
    private void handleLiveSyncToggle(ActionEvent event) {
        if (cbLiveSync.isSelected()) {
            startSync();
        } else {
            stopSync();
        }
    }

    private void startSync() {
        if (isMonitoring) {
            return;
        }
        isMonitoring = true;
        showStatus("Live sync enabled");

        NotificationUiService.startLiveSync(new NotificationService.NotificationListener() {
            @Override
            public void onNotificationsUpdated(List<Notification> activeNotifications) {
                Platform.runLater(() -> {
                    allNotifications.clear();
                    allNotifications.addAll(activeNotifications);
                    applyFilters();
                    hideStatus();
                });
            }

            @Override
            public void onError(Throwable throwable) {
                Platform.runLater(() -> {
                    lblStatus.setText("Sync error: " + throwable.getMessage());
                });
            }
        }, syncIntervalMs);
    }

    private void stopSync() {
        if (!isMonitoring) {
            return;
        }
        isMonitoring = false;
        NotificationUiService.stopLiveSync();
        hideStatus();
    }

    @FXML
    private void handleDismissAll(ActionEvent event) {
        showStatus("Dismissing all clearable entries...");

        Task<Void> dismissAllTask = NotificationUiService.createDismissAllNotificationsTask();
        
        dismissAllTask.setOnSucceeded(e -> {
            hideStatus();
            // Instant local clear for clearable entries
            allNotifications.removeIf(Notification::isClearable);
            applyFilters();
        });

        dismissAllTask.setOnFailed(e -> {
            hideStatus();
            showErrorAlert("Dismiss All Failed", dismissAllTask.getException().getMessage());
        });

        UiThreadExecutor.runInBackground(dismissAllTask);
    }

    @Override
    public void cleanup() {
        // Lifecycle Hook: Make sure to stop background polling loops when view is closed!
        stopSync();
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
