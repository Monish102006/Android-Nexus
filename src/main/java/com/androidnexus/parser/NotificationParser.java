package com.androidnexus.parser;

import com.androidnexus.model.Notification;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NotificationParser {

    private static final Pattern RECORD_PATTERN = Pattern.compile("NotificationRecord\\{[^\\}]*key=([^\\s\\}]+)");
    private static final Pattern PKG_ID_PATTERN = Pattern.compile("pkg=([^\\s]+)\\s+user=\\d+\\s+id=([^\\s]+)\\s+tag=([^\\s]+)");
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
            if (trimmed.contains("NotificationRecord{")) {
                if (current != null && current.getKey() != null) {
                    notifications.add(current);
                }
                current = new Notification();
                Matcher m = RECORD_PATTERN.matcher(trimmed);
                if (m.find()) {
                    current.setKey(m.group(1));
                }
                continue;
            }

            if (current == null) {
                continue;
            }

            // 2. Parse pkg, id, tag details
            if (trimmed.startsWith("pkg=")) {
                Matcher m = PKG_ID_PATTERN.matcher(trimmed);
                if (m.find()) {
                    current.setPackageName(m.group(1));
                    try {
                        current.setId(Integer.parseInt(m.group(2)));
                    } catch (NumberFormatException e) {
                        // ignore
                    }
                    String tag = m.group(3);
                    current.setTag("null".equals(tag) ? null : tag);
                }
                continue;
            }

            // 3. Parse flags (for ongoing and clearable status)
            if (trimmed.startsWith("flags=")) {
                // e.g. flags=0x10 or flags=0x62
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
            } else if (trimmed.startsWith("text=")) {
                current.setText(trimmed.substring("text=".length()).trim());
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
}
