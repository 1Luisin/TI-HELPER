package br.com.scmjf.tihelper;

import br.com.scmjf.tihelper.util.AppLogger;
import br.com.scmjf.tihelper.util.AlertUtil;
import javafx.application.Platform;

public final class Launcher {

    private Launcher() {
    }

    public static void main(String[] args) {
        AppLogger.configure();
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            AppLogger.error("Erro nao tratado na thread " + thread.getName(), throwable);
            try {
                Platform.runLater(() -> AlertUtil.error("Erro inesperado",
                        "Ocorreu um erro inesperado. Consulte o log local para detalhes."));
            } catch (IllegalStateException ignored) {
                // JavaFX ainda pode nao estar inicializado.
            }
        });
        MainApp.main(args);
    }
}
