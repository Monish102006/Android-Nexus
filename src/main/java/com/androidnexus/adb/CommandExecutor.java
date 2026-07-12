package com.androidnexus.adb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CommandExecutor {

    public static String executeCommand(String... command) {

        try {

            ProcessBuilder processBuilder =
                    new ProcessBuilder(command);

            Process process = processBuilder.start();

            StringBuilder output = new StringBuilder();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );

            String line;

            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            return output.toString();

        } catch (IOException e) {

            return "ERROR: " + e.getMessage();

        }

    }

}