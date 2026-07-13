package com.androidnexus.service;
import com.androidnexus.utils.Constants;

import com.androidnexus.adb.CommandExecutor;

public class ScrcpyService {

    public static void launch() {

        CommandExecutor.executeProcess(
                Constants.SCRCPY_PATH
        );

    }

}