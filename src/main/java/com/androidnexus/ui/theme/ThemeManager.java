package com.androidnexus.ui.theme;

import javafx.collections.ObservableList;
import javafx.scene.Scene;

/**
 * Manages runtime theme selection and loads active stylesheets to scenes.
 */
public class ThemeManager {
    private static ThemeType activeTheme = ThemeType.DARK;
    
    public static void applyTheme(Scene scene, ThemeType type) {
        activeTheme = type;
        ObservableList<String> stylesheets = scene.getStylesheets();
        stylesheets.clear();
        
        // Always load structural base styles first
        stylesheets.add(ThemeManager.class.getResource("/css/base.css").toExternalForm());
        
        // Append theme-specific variable overrides
        String themeFile = switch (type) {
            case LIGHT -> "/css/light.css";
            case DARK -> "/css/dark.css";
            case DRACULA -> "/css/dracula.css";
        };
        stylesheets.add(ThemeManager.class.getResource(themeFile).toExternalForm());
    }

    public static ThemeType getActiveTheme() {
        return activeTheme;
    }
}
