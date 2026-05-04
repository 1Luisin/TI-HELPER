package br.com.scmjf.tihelper;

import java.io.IOException;

import br.com.scmjf.tihelper.util.SceneUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MainApp extends Application {

    private static final String APP_TITLE = "TI Helper - SCMJF";

    @Override
    public void start(Stage stage) throws IOException {
        Parent root = loadView("login.fxml");
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle(APP_TITLE);
        stage.setMinWidth(860);
        stage.setMinHeight(600);
        stage.setScene(SceneUtil.createScene(stage, root, 1080, 720));
        stage.show();
    }

    private Parent loadView(String viewName) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass()
                .getResource("/br/com/scmjf/tihelper/view/" + viewName));
        return loader.load();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
