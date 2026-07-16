package com.androidnexus.parser;

import com.androidnexus.model.Notification;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class NotificationParser {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Parses the output of "adb shell dumpsys notification" into a list of Notification objects.
     */
    public static List<Notification> parse(String dumpsysOutput) {
        List<Notification> notifications = new ArrayList<>();
        if (dumpsysOutput == null || dumpsysOutput.isEmpty()) {
            return notifications;
        }

        String[] lines = dumpsysOutput.split("\n");
        Notification current = null;

        for (String line : lines) {
            String trimmed = line.trim();

            // 1. Detect start of a new NotificationRecord
            if (trimmed.startsWith("NotificationRecord{") || trimmed.startsWith("NotificationRecord(")) {
                if (current != null && current.getKey() != null) {
                    notifications.add(current);
                }
                current = new Notification();
                parseProperties(trimmed, current);
                continue;
            }

            if (current == null) {
                continue;
            }

            // 2. Parse pkg, id, tag details if on a separate line
            if (trimmed.startsWith("pkg=")) {
                parseProperties(trimmed, current);
                continue;
            }

            // 3. Parse flags (for ongoing and clearable status)
            if (trimmed.startsWith("flags=")) {
                String hex = trimmed.substring("flags=".length()).trim();
                try {
                    if (hex.startsWith("0x")) {
                        hex = hex.substring(2);
                    }
                    int flags = Integer.parseInt(hex, 16);
                    
                    // FLAG_ONGOING_EVENT = 0x00000002
                    // FLAG_NO_CLEAR      = 0x00000020
                    boolean ongoing = (flags & 0x02) != 0;
                    boolean noClear = (flags & 0x20) != 0;
                    
                    current.setOngoing(ongoing);
                    current.setClearable(!ongoing && !noClear);
                } catch (Exception e) {
                    current.setClearable(true);
                }
                continue;
            }

            // 4. Parse content properties (title, text, when)
            if (trimmed.startsWith("title=")) {
                current.setTitle(trimmed.substring("title=".length()).trim());
            } else if (trimmed.startsWith("android.title=")) {
                String val = extractStringFromExtras(trimmed, "android.title=");
                if (val != null) {
                    current.setTitle(val);
                }
            } else if (trimmed.startsWith("text=")) {
                current.setText(trimmed.substring("text=".length()).trim());
            } else if (trimmed.startsWith("android.text=")) {
                String val = extractStringFromExtras(trimmed, "android.text=");
                if (val != null) {
                    current.setText(val);
                }
            } else if (trimmed.startsWith("when=")) {
                String whenStr = trimmed.substring("when=".length()).trim();
                try {
                    long timestamp = Long.parseLong(whenStr);
                    if (timestamp > 0) {
                        LocalDateTime dateTime = LocalDateTime.ofInstant(
                                Instant.ofEpochMilli(timestamp), ZoneId.systemDefault()
                        );
                        current.setTimestamp(dateTime.format(DATE_FORMATTER));
                    }
                } catch (NumberFormatException e) {
                    current.setTimestamp(null);
                }
            }
        }

        // Add the last parsed notification
        if (current != null && current.getKey() != null) {
            notifications.add(current);
        }

        // Filter out notifications that don't have basic package or key information
        List<Notification> validNotifications = new ArrayList<>();
        for (Notification n : notifications) {
            if (n.getPackageName() != null && n.getKey() != null) {
                validNotifications.add(n);
            }
        }

        return validNotifications;
    }

    private static void parseProperties(String line, Notification n) {
        String pkg = extractValue(line, "pkg=");
        if (pkg != null) {
            n.setPackageName(pkg);
        }

        String idStr = extractValue(line, "id=");
        if (idStr != null) {
            try {
                n.setId(Integer.parseInt(idStr));
            } catch (NumberFormatException e) {
                // ignore
            }
        }

        String tag = extractValue(line, "tag=");
        if (tag != null) {
            n.setTag("null".equals(tag) ? null : tag);
        }

        String key = extractValue(line, "key=");
        if (key != null) {
            if (key.endsWith(":") || key.endsWith("}") || key.endsWith(")")) {
                key = key.substring(0, key.length() - 1);
            }
            n.setKey(key);
        }
    }

    private static String extractValue(String line, String prefix) {
        int index = line.indexOf(prefix);
        if (index == -1) {
            return null;
        }
        int start = index + prefix.length();
        int end = start;
        while (end < line.length()) {
            char c = line.charAt(end);
            if (c == ' ' || c == '}' || c == ')' || c == '\r' || c == '\n') {
                break;
            }
            if ("key=".equals(prefix) && c == ':') {
                break;
            }
            end++;
        }
        return line.substring(start, end);
    }

    private static String extractStringFromExtras(String trimmed, String prefix) {
        if (trimmed.startsWith(prefix)) {
            String value = trimmed.substring(prefix.length()).trim();
            int openParen = value.indexOf('(');
            int closeParen = value.lastIndexOf(')');
            if (openParen != -1 && closeParen != -1 && closeParen > openParen) {
                return value.substring(openParen + 1, closeParen);
            }
        }
        return null;
    }
}
