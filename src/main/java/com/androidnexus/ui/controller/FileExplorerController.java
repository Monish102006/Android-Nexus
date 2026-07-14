package com.androidnexus.ui.controller;

import com.androidnexus.model.AndroidFile;
import com.androidnexus.ui.service.FileUiService;
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
import java.util.List;
import java.util.Optional;
import java.util.Stack;

public class FileExplorerController extends BaseController {

    @FXML private Button btnBack;
    @FXML private Button btnHomeDir;
    @FXML private TextField txtPath;
    @FXML private Button btnRefresh;

    @FXML private TreeView<String> treeDir;
    @FXML private TableView<AndroidFile> tableFiles;
    @FXML private TableColumn<AndroidFile, String> colName;
    @FXML private TableColumn<AndroidFile, String> colSize;
    @FXML private TableColumn<AndroidFile, String> colDate;

    @FXML private Button btnDownload;
    @FXML private Button btnUpload;
    @FXML private Button btnNewFolder;
    @FXML private Button btnRename;
    @FXML private Button btnDelete;

    @FXML private HBox statusOverlay;
    @FXML private Label lblStatus;

    private String currentPath = "/sdcard";
    private final Stack<String> pathHistory = new Stack<>();

    @FXML
    public void initialize() {
        // 1. Configure Table Columns
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        
        // Formatter to display folder as "--" and files as human-readable sizes
        colSize.setCellValueFactory(cellData -> {
            AndroidFile file = cellData.getValue();
            if (file.isDirectory()) {
                return new javafx.beans.property.SimpleStringProperty("--");
            }
            return new javafx.beans.property.SimpleStringProperty(file.getHumanReadableSize());
        });

        colDate.setCellValueFactory(cellData -> {
            AndroidFile file = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(file.getLastModified());
        });

        // 2. Setup Double-Click Navigation on Table Rows
        tableFiles.setRowFactory(tv -> {
            TableRow<AndroidFile> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    AndroidFile rowData = row.getItem();
                    if (rowData.isDirectory()) {
                        navigateToPath(rowData.getPath(), true);
                    }
                }
            });
            return row;
        });

        // 3. Initialize Lazy-Loading Tree View
        setupTreeView();

        // 4. Initial Navigation
        navigateToPath(currentPath, false);
    }

    private void setupTreeView() {
        TreeItem<String> rootItem = new TreeItem<>("Device");
        rootItem.setExpanded(true);
        treeDir.setRoot(rootItem);
        treeDir.setShowRoot(false);

        // Load root folders
        addLazyDirectoryNode(rootItem, "sdcard", "/sdcard");
    }

    private void addLazyDirectoryNode(TreeItem<String> parent, String name, String absolutePath) {
        TreeItem<String> item = new TreeItem<>(name) {
            private boolean isFirstTime = true;

            @Override
            public boolean isLeaf() {
                return false; // Force expansion arrow to show
            }

            @Override
            public ObservableList<TreeItem<String>> getChildren() {
                if (isFirstTime) {
                    isFirstTime = false;
                    triggerLazyLoad(this, absolutePath);
                }
                return super.getChildren();
            }
        };
        parent.getChildren().add(item);
    }

    private void triggerLazyLoad(TreeItem<String> item, String absolutePath) {
        item.getChildren().add(new TreeItem<>("Loading..."));

        Task<List<AndroidFile>> listTask = FileUiService.createListFilesTask(absolutePath);
        listTask.setOnSucceeded(e -> {
            item.getChildren().clear();
            List<AndroidFile> files = listTask.getValue();
            for (AndroidFile f : files) {
                if (f.isDirectory()) {
                    addLazyDirectoryNode(item, f.getName(), f.getPath());
                }
            }
        });
        listTask.setOnFailed(e -> {
            item.getChildren().clear();
            item.getChildren().add(new TreeItem<>("Error loading"));
        });
        UiThreadExecutor.runInBackground(listTask);
    }

    private void navigateToPath(String path, boolean recordHistory) {
        showStatus("Reading directory...");
        btnRefresh.setDisable(true);

        Task<List<AndroidFile>> listTask = FileUiService.createListFilesTask(path);

        listTask.setOnSucceeded(e -> {
            if (recordHistory && !path.equals(currentPath)) {
                pathHistory.push(currentPath);
            }
            currentPath = path;
            txtPath.setText(currentPath);
            
            List<AndroidFile> files = listTask.getValue();
            tableFiles.setItems(FXCollections.observableArrayList(files));

            hideStatus();
            btnRefresh.setDisable(false);
            btnBack.setDisable(pathHistory.isEmpty());
        });

        listTask.setOnFailed(e -> {
            hideStatus();
            btnRefresh.setDisable(false);
            showErrorAlert("Navigation Error", "Could not read path: " + path);
        });

        UiThreadExecutor.runInBackground(listTask);
    }

    @FXML
    private void handleBack(ActionEvent event) {
        if (!pathHistory.isEmpty()) {
            String prevPath = pathHistory.pop();
            navigateToPath(prevPath, false);
        }
    }

    @FXML
    private void handleHome(ActionEvent event) {
        navigateToPath("/sdcard", true);
    }

    @FXML
    private void handlePathEnter(ActionEvent event) {
        String enteredPath = txtPath.getText().trim();
        if (!enteredPath.isEmpty()) {
            navigateToPath(enteredPath, true);
        }
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        navigateToPath(currentPath, false);
    }

    @FXML
    private void handleDownload(ActionEvent event) {
        AndroidFile selected = tableFiles.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarningAlert("No Selection", "Please select a file or folder to download.");
            return;
        }

        // 1. Choose local directory
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("Select Download Destination");
        File targetDir = dirChooser.showDialog(tableFiles.getScene().getWindow());
        if (targetDir == null) {
            return;
        }

        showStatus("Downloading: " + selected.getName() + "...");
        
        Task<Void> dlTask = FileUiService.createDownloadTask(selected.getPath(), targetDir);
        dlTask.setOnSucceeded(e -> {
            hideStatus();
            showInfoAlert("Download Finished", "Successfully downloaded: " + selected.getName());
        });
        dlTask.setOnFailed(e -> {
            hideStatus();
            showErrorAlert("Download Failed", "Failed to download " + selected.getName() + ": " + dlTask.getException().getMessage());
        });

        UiThreadExecutor.runInBackground(dlTask);
    }

    @FXML
    private void handleUpload(ActionEvent event) {
        // 1. Select local file to push
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File to Upload");
        File localFile = fileChooser.showOpenDialog(tableFiles.getScene().getWindow());
        if (localFile == null) {
            return;
        }

        showStatus("Uploading " + localFile.getName() + "...");

        Task<Void> upTask = FileUiService.createUploadTask(localFile, currentPath);
        upTask.setOnSucceeded(e -> {
            hideStatus();
            showInfoAlert("Upload Finished", "Successfully uploaded " + localFile.getName());
            handleRefresh(null); // Reload view
        });
        upTask.setOnFailed(e -> {
            hideStatus();
            showErrorAlert("Upload Failed", "Failed to upload " + localFile.getName() + ": " + upTask.getException().getMessage());
        });

        UiThreadExecutor.runInBackground(upTask);
    }

    @FXML
    private void handleNewFolder(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog("New Folder");
        dialog.setTitle("New Folder");
        dialog.setHeaderText("Create a folder in " + currentPath);
        dialog.setContentText("Folder Name:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().trim().isEmpty()) {
            String folderName = result.get().trim();
            String fullPath = currentPath + "/" + folderName;

            showStatus("Creating folder...");

            Task<Void> mkdirTask = FileUiService.createCreateFolderTask(fullPath);
            mkdirTask.setOnSucceeded(e -> {
                hideStatus();
                handleRefresh(null);
            });
            mkdirTask.setOnFailed(e -> {
                hideStatus();
                showErrorAlert("Folder Creation Failed", mkdirTask.getException().getMessage());
            });

            UiThreadExecutor.runInBackground(mkdirTask);
        }
    }

    @FXML
    private void handleRename(ActionEvent event) {
        AndroidFile selected = tableFiles.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarningAlert("No Selection", "Please select a file or folder to rename.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog(selected.getName());
        dialog.setTitle("Rename File");
        dialog.setHeaderText("Rename: " + selected.getName());
        dialog.setContentText("New Name:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().trim().isEmpty()) {
            String newName = result.get().trim();
            showStatus("Renaming...");

            Task<Void> renameTask = FileUiService.createRenameTask(selected.getPath(), newName);
            renameTask.setOnSucceeded(e -> {
                hideStatus();
                handleRefresh(null);
            });
            renameTask.setOnFailed(e -> {
                hideStatus();
                showErrorAlert("Rename Failed", renameTask.getException().getMessage());
            });

            UiThreadExecutor.runInBackground(renameTask);
        }
    }

    @FXML
    private void handleDelete(ActionEvent event) {
        AndroidFile selected = tableFiles.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarningAlert("No Selection", "Please select a file or folder to delete.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Delete: " + selected.getName());
        alert.setContentText("Are you sure you want to permanently delete this file/folder?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            showStatus("Deleting...");

            Task<Void> delTask = FileUiService.createDeleteTask(selected.getPath());
            delTask.setOnSucceeded(e -> {
                hideStatus();
                handleRefresh(null);
            });
            delTask.setOnFailed(e -> {
                hideStatus();
                showErrorAlert("Deletion Failed", delTask.getException().getMessage());
            });

            UiThreadExecutor.runInBackground(delTask);
        }
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
