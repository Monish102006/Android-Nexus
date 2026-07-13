package com.androidnexus.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileNameGenerator {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    public static String generateScreenshotName() {

        return "Screenshot_" +
                LocalDateTime.now().format(FORMATTER) +
                ".png";
    }

}