# Android Nexus — Testing Strategy & Guidelines

This document details the test framework configuration, parser mock testing, and dynamic integration testing.

---

## 1. Testing Framework Stack

Android Nexus utilizes:
- **JUnit 5 (Jupiter)** for test assertions.
- **Maven Surefire Plugin** for execution.
- **Assumptions** (`org.junit.jupiter.api.Assumptions.assumeTrue`) to filter integration tests depending on device connection.

---

## 2. Stateless Parser Testing (Unit Tests)

All console-output parsers are completely stateless and tested without active device connections. We mock command output logs inside unit tests:

- **`BatteryParserTest`**: Feeds mock outputs of `dumpsys battery` to assert correct extraction of levels.
- **`AndroidFileParserTest`**: Feeds mock 8-column listing lines to test directory mapping, file extensions, human-readable size labels, and symlink targets.
- **`PackageParserTest`**: Feeds mock package lists (`pm list packages -f`) to verify package-type mapping (`USER`/`SYSTEM`).
- **`NotificationParserTest`**: Feeds mock dumpsys notification lines to verify hex flag parsing (clearable vs ongoing).
- **`StorageParserTest`**: Feeds mock outputs of `df` to verify total/free capacities.

---

## 3. Dynamic Device Integration Testing

Integration tests run directly on the connected device (e.g. Motorola Moto G64). If no device is connected, the setup method skips execution silently, allowing build pipelines (`mvn test`) to pass cleanly on headless servers.

```java
@BeforeEach
public void setUp() {
    boolean isConnected = false;
    try {
        isConnected = DeviceDetector.isDeviceConnected();
    } catch (AdbException e) {
        // ignore
    }
    Assumptions.assumeTrue(isConnected, "ADB device not connected. Skipping integration tests.");
}
```

### File Manager Lifecycle Test
- **Location**: `FileControllerIntegrationTest`
- **Sequence**:
  1. Create folder `/sdcard/NexusTest`
  2. Upload local dummy file
  3. Verify file exists
  4. Download dummy file back to PC
  5. Rename dummy file
  6. Copy / move files
  7. Search for files matching naming criteria
  8. Delete `/sdcard/NexusTest` and confirm deletion
  9. Assert zero remnants left

### Application Manager Test
- **Location**: `ApplicationControllerIntegrationTest`
- **Sequence**:
  1. Fetch all packages, find `com.android.settings`
  2. Extract settings APK and download to PC
  3. Verify downloaded file size > 0
  4. Clean up downloaded local file

### Notification Manager Lifecycle Test
- **Location**: `NotificationControllerIntegrationTest`
- **Sequence**:
  1. Post a test notification: `cmd notification post -t "Test Title" "TestTag" "TestBody"`
  2. Read active notifications list and find item
  3. Verify message content matches
  4. Call `dismissNotification(key)`
  5. Confirm notification has disappeared
