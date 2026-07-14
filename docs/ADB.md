# Android Nexus — ADB Integration & Execution

This document details low-level communication with the Android Debug Bridge (ADB) daemon and subprocess management.

---

## 1. Process Stream Deadlocks & Solutions

When executing terminal commands (such as `dumpsys` which return huge amounts of text), operating systems block process execution once the OS stream pipe buffers fill up.
If Java blocks waiting for `process.waitFor()` without continuously emptying the stdout and stderr streams, **the subprocess deadlocks**.

### CommandExecutor Thread Model
`CommandExecutor` spawns background daemon reader threads for each process:
1. **Stdout thread**: Empties stdout stream and appends it to a `StringBuilder`.
2. **Stderr thread**: Empties stderr stream and buffers it to track diagnostic errors.
3. **Timeout watchdog**: Enforces execution limits (default 30 seconds for query calls, up to 5 minutes for file push/pull operations).

---

## 2. Dynamic Device Capabilities Detection

Device capabilities vary significantly across OEMs. Rather than executing unstable commands or assuming support, `DeviceService.getDeviceInformation()` queries and caches capabilities at startup:

| Capability Checked | Command executed to verify | Action taken if unsupported |
| :--- | :--- | :--- |
| **Notification Access** | `dumpsys notification --short` | Disables Active Notifications UI view. |
| **Screen Recording** | `which screenrecord` | Disables video recording / mirror panel buttons. |
| **Camera Flashlight** | `cmd package list services` (look for `flashlight` service registration) | Disables flashlight quick actions. |
| **Media Control** | `cmd package list services` (look for `media_session` service registration) | Disables audio controller volume buttons. |

---

## 3. Safe Notification Dismissal

Historically, developers dismiss notifications via ADB service transactions (e.g. `service call notification 1 ...`). However, transaction IDs change frequently across major Android updates and OEM shells (Samsung, Xiaomi, Motorola).
Android Nexus resolves this by executing the standard CLI command available on Android 8.0+:
```bash
adb shell cmd notification dismiss <notification_key>
```
This is fully portable, safe, and supported across all modern devices (including the test Motorola Moto G64).

---

## 4. Live Screen Mirroring (`scrcpy`)

Live mirroring is delegated to the open-source tool **`scrcpy`**.
- The path is configured under `Constants.SCRCPY_PATH`.
- When triggered, `ScreenshotUiService` attempts to spawn `scrcpy` asynchronously:
  - Check if `Constants.SCRCPY_PATH` exists on the host.
  - If not, check if `scrcpy` is available globally in the OS path environment.
  - If both fail, it returns an exception detailing installation steps.
- The process is spawned in the background without blocking the JavaFX thread.
