package br.com.scmjf.tihelper;

import java.io.IOException;

import br.com.scmjf.tihelper.util.SceneUtil;
import br.com.scmjf.tihelper.util.TrayService;
import br.com.scmjf.tihelper.util.AppInfo;
import br.com.scmjf.tihelper.util.AppContext;
import br.com.scmjf.tihelper.util.AppLogger;
import br.com.scmjf.tihelper.util.AlertUtil;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MainApp extends Application {

    private static final String APP_TITLE = AppInfo.NAME;

    @Override
    public void init() {
        AppLogger.configure();
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            AppLogger.error("Erro não tratado na thread " + thread.getName(), throwable);
            try {
                Platform.runLater(() -> AlertUtil.error("Erro inesperado",
                        "Ocorreu um erro inesperado. Consulte o log local para detalhes."));
            } catch (IllegalStateException ignored) {
                // JavaFX ainda pode não estar inicializado.
            }
        });
    }

    @Override
    public void start(Stage stage) throws IOException {
        AppLogger.info("Início da aplicação " + AppInfo.VERSION + ".");
        AppContext.dataDirectory();
        Parent root = loadView("login.fxml");
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle(APP_TITLE);
        stage.setMinWidth(860);
        stage.setMinHeight(600);
        stage.setScene(SceneUtil.createScene(stage, root, 1080, 720));

        TrayService.install(stage);
        Platform.setImplicitExit(false);
        stage.setOnCloseRequest(event -> {
            event.consume();
            TrayService.requestClose(stage);
        });

        stage.show();
    }

    @Override
    public void stop() {
        AppLogger.info("Fechamento da aplicação.");
        TrayService.shutdown();
    }

    private Parent loadView(String viewName) throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass()
                    .getResource("/br/com/scmjf/tihelper/view/" + viewName));
            return loader.load();
        } catch (IOException exception) {
            AppLogger.error("Erro ao carregar FXML: " + viewName, exception);
            throw exception;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
