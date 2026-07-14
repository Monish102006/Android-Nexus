# Android Nexus

Android Nexus is an enterprise-grade desktop device management suite designed for communicating with Android devices via ADB (Android Debug Bridge). It provides file management, application lifecycle control, notification synchronization, and screenshot control in a premium, dynamically themed JavaFX interface.

---

## 🚀 Features Showcase

### 1. Device Dashboard (Home)
- Real-time hardware specifications (model, manufacturer, Android API level, screen resolution).
- Live battery temperature, health, and capacity tracking.
- Dynamic partition storage meter displaying total and free space on `/data`.
- Cached OEM capability badge indicators.

### 2. File Explorer
- Split-pane navigation enclosing folder directory trees and files grid.
- **Lazy-Loaded Trees**: Folders are queried and populated only upon item expansion to avoid blocking storage latency.
- Comprehensive file toolbars supporting upload/download transfers, creation, renaming, and recursive deletions.

### 3. Application Manager
- installed packages list featuring type categorization (`USER` / `SYSTEM`) with emerald green/orange badge pills.
- Support for package launches, force stops, caches/database resets, APK extractions, uninstalls, and sideload APK installations.

### 4. Active Notification Panel
- Status bar notification monitoring showing package details, messages, and local system timestamps.
- Non-blocking live background polling thread sync with settings-defined sync rates.
- Row-level dismiss buttons and "Dismiss All Clearable" buttons utilizing standard `cmd notification` APIs.

### 5. Screenshot & Mirroring
- display previews, device volume adjustments, lock key triggers, and background thread execution for live **`scrcpy`** mirroring.

### 6. Dynamic Theme system
- Real-time palette updates (Dark, Light, Dracula cyberpunk theme) utilizing structural CSS variables.
- Crisp vector icon path renderings styled directly via stylesheet variables.

---

## 🏛️ System Architecture

Android Nexus follows a strict layered decoupling model. View layers communicate with low-level ADB executors exclusively through intermediate services.

```mermaid
graph TD
    subgraph Presentation Layer
        FXML[FXML Layouts] --> Controller[View Controllers]
    end

    subgraph UI Service Layer
        Controller --> UiService[UI Services]
        UiService --> Executor[UiThreadExecutor]
    end

    subgraph Backend Facade Layer
        UiService --> BackendController[Backend Controllers]
    end

    subgraph Service & Parser Layer
        BackendController --> Service[Services]
        Service --> Parser[Parsers]
    end

    subgraph Infrastructure Layer
        Service --> CmdExec[CommandExecutor]
        CmdExec --> Proc[ProcessBuilder]
    end

    subgraph On-Device Execution
        Proc --> ADB[ADB Daemon]
        ADB --> Android[Android System]
    end

    style Presentation Layer fill:#2d3748,stroke:#4a5568,stroke-width:2px;
    style UI Service Layer fill:#1a202c,stroke:#2d3748,stroke-width:2px;
    style Backend Facade Layer fill:#2b6cb0,stroke:#3182ce,stroke-width:2px;
    style Service & Parser Layer fill:#2c5282,stroke:#2b6cb0,stroke-width:2px;
    style Infrastructure Layer fill:#276749,stroke:#2f855a,stroke-width:2px;
    style On-Device Execution fill:#744210,stroke:#975a16,stroke-width:2px;
```

For a detailed review of the layering boundaries and concurrency model, see [Architecture Documentation](docs/Architecture.md).

---

## 📂 Project Structure

```
Android-Nexus/
├── docs/                             # Architecture & developer docs
│   ├── Architecture.md
│   ├── Backend.md
│   ├── UI.md
│   ├── ADB.md
│   ├── Testing.md
│   ├── DeveloperGuide.md
│   └── Contributing.md
│
├── src/main/
│   ├── java/com/androidnexus/
│   │   ├── adb/                      # Low-level process executors
│   │   ├── controller/               # Backend facade layer
│   │   ├── exception/                # Checked exceptions tree
│   │   ├── model/                    # Domain POJOs and records
│   │   ├── parser/                   # Stateless text parsers
│   │   ├── service/                  # Business logic services
│   │   ├── ui/                       # JavaFX Views and Controllers
│   │   └── utils/                    # Shared utilities
│   │
│   └── resources/
│       ├── fxml/                     # XML view layouts
│       └── css/                      # CSS stylesheets and themes
│
└── pom.xml                           # Maven dependencies
```

---

## 🛠️ Getting Started

### Prerequisites
- **JDK 21 (LTS)** or higher.
- **Maven 3.8+** installed.
- **Android Platform Tools (ADB)** added to your environment `PATH`.
- An Android device with **USB Debugging** enabled.
- **scrcpy** (optional) for screen mirroring.

### Build and Test
```bash
# Compile project
mvn clean compile

# Run all unit and integration tests
mvn test
```

### Launch UI
```bash
mvn javafx:run
```

---

## 🗺️ Project Roadmap

- [x] **Module 5**: Asynchronous File Manager Backend.
- [x] **Module 6**: APK Package Manager Backend.
- [x] **Module 7**: Notification Status Monitor & Capabilities Detection.
- [x] **Module 8**: Dynamic Theme JavaFX Desktop UI.
- [ ] **Module 9**: Local Ollama AI System Assistant.
- [ ] **Module 10**: Recent Files & Bookmark Favorites features.

---

## 📄 License
This project is licensed under the MIT License - see the LICENSE file for details.
