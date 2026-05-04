package br.com.scmjf.tihelper.util;

import java.util.Objects;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public final class SceneUtil {

    private static final String STYLESHEET = "/br/com/scmjf/tihelper/styles/app.css";

    private SceneUtil() {
    }

    public static Scene createScene(Stage stage, Parent content, double width, double height) {
        Scene scene = new Scene(WindowChrome.wrap(stage, content), width, height);
        scene.getStylesheets().add(Objects.requireNonNull(SceneUtil.class.getResource(STYLESHEET)).toExternalForm());
        return scene;
    }
}
