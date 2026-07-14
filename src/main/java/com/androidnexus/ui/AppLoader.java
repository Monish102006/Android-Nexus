package com.androidnexus.ui;

import com.androidnexus.ui.theme.ThemeManager;
import com.androidnexus.ui.theme.ThemeType;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main JavaFX Application loader.
 *
 * Spawns the JavaFX lifecycle, loads the primary window FXML layout (shell.fxml),
 * sets up the Scene, and applies the default Theme (Dark).
 */
public class AppLoader extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the main window shell FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/shell.fxml"));
        Parent root = loader.load();

        // Create the scene with default dimensions (1200x800)
        Scene scene = new Scene(root, 1200, 800);

        // Apply default Dark theme
        ThemeManager.applyTheme(scene, ThemeType.DARK);

        primaryStage.setTitle("Android Nexus");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(960);
        primaryStage.setMinHeight(640);
        primaryStage.show();
    }

    /**
     * Helper launch method to trigger application start.
     */
    public static void launchApp(String[] args) {
        launch(args);
    }
}
