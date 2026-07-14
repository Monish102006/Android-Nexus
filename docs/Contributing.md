# Contributing to Android Nexus

Thank you for your interest in contributing to Android Nexus! Please read through these guidelines to maintain project architecture and code quality standards.

---

## 1. Code Style and Conventions

- **Clean Architecture**: Decouple layers. FXML views/controllers must never invoke CommandExecutor or ADB processes directly. Use UI Services and background Executors.
- **Rule 5 Compliance**: No string manipulation or console parsing is permitted inside Services or Controllers. Dedicated parser classes must carry all text processing.
- **Exception Hierarchy**: Use checked exceptions (`AdbException`, `DeviceNotFoundException`, `CommandTimeoutException`) rather than generic string errors.
- **File Links & Schematics**: Maintain package documentation `package-info.java` files when creating new layers.

---

## 2. Commit Message Guidelines

We follow the **Conventional Commits** specification:

```
<type>(<scope>): <description>

[optional body]
```

### Types:
- `feat`: A new user-facing feature (e.g., `feat(ui): add notification synchronization checkbox`).
- `fix`: A bug fix (e.g., `fix(audio): correct volume up button keycode mapping`).
- `docs`: Documentation updates.
- `refactor`: Code restructuring without features or fixes.
- `test`: Adding or correcting tests.

---

## 3. Pull Request Checklist

Before submitting a Pull Request, ensure:
1. All code compiles with Java 21: `mvn clean compile` succeeds.
2. All unit tests pass: `mvn test` runs with zero failures.
3. No dependencies cross boundaries (e.g., checking package imports in view controllers).
4. No debug/IDE print statements (use logging facades).
5. Dynamic integration tests are validated on a connected device.
