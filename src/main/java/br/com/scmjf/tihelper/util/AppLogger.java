package br.com.scmjf.tihelper.util;

import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public final class AppLogger {

    private static final Logger LOGGER = Logger.getLogger(AppInfo.NAME);
    private static boolean configured;

    private AppLogger() {
    }

    public static synchronized void configure() {
        if (configured) {
            return;
        }

        try {
            Files.createDirectories(AppPaths.logsDirectory());
            FileHandler handler = new FileHandler(AppPaths.logFile().toString(), true);
            handler.setEncoding("UTF-8");
            handler.setFormatter(new CompactFormatter());
            LOGGER.setUseParentHandlers(false);
            LOGGER.addHandler(handler);
            LOGGER.setLevel(Level.INFO);
            configured = true;
        } catch (IOException exception) {
            LOGGER.log(Level.SEVERE, "Não foi possível configurar o log local.", exception);
        }
    }

    public static void info(String message) {
        configure();
        LOGGER.info(message);
    }

    public static void warning(String message) {
        configure();
        LOGGER.warning(message);
    }

    public static void error(String message, Throwable throwable) {
        configure();
        LOGGER.log(Level.SEVERE, message, throwable);
    }

    private static final class CompactFormatter extends Formatter {

        @Override
        public String format(LogRecord record) {
            String thrown = "";
            if (record.getThrown() != null) {
                thrown = System.lineSeparator() + record.getThrown();
            }
            return "%1$tF %1$tT [%2$s] %3$s%4$s%n".formatted(
                    record.getMillis(),
                    record.getLevel().getName(),
                    formatMessage(record),
                    thrown);
        }
    }
}
