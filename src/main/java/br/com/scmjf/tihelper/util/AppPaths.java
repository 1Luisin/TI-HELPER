package br.com.scmjf.tihelper.util;

import java.nio.file.Path;

public final class AppPaths {

    private static final String APP_FOLDER = "TI Helper - SCMJF";

    private AppPaths() {
    }

    public static Path appDirectory() {
        String localAppData = System.getenv("LOCALAPPDATA");
        if (localAppData != null && !localAppData.isBlank()) {
            return Path.of(localAppData, APP_FOLDER);
        }

        return Path.of(System.getProperty("user.home"), "AppData", "Local", APP_FOLDER);
    }

    public static Path dataDirectory() {
        return appDirectory().resolve("data");
    }

    public static Path logsDirectory() {
        return appDirectory().resolve("logs");
    }

    public static Path logFile() {
        return logsDirectory().resolve("app.log");
    }
}
