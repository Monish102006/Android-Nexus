package com.androidnexus.parser;

import com.androidnexus.model.AndroidFile;
import java.util.ArrayList;
import java.util.List;

public class AndroidFileParser {

    /**
     * Parses the output of "adb shell ls -l <directory>" into a list of AndroidFile objects.
     *
     * Format expected:
     * permissions links owner group size date time name [-> target]
     * e.g., -rw-rw---- 1 root sdcard_rw 1048576 2024-01-15 10:30 document.pdf
     * or drwxrwx--x 2 root sdcard_rw 4096 2024-01-15 10:30 Photos
     * or lrwxrwxrwx 1 root root 21 2024-01-15 10:30 sdcard -> /storage/emulated/0
     */
    public static List<AndroidFile> parse(String output, String directory) {
        List<AndroidFile> files = new ArrayList<>();
        if (output == null || output.isEmpty()) {
            return files;
        }

        String[] lines = output.split("\n");

        for (String line : lines) {
            line = line.trim();

            if (line.isEmpty() || line.startsWith("total")) {
                continue;
            }

            String[] parts = line.split("\\s+");

            // Standard ls -l output has at least 8 parts:
            // 0: permissions, 1: links, 2: owner, 3: group, 4: size, 5: date, 6: time, 7+: name
            if (parts.length < 8) {
                continue;
            }

            try {
                AndroidFile file = new AndroidFile();

                // 1. Permissions
                String perm = parts[0];
                file.setPermissions(perm);
                file.setDirectory(perm.startsWith("d"));
                boolean isLink = perm.startsWith("l");
                file.setSymlink(isLink);

                // 2. Owner & Group
                file.setOwner(parts[2]);
                file.setGroup(parts[3]);

                // 3. Size
                long size = Long.parseLong(parts[4]);
                file.setSize(size);
                file.setHumanReadableSize(toHumanReadableSize(size));

                // 4. Last Modified
                file.setLastModified(parts[5] + " " + parts[6]);

                // 5. Name (handles spaces and symlinks)
                StringBuilder rawName = new StringBuilder();
                for (int i = 7; i < parts.length; i++) {
                    rawName.append(parts[i]);
                    if (i != parts.length - 1) {
                        rawName.append(" ");
                    }
                }

                String fullName = rawName.toString();
                String baseName;
                String target = null;
                
                if (isLink && fullName.contains(" -> ")) {
                    String[] nameParts = fullName.split(" -> ", 2);
                    baseName = nameParts[0].trim();
                    target = nameParts[1].trim();
                } else {
                    baseName = fullName;
                }

                if (baseName.contains("/")) {
                    int lastSlash = baseName.lastIndexOf('/');
                    baseName = baseName.substring(lastSlash + 1);
                }

                file.setName(baseName);
                file.setSymlinkTarget(target);

                // 6. Path & Extension
                String pathPart = fullName;
                if (isLink && pathPart.contains(" -> ")) {
                    pathPart = pathPart.split(" -> ", 2)[0].trim();
                }

                if (pathPart.startsWith("/")) {
                    file.setPath(pathPart);
                } else {
                    file.setPath(directory.endsWith("/") ? directory + file.getName() : directory + "/" + file.getName());
                }

                if (file.isDirectory()) {
                    file.setExtension("");
                } else {
                    file.setExtension(getFileExtension(file.getName()));
                }

                files.add(file);

            } catch (Exception e) {
                // Skip malformed lines instead of throwing to be resilient
                // System.err.println("Skipping malformed line: " + line + " Reason: " + e.getMessage());
            }
        }

        return files;
    }

    private static String toHumanReadableSize(long size) {
        if (size <= 0) return "0 B";
        if (size < 1024) return size + " B";
        int exp = (int) (Math.log(size) / Math.log(1024));
        char pre = "KMGTPE".charAt(exp - 1);
        return String.format("%.1f %cB", size / Math.pow(1024, exp), pre);
    }

    private static String getFileExtension(String name) {
        if (name == null) return "";
        int dotIndex = name.lastIndexOf('.');
        if (dotIndex == -1 || dotIndex == name.length() - 1 || dotIndex == 0) {
            return "";
        }
        return name.substring(dotIndex + 1).toLowerCase();
    }
}