# Android Nexus — JavaFX UI Documentation

This document describes the JavaFX Desktop UI implementation details, theme manager, FXML subviews, and UI layout specifications.

---

## 1. UI Components & Controllers

The UI structure consists of a single Shell window enclosing dynamic pages swapped inside a `StackPane` content area.

| Screen FXML | Controller Class | Description |
| :--- | :--- | :--- |
| `shell.fxml` | `ShellController` | Nav sidebar, top command search, bottom status bar. |
| `home.fxml` | `HomeController` | Live device properties, battery levels, storage meters, capability badges. |
| `file_explorer.fxml` | `FileExplorerController` | SplitPane enclosing lazy-loaded directories tree and file lists. |
| `app_manager.fxml` | `AppManagerController` | Installed application grid with system/user badge pills and sideload options. |
| `notification_panel.fxml` | `NotificationController` | Active notifications grid with live polling sync triggers. |
| `screenshot_viewer.fxml` | `ScreenshotController` | Large screenshot preview image, media volume sliders, and mirror triggers. |
| `settings.fxml` | `SettingsController` | Sync rate configuration, theme selections, save target directory pickers. |

---

## 2. Dynamic Theme System

The appearance of Android Nexus is styled using **CSS variables** defined in theme-specific override sheets:
- `base.css`: Establishes the layout structure (background, padding, margins, sidebar, button behaviors).
- `dark.css`: Override variables defining dark theme colors.
- `light.css`: Override variables defining light theme colors.
- `dracula.css`: High-contrast cyberpunk palette overrides.

### Applying Theme Dynamically
`ThemeManager.applyTheme(Scene scene, ThemeType type)` clears stylesheets, loads `base.css` first to establish structure, and appends the selected theme-specific color variable overrides sheet.

---

## 3. Resolution-Independent Vector Icons

Sidebar navigation items are rendered using SVG Paths via **`SvgIconFactory`** rather than PNG files. This yields benefits:
1. **Crisp Scaling**: Icons do not blur or pixelate on high-DPI screens or window resizing.
2. **Color Shifting**: Icons use the CSS style `-fx-fill: -text-secondary;`, meaning their color shifts automatically during theme toggles.

---

## 4. Context-Aware Global Search

The global search input field situated at the top right of the application shell connects directly to whatever subview controller is currently active.
- Sub-controllers implement the `onGlobalSearch(String query)` hook.
- When query characters are typed, the text listener in the `ShellController` calls the active view hook:
  - **Home**: (No-op).
  - **File Explorer**: Dynamically filters currently listed folder contents.
  - **Applications**: Filters applications by package name or label matching.
  - **Notifications**: Filters messages containing the query phrase.
- This creates a cohesive command palette behavior.
