package com.androidnexus.parser;

import com.androidnexus.model.AndroidFile;

import java.util.ArrayList;
import java.util.List;

public class AndroidFileParser {

    public static List<AndroidFile> parse(String output, String directory) {

        List<AndroidFile> files = new ArrayList<>();

        String[] lines = output.split("\n");

        for (String line : lines) {

            line = line.trim();

            if (line.isEmpty() || line.startsWith("total")) {
                continue;
            }

            String[] parts = line.split("\\s+");

            if (parts.length < 8) {
                continue;
            }

            AndroidFile file = new AndroidFile();

            file.setDirectory(parts[0].startsWith("d"));

            file.setSize(Long.parseLong(parts[4]));

            StringBuilder fileName = new StringBuilder();

            for (int i = 7; i < parts.length; i++) {

                fileName.append(parts[i]);

                if (i != parts.length - 1) {
                    fileName.append(" ");
                }
            }

            file.setName(fileName.toString());

            file.setPath(directory + "/" + fileName);

            files.add(file);

        }

        return files;

    }

}