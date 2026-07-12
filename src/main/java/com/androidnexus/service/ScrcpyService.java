package com.androidnexus.service;

import com.androidnexus.adb.CommandExecutor;

public class ScrcpyService {

    public static void launch() {

        CommandExecutor.executeProcess(
                "E:\\Android-Control-Center\\scrcpy\\scrcpy.exe"
        );

    }

}