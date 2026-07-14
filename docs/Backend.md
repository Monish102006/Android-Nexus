# Android Nexus — Backend Service Documentation

This document describes the frozen Backend API, services, data models, and command parsers.

---

## 1. Frozen API Contract v1.0

The backend controller facades expose these operations. These signatures are frozen to ensure stability.

### `DeviceController`
- `getDevice()`: Retrieves basic hardware parameters and cached capability metrics.
- `getScreenResolution()`: Queries the device screen resolution.
- `getStorageInfo()`: Returns the total and available blocks on `/data`.
- `lock()`: Simulates a power button press.
- `home()`, `back()`, `recentApps()`, `notifications()`, `quickSettings()`: Key event navigations.
- `startRecording()`, `stopRecording()`: Starts/stops screen records.
- `volumeUp()`, `volumeDown()`, `mute()`: Triggers audio control commands.

### `FileController`
- `listFiles(String path)`: Returns files and directories.
- `getFileDetails(String path)`: Returns detailed file metadata.
- `downloadFile(String remote, String local)`: Transfers files/folders from device to PC.
- `uploadFile(String local, String remote)`: Transfers files/folders from PC to device.
- `createFolder(String path)`: Creates folder directories.
- `deleteFile(String path)`: Removes file/folder.
- `renameFile(String path, String newName)`: Renames remote entities.
- `copyFile(String src, String dst)`, `moveFile(String src, String dst)`: Copies/moves directory structures.
- `searchFiles(String dir, String query)`: Recursively finds files matching query.

### `ApplicationController`
- `getInstalledApplications()`: Lists all applications.
- `getUserApplications()`: Lists user-installed packages only.
- `getApplicationDetails(String packageName)`: Queries package version, SDK limits, and installer details.
- `installApplication(String localApk)`: Sideloads an APK.
- `uninstallApplication(String package)`: Uninstalls a package.
- `launchApplication(String package)`: Boot launcher activity.
- `forceStopApplication(String package)`: Stops app processes.
- `clearApplicationData(String package)`: Resets app cache and databases.
- `extractApk(String package, String destDir)`: Pulls APK to local machine.

### `NotificationController`
- `getActiveNotifications()`: Reads status bar notifications.
- `getNotificationsFiltered(String type)`: Reads notifications filtered by type/package.
- `dismissNotification(String key)`: Cancels notification.
- `dismissAllNotifications()`: Cancels all swipable notifications.
- `startMonitoring(listener, intervalMs)`: Starts background polling monitor.
- `stopMonitoring()`: Stops active polling.

---

## 2. Stateless Parsers (Rule 5 Compliance)

All raw command output parsing is delegated to stateless parsers:
- **`BatteryParser`**: Parses `dumpsys battery` outputs to return battery level percentage.
- **`AndroidFileParser`**: Translates `ls -l` lines into structured metadata, resolving symlinks, permissions, sizes, and timestamps.
- **`PackageParser`**: Formats package details from `pm list` and extracts versions, minSdk, and targetSdk from `dumpsys package`.
- **`NotificationParser`**: Iterates through `dumpsys notification --noredact` logs to map keys, content, clearable tags, and local timestamps.
- **`StorageParser`**: Formats partition blocks from `df /data` into human-readable capacities.

---

## 3. Checked Exception Hierarchy

String-based error messages are replaced with structured exceptions:
- **`AdbException`**: Base exception class containing command exit code and stderr outputs.
- **`DeviceNotFoundException`**: Thrown if no device is connected.
- **`CommandTimeoutException`**: Thrown if a command execution hangs beyond limits.
