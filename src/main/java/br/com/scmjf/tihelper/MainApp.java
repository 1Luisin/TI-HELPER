package br.com.scmjf.tihelper;

import java.io.IOException;
import java.util.Objects;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    private static final String APP_TITLE = "TI Helper - SCMJF";

    @Override
    public void start(Stage stage) throws IOException {
        Parent root = loadView("login.fxml");
        Scene scene = new Scene(root, 1080, 720);
        scene.getStylesheets().add(Objects.requireNonNull(getClass()
                .getResource("/br/com/scmjf/tihelper/styles/app.css")).toExternalForm());

        stage.setTitle(APP_TITLE);
        stage.setMinWidth(980);
        stage.setMinHeight(640);
        stage.setScene(scene);
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
