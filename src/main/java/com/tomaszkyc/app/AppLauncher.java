package com.tomaszkyc.app;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AppLauncher {

    private static final Logger log = LogManager.getLogger(AppLauncher.class.getName());

    public static void main(String[] args) {
        log.debug("Application is starting...");
        MainApp.main(args);
        log.debug("Application closed.");
    }
}
