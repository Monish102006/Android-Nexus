# Android Nexus — Developer Setup Guide

This document describes the environment setup, compilation procedures, and coding guidelines for contributing to Android Nexus.

---

## 1. Prerequisites

Ensure your development machine has the following tools installed:
1. **JDK 21 (LTS)**: Required for compiling Java 21 features (records, pattern matching).
2. **Maven 3.8+**: Dependency manager.
3. **Android Platform Tools (ADB)**: Added to your system environment `PATH` variables.
4. **scrcpy** (Optional): Required for live mirroring support.

---

## 2. Setting Up the Project

### Clone & Build
```bash
git clone https://github.com/Monish102006/Android-Nexus.git
cd Android-Nexus
mvn clean compile
```

### Running Tests
To run all parser unit tests and device integration tests:
```bash
mvn test
```
*Note: Ensure an Android device is connected via USB debugging to run integration tests.*

### Running the JavaFX App
To launch the desktop interface:
```bash
mvn javafx:run
```

---

## 3. Creating a New Feature Module

When adding a new capability (e.g. Screen Recorder, Logcat viewer), follow these development rules:

1. **Write the Parser first**:
   - Save in `com.androidnexus.parser`.
   - Keep it 100% static and stateless.
   - Immediately add a JUnit test class in `src/test/java/com/androidnexus/parser/`.
2. **Create the Backend Service**:
   - Save in `com.androidnexus.service`.
   - Execute ADB commands strictly using the `CommandExecutor` boundary.
3. **Create the Controller Facade**:
   - Expose the method inside a backend controller in `com.androidnexus.controller`.
4. **Write the Integration Test**:
   - Create a controller integration test class in `src/test/java/com/androidnexus/controller/`.
   - Wrap the test lifecycle using assumptions to check if a device is active.
5. **Implement the UI Service**:
   - Create a background `Task` scheduler wrapper inside `com.androidnexus.ui.service`.
6. **Implement UI & FXML**:
   - Create FXML under `/resources/fxml/` and link to a new controller class in `com.androidnexus.ui.controller`.
   - Bind search query filters inside the global command search hook.
7. **Commit & Milestone**:
   - Verify code compiles cleanly and commit under master.
   - Tag the release version.
